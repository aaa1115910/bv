package com.origeek.imageViewer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2023-05-24 12:15
 **/
class Ticket {

    private var ticket by mutableStateOf("")

    private val ticketMap = mutableMapOf<String, Continuation<Unit>>()

    suspend fun awaitNextTicket() = suspendCoroutine<Unit> { c ->
        ticket = UUID.randomUUID().toString()
        ticketMap[ticket] = c
    }

    private fun clearTicket() {
        ticketMap.forEach {
            it.value.resume(Unit)
            ticketMap.remove(it.key)
        }
    }

    @Composable
    fun Next() {
        LaunchedEffect(ticket) {
            clearTicket()
        }
    }

}