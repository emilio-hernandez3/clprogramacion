package com.uth.biblioteca.repository;

import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DatabaseClient {

	//OBJETO QUE PERMITE LA CONSTRUCCIÓN DE LA CONEXIÓN AL SERVICIO DE BASE DE DATOS EN INTERNET
	private Retrofit retrofit;
	//OBJETO QUE PERMITE VER EN LA CONSOLA EL DETALLE DE LA PETICIÓN A LA BASE DE DATOS EN UNA FORMA ENTENDIBLE
	private HttpLoggingInterceptor interceptor;
	
	public DatabaseClient(String url, Long timeout) {
		interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		
		OkHttpClient cliente = new OkHttpClient.Builder()
				.addInterceptor(interceptor)
				.connectTimeout(timeout, TimeUnit.MILLISECONDS)
				.writeTimeout(timeout, TimeUnit.MILLISECONDS)
				.readTimeout(timeout, TimeUnit.MILLISECONDS)
				.build();
		
		retrofit = new Retrofit.Builder()
				.client(cliente)
				.baseUrl(url)
				.addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").create()))
				.build();
		
	}
	
	public DatabaseRepository getClient() {
		return retrofit.create(DatabaseRepository.class);
	}
}
