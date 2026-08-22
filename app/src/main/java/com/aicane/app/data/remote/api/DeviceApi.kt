package com.aicane.app.data.remote.api

import com.aicane.app.data.remote.dto.device.DeviceResponse
import com.aicane.app.data.remote.dto.device.RegisterDeviceRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DeviceApi {
    @POST("api/v1/devices")
    suspend fun registerDevice(@Body request: RegisterDeviceRequest): DeviceResponse

    @GET("api/v1/devices")
    suspend fun getDevices(): List<DeviceResponse>
}
