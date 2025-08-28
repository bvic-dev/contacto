package com.bvic.lydiacontacts.core

enum class NetworkError : Error {
    RequestTimeout,
    Unauthorized,
    NoInternet,
    NotFound,
    ServerError,
    Serialization,
    Unknown,
}
