package com.uth.biblioteca.data;

import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class LibrosReport implements JRDataSource {
	
	private List<Libro> data;
	private int counter = -1;
	private int maxCounter = 0;

	public List<Libro> getData() {
		return data;
	}

	public void setData(List<Libro> libros) {
		this.data = libros;
		this.maxCounter = this.data.size()-1;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getMaxCounter() {
		return maxCounter;
	}

	public void setMaxCounter(int maxCounter) {
		this.maxCounter = maxCounter;
	}

	@Override
	public boolean next() throws JRException {
		if(counter < maxCounter) {
			counter++;
			return true;//AUN HAY DATOS QUE IMPRIMIR
		}
		return false;//YA NO HAY MÁS DATOS QUE IMPRIMIR
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		//PERMITE LLENAR LAS VARIABLES (FIELDS) DEFINIDAS EN NUESTRO REPORTE
		if("isbn".equals(jrField.getName())) {
			return data.get(counter).getIsbn();
		}else if("nombre".equals(jrField.getName())) {
			return data.get(counter).getName();
		}else if("autor".equals(jrField.getName())) {
			return data.get(counter).getAuthor();
		}else if("fecha".equals(jrField.getName())) {
			return data.get(counter).getPublicationdate().toString();
		}
		return "";
	}
}
