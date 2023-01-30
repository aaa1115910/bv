package dev.aaa1115910.biliapi.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchCost(
    @SerialName("params_check")
    val paramsCheck: String,
    @SerialName("hotword_request")
    val hotwordRequest: String? = null,
    @SerialName("hotword_request_format")
    val hotwordRequestFormat: String? = null,
    @SerialName("hotword_response_format")
    val hotwordResponseFormat: String? = null,
    @SerialName("deserialize_response")
    val deserializeResponses: String? = null,
    val total: String,
    @SerialName("main_handler")
    val mainHandler: String? = null,
)