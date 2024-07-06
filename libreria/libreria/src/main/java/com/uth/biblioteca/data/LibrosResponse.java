package com.uth.biblioteca.data;

import java.util.List;

public class LibrosResponse {
	private List<Libro> items;
	private int count;
	
	public List<Libro> getItems() {
		return items;
	}
	public void setItems(List<Libro> items) {
		this.items = items;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
