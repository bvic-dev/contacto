package com.bvic.contacto.core

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(
        val data: D,
    ) : Result<D, Nothing>

    data class Error<out E : com.bvic.contacto.core.Error>(
        val error: E,
    ) : Result<Nothing, E>

    data object Loading : Result<Nothing, Nothing>
}

inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> =
    when (this) {
        is Result.Error, is Result.Loading -> this
        is Result.Success -> {
            action(data)
            this
        }
    }

inline fun <T, E : Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> =
    when (this) {
        is Result.Error -> {
            action(error)
            this
        }

        is Result.Success, is Result.Loading -> this
    }

fun <T, E : Error> Result<T, E>.isLoading(): Boolean = this is Result.Loading
