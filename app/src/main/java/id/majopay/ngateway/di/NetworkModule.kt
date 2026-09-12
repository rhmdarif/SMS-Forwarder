package id.majopay.ngateway.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.majopay.ngateway.BuildConfig
import id.majopay.ngateway.data.remote.api.ForwardingApiService
import id.majopay.ngateway.domain.model.ApiCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Provide Gson instance for JSON serialization/deserialization.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    /**
     * Provide HTTP logging interceptor for debugging.
     *
     * Hanya aktif di build debug. Di release level = NONE supaya URL (yang memuat api_key),
     * header, dan body tidak pernah tercetak ke logcat. Header rahasia di-redact bahkan di
     * debug agar api_secret tidak bocor lewat log yang di-share saat debugging.
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader(ApiCredentials.SECRET_HEADER)
            redactHeader("Authorization")
            redactHeader("Cookie")
        }
    }
    
    /**
     * Provide OkHttpClient with timeouts and logging.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Provide Retrofit instance.
     * Using ScalarsConverterFactory for String body requests.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.placeholder.com/") // Placeholder base URL
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Provide ForwardingApiService.
     */
    @Provides
    @Singleton
    fun provideForwardingApiService(retrofit: Retrofit): ForwardingApiService {
        return retrofit.create(ForwardingApiService::class.java)
    }
} 