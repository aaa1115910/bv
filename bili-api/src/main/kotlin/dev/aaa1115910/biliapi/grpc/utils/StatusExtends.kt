package dev.aaa1115910.biliapi.grpc.utils

import bilibili.rpc.Status
import com.google.protobuf.Message
//import com.google.rpc.Status
import io.grpc.Metadata
import io.grpc.StatusException

// 如果没有设置 java_multiple_files = true; 那么就需要使用下面的代码
// 这时候生成的代码会将很多 class 放在同一个 class 里面
// 例如 bilibili.rpc.Status 会被放在 bilibili.rpc.StatusOuterClass$Status 中
// 这时候直接从 typeUrl 中获取 class 名称是不对的
/*
@Suppress("UNCHECKED_CAST")
fun Status.getTypeClass(): Class<Message> {
    val protoClassName = this.detailsList.first().typeUrl.split("/").last()
    val splitProtoClassNames = protoClassName.split(".")
    val nameClass = splitProtoClassNames
        .subList(0, splitProtoClassNames.size - 1)
        .joinToString(".") +
            ".${splitProtoClassNames.last()}OuterClass$${splitProtoClassNames.last()}"
    return Class.forName(nameClass) as Class<Message>
}
*/

@Suppress("UNCHECKED_CAST")
fun Status.getTypeClass(): Class<Message> {
    val nameClass = this.detailsList.first().typeUrl.split("/").last()
    return Class.forName(nameClass) as Class<Message>
}

fun Status.getDetail(): Any {
    val clazz = this.getTypeClass()
    return this.detailsList.first().unpack(clazz)
}

fun handleGrpcException(it: Throwable) {
    when (it) {
        is StatusException -> {
            val statusDetailsKey = Metadata.Key.of(
                "grpc-status-details-bin", Metadata.BINARY_BYTE_MARSHALLER
            )
            val data = it.trailers[statusDetailsKey]
            val status = Status.parseFrom(data).getDetail()
            when (status) {
                is bilibili.rpc.Status -> {
                    throw IllegalStateException(status.message)
                }

                is common.ErrorProto -> {
                    throw IllegalStateException(status.message)
                }
            }
        }

        else -> throw it
    }
}