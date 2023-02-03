package com.hbl.amc.domain.datasource

/**
 * Custom result object.
 */
class LiveDataWrapper<T>(
    val responseStatus: Status,
    val response: T? = null,
    val error: Throwable? = null
) {

    enum class Status {
        SUCCESS, LOADING, ERROR
    }

    companion object {
        fun <T> loading() = LiveDataWrapper<T>(Status.LOADING)
        fun <T> success (data: T) = LiveDataWrapper<T>(Status.SUCCESS, data)
        fun <T> error(err: Throwable) = LiveDataWrapper<T>(Status.ERROR, null, err)
    }
}