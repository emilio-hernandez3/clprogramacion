package com.uth.biblioteca.views.libros;

import java.util.List;

import com.uth.biblioteca.data.Autor;
import com.uth.biblioteca.data.Libro;

public interface LibrosViewModel {

	void mostrarLibrosEnGrid(List<Libro> items);
	void mostrarAutoresEnCombobox(List<Autor> items);
	
	void mostrarMensajeExito(String mensaje);
	void mostrarMensajeError(String mensaje);
}
