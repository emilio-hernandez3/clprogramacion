package com.uth.biblioteca.data;

import java.util.List;

public class AutoresResponse {
	private List<Autor> items;
	private int count;
	
	public List<Autor> getItems() {
		return items;
	}
	public void setItems(List<Autor> items) {
		this.items = items;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
