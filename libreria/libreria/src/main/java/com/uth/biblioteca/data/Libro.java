package com.uth.biblioteca.data;

import java.util.Date;

public class Libro  {

    private byte[] image;
    private String name;
    private String author;
    private String editorial;
    private Date publicationdate;
    private Integer pages;
    private String isbn;

    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Date getPublicationdate() {
        return publicationdate;
    }
    public void setPublicationdate(Date publicationdate) {
        this.publicationdate = publicationdate;
    }
    public Integer getPages() {
        return pages;
    }
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
	public String getEditorial() {
		return editorial;
	}
	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}

}
