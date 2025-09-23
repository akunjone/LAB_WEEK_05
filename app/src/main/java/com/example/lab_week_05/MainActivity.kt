package com.example.lab_week_05

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab_week_05.*
import com.example.lab_week_05.api.CatApiService
import retrofit2.Callback
import android.util.Log
import com.example.lab_week_05.model.ImageData
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : AppCompatActivity() {
//    private val retrofit by lazy{
//        Retrofit.Builder()
//            .baseUrl("https://api.thecatapi.com/v1/")
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .build()
//    }

    private val moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val catApiService by lazy{
        retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy{
        findViewById(R.id.api_response)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCatImageResponse()
    }

//    private fun getCatImageResponse() {
//        val call = catApiService.searchImages(1, "full")
//        call.enqueue(object: Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable){
//                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
//            }
//            override fun onResponse(call: Call<String>, response: Response<String>){
//                if (response.isSuccessful){
//                    apiResponseView.text = response.body()
//                }
//                else{
//                    Log.e(MAIN_ACTIVITY, "Failed to get response\n"+
//                            response.errorBody()?.string().orEmpty()
//                    )
//                }
//            }
//        })
//    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object: Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
            }
            override fun onResponse(call: Call<List<ImageData>>,
                                    response: Response<List<ImageData>>) {
                if(response.isSuccessful){
                    val image = response.body()
                    val firstImage = image?.firstOrNull()?.imageUrl ?: "No URL"
                    apiResponseView.text = getString(R.string.image_placeholder,
                        firstImage)
                }
                else{
                    Log.e(MAIN_ACTIVITY, "Failed to get response\n" +
                            response.errorBody()?.string().orEmpty()
                    )
                }
            }
        })
    }


    companion object{
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}

