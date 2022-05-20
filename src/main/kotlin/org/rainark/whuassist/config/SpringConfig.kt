package org.rainark.whuassist.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
class SpringConfig : WebMvcConfigurer, WebSocketConfigurer {
    @Autowired
    lateinit var fastJsonParameterResolver : FastJsonParameterResolver
    @Autowired
    lateinit var jwtUserIdParameterResolver : JwtUserIdParameterResolver
    @Autowired
    lateinit var jwtUserParameterResolver : JwtUserParameterResolver
    @Autowired
    lateinit var jwtInterceptor : JwtInterceptor
    @Autowired
    lateinit var websocketHandler : JsonWebsocketHandler
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

    override fun registerWebSocketHandlers(registry : WebSocketHandlerRegistry) {
        registry.addHandler(websocketHandler, "/chat")
            .setAllowedOrigins("*")
    }

    @Bean
    fun taskScheduler() : TaskScheduler {
        val scheduling = ThreadPoolTaskScheduler()
        scheduling.poolSize = 10
        scheduling.initialize()
        return scheduling
    }
}