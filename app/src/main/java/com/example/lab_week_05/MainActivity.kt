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
import android.widget.ImageView
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.lab_week_05.model.CatBreedData


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

    private val imageResultView: ImageView by lazy {
        findViewById(R.id.image_result)
    }
    private val imageLoader: ImageLoader by lazy {
        GlideLoader(this)
    }

    private val breedInfoTextView: TextView by lazy{
        findViewById(R.id.breed_info_textview)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCatImageResponse()
        getCatBreedsResponse()
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
//                    val firstImage = image?.firstOrNull()?.imageUrl ?: "No URL"
                    val firstImage = image?.firstOrNull()?.imageUrl.orEmpty()
                    if (firstImage.isNotBlank()) {
                        imageLoader.loadImage(firstImage, imageResultView)
                    } else {
                        Log.d(MAIN_ACTIVITY, "Missing image URL")
                    }

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

    private fun getCatBreedsResponse() {
        val call = catApiService.getBreeds()
        call.enqueue(object : Callback<List<CatBreedData>> {
            override fun onFailure(call: Call<List<CatBreedData>>, t: Throwable) {
                Log.e(TAG, "Failed to get cat breeds", t)
                breedInfoTextView.text = "Unknown"
            }

            override fun onResponse(
                call: Call<List<CatBreedData>>,
                response: Response<List<CatBreedData>>
            ) {
                if (response.isSuccessful) {
                    val breeds: List<CatBreedData>? = response.body()
                    if (breeds != null && breeds.isNotEmpty()) {
                        val randomBreed = breeds.randomOrNull()
                        if (randomBreed != null) {
                            val breedInfo =
                                "Breed Name: ${randomBreed.name.orEmpty()}\n" +
                                        "Temperament: ${randomBreed.temperament.orEmpty()}\n"
                            breedInfoTextView.text = breedInfo
                            Log.d(TAG, "Breeds received: ${randomBreed.name.orEmpty()}")
                        } else {
                            Log.w(TAG, "Could not select a random breed")
                            breedInfoTextView.text = "Could not select a random breed"
                        }
                    } else if(breeds != null && breeds.isEmpty()){
                        Log.d(TAG, "No breeds found in the API response")
                        breedInfoTextView.text = "No breed cat found"
                    } else {  //all breeds == null
                        Log.w(TAG, "Breed response successful but body is null or invalid.")
                        breedInfoTextView.text = "Breed data is unavailable"
                    }
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    Log.e(
                        TAG, "Failed to get breeds response. Code: ${response.code()}\n" +
                                errorBody
                    )
                    apiResponseView.append("\n\nError fetching breeds: ${response.code()}")
                }
            }
        })
    }


    companion object{
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
        const val TAG = "CAT_BREED"
    }
}

