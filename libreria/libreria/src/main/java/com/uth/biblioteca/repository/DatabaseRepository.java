package com.uth.biblioteca.repository;

import com.uth.biblioteca.data.AutoresResponse;
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
	
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@GET("/pls/apex/ingenieria_uth/appbiblioteca/autores")
	Call<AutoresResponse> getAutores();
	
	//URL: https://apex.oracle.com/pls/apex/ingenieria_uth/appbiblioteca/libros
	//[1]: Hostname: https://apex.oracle.com
	//[2]: Route: /pls/apex/ingenieria_uth/appbiblioteca/libros
	
	///pls/apex/{WORKSPACE}/{MODULO}/{PLANTILLA}
	
}
