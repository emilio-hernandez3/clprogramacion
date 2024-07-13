package com.uth.biblioteca.repository;

import java.io.IOException;

import com.uth.biblioteca.data.AutoresResponse;
import com.uth.biblioteca.data.LibrosResponse;
import retrofit2.Call;
import retrofit2.Response;

public class DatabaseRepositoryImpl {
	
	private static DatabaseRepositoryImpl INSTANCE;
	private DatabaseClient client;
	
	private DatabaseRepositoryImpl(String url, Long timeout) {
		client = new DatabaseClient(url, timeout);
	}
	//PATRON SINGLETON
	public static DatabaseRepositoryImpl getInstance(String url, Long timeout) {
		if(INSTANCE == null) {
			synchronized (DatabaseRepositoryImpl.class) {
				if(INSTANCE == null) {
					INSTANCE = new DatabaseRepositoryImpl(url, timeout);
				}
			}
		}
		return INSTANCE;
	}

	
	public LibrosResponse consultarLibros() throws IOException {
		//AQUI LE DIGO QUE OPERACIÓN QUIERO QUE EJECUTE
		Call<LibrosResponse> call = client.getClient().getBooks();
		Response<LibrosResponse> response = call.execute();//AQUI ES DONDE LLAMO A LA BASE DE DATOS
		if(response.isSuccessful()) {
			return response.body();
		}else {
			return null;
		}
	}
	
	public AutoresResponse consultarAutores() throws IOException {
		//AQUI LE DIGO QUE OPERACIÓN QUIERO QUE EJECUTE
		Call<AutoresResponse> call = client.getClient().getAutores();
		Response<AutoresResponse> response = call.execute();//AQUI ES DONDE LLAMO A LA BASE DE DATOS
		if(response.isSuccessful()) {
			return response.body();
		}else {
			return null;
		}
	}
}
