package com.example.gamewishv20.Models;

/**
 * Created by Mozeeb on 22/04/2018.
 */

public class Game {

    private String name;
    private String summary;
    private String genre;
    private String imageUrl;
    private double rating;
    private String gameId;

    public Game() {

    }

    public Game(String name, String summary, String genre, double rating, String imageUrl) {
        this.name = name;
        this.summary = summary;
        this.genre = genre;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}

