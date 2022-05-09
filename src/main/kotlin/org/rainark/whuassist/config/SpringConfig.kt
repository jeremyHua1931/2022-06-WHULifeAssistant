package org.rainark.whuassist.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SpringConfig : WebMvcConfigurer {
    @Autowired
    lateinit var fastJsonParameterResolver : FastJsonParameterResolver
    @Autowired
    lateinit var jwtUserIdParameterResolver : JwtUserIdParameterResolver
    @Autowired
    lateinit var jwtUserParameterResolver : JwtUserParameterResolver
    @Autowired
    lateinit var jwtInterceptor : JwtInterceptor
    override fun addArgumentResolvers(argumentResolvers : MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(fastJsonParameterResolver)
        argumentResolvers.add(jwtUserIdParameterResolver)
        argumentResolvers.add(jwtUserParameterResolver)
        super.addArgumentResolvers(argumentResolvers)
    }

    override fun addInterceptors(registry : InterceptorRegistry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
        super.addInterceptors(registry)
    }

    override fun addResourceHandlers(registry : ResourceHandlerRegistry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
        super.addResourceHandlers(registry)
    }
}