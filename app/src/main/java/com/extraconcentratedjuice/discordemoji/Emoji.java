package com.extraconcentratedjuice.discordemoji;

import java.util.HashMap;


public class Emoji {
    public static HashMap<Integer, String> categories = new HashMap<>();

    public int id;
    public String title;
    public String description;
    public String slug;
    public int category;
    public String author;

    public Emoji(int id, String title, String slug, String description, int category, String author)
    {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.category = category;
        this.author = author;
    }

    public String CategoryName()
    {
        return categories.get(this.category);
    }


}
