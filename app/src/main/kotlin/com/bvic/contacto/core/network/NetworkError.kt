package com.bvic.contacto.core.network

import com.bvic.contacto.core.Error

enum class NetworkError : Error {
    RequestTimeout,
    Unauthorized,
    NoInternet,
    NotFound,
    ServerError,
    Serialization,
    Unknown,
}
