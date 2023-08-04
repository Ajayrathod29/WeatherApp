package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
     private  val binding: ActivityMainBinding by lazy {
         ActivityMainBinding.inflate(layoutInflater)
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Osmanabad")
       searchCity()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun searchCity() {
     val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        fetchWeatherData(query)
                    }
                    return true

                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }


        })
  }

    private fun fetchWeatherData(cityName:String) {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build().create(ApiInterface::class.java)

        val retrofitData = retrofit.getWeatherData(cityName,"18a45f14ce53a3ffe5e0c5268e4edc35","metric")
        retrofitData.enqueue(object : Callback<WeatherData?> {
            override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val wild = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.temprature.text = "$temperature °C"
                    binding.Weather.text = condition
                    binding.maxtime.text = "Max Temp:$maxTemp °C"
                    binding.mintime.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.wild.text = "$wild m/s"
                    binding.sea.text = "$seaLevel hpa"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.cityname.text = "$cityName"

                 changeImageAccordingToWeatherCondition(condition)

                }
            }

            override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
            }
        })

    }

    private fun changeImageAccordingToWeatherCondition(conditions:String) {
        when(conditions){

            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds ","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rains" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate snow","Heavy Snow","Blizzards" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))


    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date()))


    }

    private fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
   }
}