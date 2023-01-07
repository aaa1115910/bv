package dev.aaa1115910.bv.network

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import kotlin.test.Test

internal class AppCenterApiTest {

    companion object {
        private const val ownerName = "aaa1115910-gmail.com"
        private const val appName = "bv"
        private const val distributionGroupName = "public"
        private const val distributionGroupId = "9259f371-d475-4088-b9fe-e5adfac1b563"
        private const val releaseId = 18
    }

    @Test
    fun `check update available packages`() {
        runBlocking {
            println(
                AppCenterApi.getPackageList(
                    ownerName = ownerName,
                    appName = appName,
                    distributionGroupName = distributionGroupName
                )
            )
        }
    }

    @Test
    fun `get update package info`() {
        runBlocking {
            println(
                AppCenterApi.getPackageInfo(
                    ownerName = ownerName,
                    appName = appName,
                    distributionGroupName = distributionGroupName,
                    releaseId = releaseId
                )
            )
        }
    }

    @Test
    fun `send install analytics`() {
        assertDoesNotThrow {
            runBlocking {
                AppCenterApi.sendInstallAnalytics(
                    ownerName = ownerName,
                    appName = appName,
                    distributionGroupId = distributionGroupId,
                    releaseId = releaseId
                )
            }
        }
    }
}