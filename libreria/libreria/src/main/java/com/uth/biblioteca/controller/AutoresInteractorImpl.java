package com.uth.biblioteca.controller;

import com.uth.biblioteca.data.AutoresResponse;
import com.uth.biblioteca.repository.DatabaseRepositoryImpl;
import com.uth.biblioteca.views.autores.AutoresViewModel;

public class AutoresInteractorImpl implements AutoresInteractor {

	private DatabaseRepositoryImpl modelo;
	private AutoresViewModel vista;
	
	public AutoresInteractorImpl(AutoresViewModel vista) {
		super();
		this.vista = vista;
		this.modelo = DatabaseRepositoryImpl.getInstance("https://apex.oracle.com", 30000L);//TIEMPO EN MILISEGUNDOS
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
				this.vista.mostrarAutoresEnLista(respuesta.getItems());
			}
			
		}catch(Exception error) {
			//EL CODIGO QUE SE EJECUTA CUANDO HAY PROBLEMAS
			error.printStackTrace();
			this.vista.mostrarMensajeError("Ha ocurrido un problema, revisa tu conexión a internet");
		}
	}

	
	
}
