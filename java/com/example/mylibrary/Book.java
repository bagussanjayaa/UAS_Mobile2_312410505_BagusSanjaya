package com.example.mylibrary;

public class Book {

    private int id;
    private String title;
    private String author;
    private String genre;
    private String status;
    private String cover;
    private boolean isFavorite;

    // ================= CONSTRUCTOR =================
    public Book(int id, String title, String author, String genre, String status, String cover, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.status = status;
        this.cover = cover;
        this.isFavorite = isFavorite;
    }

    // ================= GETTER =================
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getAuthor() {
        return author != null ? author : "";
    }

    public String getGenre() {
        return genre != null ? genre : "";
    }

    public String getStatus() {
        return status != null ? status : "";
    }

    public String getCover() {
        return cover != null ? cover : "";
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    // ================= SETTER =================
    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }

    // ⭐ OPTIONAL (biar lebih stabil saat reload data)
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}