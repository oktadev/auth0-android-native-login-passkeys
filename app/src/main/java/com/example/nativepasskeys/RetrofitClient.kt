import com.example.nativepasskeys.AuthenticationAPI
import com.example.nativepasskeys.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    public val BASE_URL = "https://login.carlastabile.tech/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: AuthenticationAPI by lazy {
        RetrofitClient.retrofit.create(AuthenticationAPI::class.java)
    }
}