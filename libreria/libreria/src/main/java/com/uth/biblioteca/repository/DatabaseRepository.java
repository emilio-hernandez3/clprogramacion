package com.uth.biblioteca.repository;

import com.uth.biblioteca.data.LibrosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface DatabaseRepository {

	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@GET("/pls/apex/ingenieria_uth/appbiblioteca/libros")
	Call<LibrosResponse> getBooks();
	
	
}
