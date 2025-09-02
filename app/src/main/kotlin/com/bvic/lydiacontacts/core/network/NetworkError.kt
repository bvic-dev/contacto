package com.bvic.lydiacontacts.core.network

import com.bvic.lydiacontacts.core.Error

enum class NetworkError : Error {
    RequestTimeout,
    Unauthorized,
    NoInternet,
    NotFound,
    ServerError,
    Serialization,
    Unknown,
}
