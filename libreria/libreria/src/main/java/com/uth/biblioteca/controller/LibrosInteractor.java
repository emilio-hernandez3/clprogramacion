package com.uth.biblioteca.controller;

import com.uth.biblioteca.data.Libro;

public interface LibrosInteractor {
	void consultarLibros();
	void consultarAutores();
	void crearLibro(Libro nuevo);
	void actualizarLibro(Libro actual);
	void eliminarLibro(String isbn);
}
