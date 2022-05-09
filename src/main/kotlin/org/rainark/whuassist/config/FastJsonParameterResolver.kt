package org.rainark.whuassist.config

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import jdk.nashorn.internal.runtime.ParserException
import org.rainark.whuassist.exception.RequestException
import org.rainark.whuassist.exception.ResponseCode
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.rainark.whuassist.util.dateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

annotation class JsonParam(val name : String = ValueConstants.DEFAULT_NONE,
                           val default : String = ValueConstants.DEFAULT_NONE,
                           val required : Boolean = true)

@Component
class FastJsonParameterResolver : HandlerMethodArgumentResolver {
    private val logger = org.slf4j.LoggerFactory.getLogger(FastJsonParameterResolver::class.java)
    companion object {
        const val JSON_BUFFER_ATTRIBUTE = "_json_buffer"
    }
    override fun supportsParameter(parameter : MethodParameter) : Boolean {
        return parameter.hasParameterAnnotation(JsonParam::class.java)
    }

    override fun resolveArgument(
        parameter : MethodParameter,
        mavContainer : ModelAndViewContainer?,
        webRequest : NativeWebRequest,
        binderFactory : WebDataBinderFactory?
    ) : Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        if(request.contentType != "application/json")
            throw RequestException(ResponseCode.CONTENT_TYPE_REJECTED)
        val jsonStr : String
        if(request.getAttribute(JSON_BUFFER_ATTRIBUTE) == null) {
            jsonStr = request.reader.readText()
            request.setAttribute(JSON_BUFFER_ATTRIBUTE, jsonStr)
        } else {
            jsonStr = request.getAttribute(JSON_BUFFER_ATTRIBUTE) as String
        }
        val json = JSON.parseObject(jsonStr) ?: JSONObject()
        return resolveArgument0(json, SpringJsonParameter(parameter))
    }

    fun resolveArgument0(json : JSONObject, parameter : AbstractJsonParameter) : Any? {
        val defaultValue = parameter.getParameterAnnotation(JsonParam::class)!!.default
        val isRequired = parameter.getParameterAnnotation(JsonParam::class)!!.required &&
                defaultValue == ValueConstants.DEFAULT_NONE
        val name = parameter.getParameterAnnotation(JsonParam::class)!!.name
            .let { if(it == ValueConstants.DEFAULT_NONE) parameter.parameterName else it }
        if(isRequired && !json.containsKey(name))
            throw RequestException(ResponseCode.PARAMETER_MISSING, name)
        if(!json.containsKey(name)) return if(defaultValue == ValueConstants.DEFAULT_NONE) null else defaultValue
        return try {
            when(parameter.parameterType) {
                String::class.java -> json.getString(name)
                Int::class.java -> json.getInteger(name)
                java.lang.Integer::class.java -> json.getIntValue(name)
                Long::class.java -> json.getLong(name)
                java.lang.Long::class.java -> json.getLongValue(name)
                Double::class.java -> json.getDouble(name)
                Boolean::class.java -> json.getBooleanValue(name)
                Date::class.java ->
                    try {
                        dateFormat.parse(json.getString(name))
                    } catch (e : ParserException) {
                        throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "$name[${json.getString(name)}]")
                    }
                else -> {
                    logger.warn("Unsupported parameter type: ${parameter.parameterType}")
                    null
                }
            }
        } catch (e : JSONException) {
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "name[${json.getString(name)}]")
        }
    }
}

abstract class AbstractJsonParameter {
    abstract fun <A : Annotation> getParameterAnnotation(clazz : KClass<A>) : A?
    abstract val parameterType : Class<*>
    abstract val parameterName : String
}

class SpringJsonParameter(private val param : MethodParameter) : AbstractJsonParameter() {
    override val parameterName : String
        get() = param.parameterName!!
    override val parameterType : Class<*>
        get() = param.parameterType
    override fun <A : Annotation> getParameterAnnotation(clazz : KClass<A>) : A? {
        return param.getParameterAnnotation(clazz.java)
    }
}

class KotlinJsonParameter(private val param : KParameter) : AbstractJsonParameter() {
    override val parameterName : String
        get() = param.name!!
    override val parameterType : Class<*>
        get() = param.type.javaType as Class<*>
    override fun <A : Annotation> getParameterAnnotation(clazz : KClass<A>) : A? {
        return param.annotations.find { it.annotationClass == clazz } as A?
    }
}