package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.device.DeviceResponse
import com.aicane.app.data.remote.dto.device.DeviceStatusResponse
import com.aicane.app.data.remote.dto.device.RegisterDeviceRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceApi {
    @POST("api/v1/devices")
    suspend fun registerDevice(@Body request: RegisterDeviceRequest): DeviceResponse

    @GET("api/v1/devices")
    suspend fun getDevices(): List<DeviceResponse>

    @DELETE("api/v1/devices/{deviceId}")
    suspend fun deleteDevice(@Path("deviceId") deviceId: String)

    @GET("api/v1/devices/{deviceId}/status")
    suspend fun getDeviceStatus(@Path("deviceId") deviceId: String): DeviceStatusResponse
}
