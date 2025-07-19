package com.example.multibhashanews.utils

/*
    This code defines a sealed class called Resource<T> in Kotlin.
    It’s a wrapper class used to represent the state of a data operation (such as loading data from a network or database)
    — typically in apps using MVVM architecture and Retrofit/LiveData/Flow.
 */
/*
    A sealed class in Kotlin is used to restrict class hierarchies.

    All subclasses of a sealed class must be declared in the same file.

    This makes it useful for representing finite types/states.
 */

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
){
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String, data: T? = null):Resource<T>(data,message)
    class Loading<T>: Resource<T>()
}