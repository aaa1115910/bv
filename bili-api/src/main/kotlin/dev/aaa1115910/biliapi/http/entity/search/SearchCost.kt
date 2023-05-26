package dev.aaa1115910.biliapi.http.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchCost(
    @SerialName("params_check")
    val paramsCheck: String,
    @SerialName("is_risk_query")
    val isRiskQuery: String? = null,
    @SerialName("illegal_handler")
    val illegalHandler: String? = null,
    @SerialName("as_response_format")
    val asResponseFormat: String? = null,
    @SerialName("mysql_request")
    val mysqlRequest: String? = null,
    @SerialName("as_request")
    val asRequest: String? = null,
    @SerialName("save_cache")
    val saveCache: String? = null,
    @SerialName("as_request_format")
    val asRequestFormat: String? = null,
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