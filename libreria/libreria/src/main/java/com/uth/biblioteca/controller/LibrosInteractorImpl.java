package com.uth.biblioteca.controller;

import com.uth.biblioteca.data.AutoresResponse;
import com.uth.biblioteca.data.Libro;
import com.uth.biblioteca.data.LibrosResponse;
import com.uth.biblioteca.repository.DatabaseRepositoryImpl;
import com.uth.biblioteca.views.libros.LibrosViewModel;

public class LibrosInteractorImpl implements LibrosInteractor {

	private DatabaseRepositoryImpl modelo;
	private LibrosViewModel vista;
	
	public LibrosInteractorImpl(LibrosViewModel vista) {
		super();
		this.vista = vista;
		this.modelo = DatabaseRepositoryImpl.getInstance("https://apex.oracle.com", 30000L);//TIEMPO EN MILISEGUNDOS
	}

	@Override
	public void consultarLibros() {
		//ESTRUCTURA TRY...CATCH PARA MANEJO DE ERRORES
		try{
			//EL CODIGO QUE SE EJECUTA EN EL HAPPY PATH
			LibrosResponse respuesta = this.modelo.consultarLibros();
			if(respuesta == null || respuesta.getCount() == 0 || respuesta.getItems() == null) {
				this.vista.mostrarMensajeError("No hay libros que mostrar");
			}else {
				this.vista.mostrarLibrosEnGrid(respuesta.getItems());
			}
			
		}catch(Exception error) {
			//EL CODIGO QUE SE EJECUTA CUANDO HAY PROBLEMAS
			error.printStackTrace();
			this.vista.mostrarMensajeError("Ha ocurrido un problema, revisa tu conexión a internet");
		}
	}
	
	@Override
	public void crearLibro(Libro nuevo) {
		//ESTRUCTURA TRY...CATCH PARA MANEJO DE ERRORES
		try{
			//EL CODIGO QUE SE EJECUTA EN EL HAPPY PATH
			boolean creado = this.modelo.crearLibro(nuevo);
			if(creado) {
				this.vista.mostrarMensajeExito("Libro creado exitosamente!");
			}else {
				this.vista.mostrarMensajeError("Hubo un problema al crear el libro");
			}
			
		}catch(Exception error) {
			//EL CODIGO QUE SE EJECUTA CUANDO HAY PROBLEMAS
			error.printStackTrace();
			this.vista.mostrarMensajeError("Ha ocurrido un problema, revisa tu conexión a internet");
		}
	}
	
	@Override
	public void actualizarLibro(Libro existente) {
		//ESTRUCTURA TRY...CATCH PARA MANEJO DE ERRORES
		try{
			//EL CODIGO QUE SE EJECUTA EN EL HAPPY PATH
			boolean actualizado = this.modelo.actualizarLibro(existente);
			if(actualizado) {
				this.vista.mostrarMensajeExito("Libro modificado exitosamente!");
			}else {
				this.vista.mostrarMensajeError("Hubo un problema al modificar el libro");
			}
			
		}catch(Exception error) {
			//EL CODIGO QUE SE EJECUTA CUANDO HAY PROBLEMAS
			error.printStackTrace();
			this.vista.mostrarMensajeError("Ha ocurrido un problema, revisa tu conexión a internet");
		}
	}
	
	@Override
	public void consultarAutores() {
		//ESTRUCTURA TRY...CATCH PARA MANEJO DE ERRORES
		try{
			//EL CODIGO QUE SE EJECUTA EN EL HAPPY PATH
			AutoresResponse respuesta = this.modelo.consultarAutores();
			if(respuesta == null || respuesta.getCount() == 0 || respuesta.getItems() == null) {
				this.vista.mostrarMensajeError("No hay autores que mostrar");
			}else {
				this.vista.mostrarAutoresEnCombobox(respuesta.getItems());
			}
			
		}catch(Exception error) {
			//EL CODIGO QUE SE EJECUTA CUANDO HAY PROBLEMAS
			error.printStackTrace();
			this.vista.mostrarMensajeError("Ha ocurrido un problema, revisa tu conexión a internet");
		}
	}

	
	@Override
	public void eliminarLibro(String isbn) {
		//ESTRUCTURA TRY...CATCH PARA MANEJO DE ERRORES
		try{
			//EL CODIGO QUE SE EJECUTA EN EL HAPPY PATH
			boolean actualizado = this.modelo.eliminarLibro(isbn);
			if(actualizado) {
				this.vista.mostrarMensajeExito("Libro eliminado correctamente!");
			}else {
				this.vista.mostrarMensajeError("Hubo un problema al borrar el libro");
			}
			
		}catch(Exception error) {
			//EL CODIGO QUE SE EJECUTA CUANDO HAY PROBLEMAS
			error.printStackTrace();
			this.vista.mostrarMensajeError("Ha ocurrido un problema, revisa tu conexión a internet");
		}
	}
}
