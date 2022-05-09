package org.rainark.whuassist.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.text.SimpleDateFormat

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)
val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
fun InvocationTargetException.unpackCause(): Throwable = cause ?: this