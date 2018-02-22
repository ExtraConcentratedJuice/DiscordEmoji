package com.extraconcentratedjuice.discordemoji;

import java.util.HashMap;


public class Emoji {
    public static HashMap<Integer, String> categories = new HashMap<>();

    public int id;
    public String title;
    public String description;
    public String slug;
    public int category;
    public int favorites;
    public String author;

    public Emoji(int id, String title, String slug, String description, int category, String author, int favorites)
    {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.category = category;
        this.author = author;
        this.favorites = favorites;
    }

    public String CategoryName()
    {
        return categories.get(this.category);
    }


}
