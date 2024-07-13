package com.uth.biblioteca.views.autores;

import java.util.List;

import com.uth.biblioteca.data.Autor;

public interface AutoresViewModel {
	void mostrarAutoresEnLista(List<Autor> items);
	void mostrarMensajeExito(String mensaje);
	void mostrarMensajeError(String mensaje);
}
