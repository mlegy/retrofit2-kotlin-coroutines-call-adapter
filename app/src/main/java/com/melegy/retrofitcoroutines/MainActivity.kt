package com.melegy.retrofitcoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.melegy.retrofitcoroutines.remote.NetworkResponse
import com.melegy.retrofitcoroutines.remote.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val moshi = Moshi.Builder().build()
        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit = createRetrofit(moshi, okHttpClient)
        val service = retrofit.create<ApiService>()

        GlobalScope.launch {
            val response1 = service.getSuccess()
            when (response1) {
                is NetworkResponse.Success -> Log.d(TAG, "Success ${response1.body.name}")
                is NetworkResponse.ApiError -> Log.d(TAG, "ApiError ${response1.body.message}")
                is NetworkResponse.NetworkError -> Log.d(TAG, "NetworkError")
                is NetworkResponse.UnknownError -> Log.d(TAG, "UnknownError")
            }

            val response2 = service.getError()
            when (response2) {
                is NetworkResponse.Success -> Log.d(TAG, "Success ${response2.body.name}")
                is NetworkResponse.ApiError -> Log.d(TAG, "ApiError ${response2.body.message}")
                is NetworkResponse.NetworkError -> Log.d(TAG, "NetworkError")
                is NetworkResponse.UnknownError -> Log.d(TAG, "UnknownError")
            }

        }
    }

    interface ApiService {
        @GET("success")
        suspend fun getSuccess(): NetworkResponse<Success, Error>

        @GET("error")
        suspend fun getError(): NetworkResponse<Success, Error>
    }

    private fun createRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://retroftcoroutines.free.beeceptor.com/")
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
