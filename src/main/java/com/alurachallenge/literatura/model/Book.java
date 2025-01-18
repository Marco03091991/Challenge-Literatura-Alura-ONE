package com.alurachallenge.literatura.model;


import jakarta.persistence.*;


@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;
    private String author;
    private String language;
    private Integer download_count;

    public Book(){}

    public Book(DataBook dataBook) {
        this.title = dataBook.title() != null ? dataBook.title() : "Titulo: N/A";
        this.author = (dataBook.authors() != null && !dataBook.authors().isEmpty())
                ? dataBook.authors().get(0).name()
                : "Autor: N/A";
        this.language = (dataBook.languages() != null && !dataBook.languages().isEmpty())
                ? dataBook.languages().get(0)
                : "Idioma: N/A";
        this.download_count = dataBook.download_count() != null ? dataBook.download_count() : 0;
    }

    public Integer getDownload_count() {
        return download_count;
    }

    public void setDownload_count(Integer download_count) {
        this.download_count = download_count;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
