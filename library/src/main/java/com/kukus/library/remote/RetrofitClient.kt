package com.kukus.customer.halper.remote

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitClient{

    companion object {
        private var retrofit : Retrofit? = null

        fun getClient(baseUrl : String) : Retrofit{

            if (retrofit == null){

                retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build()
            }

            return retrofit!!
        }
    }


}