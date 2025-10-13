package com.example.hw4_cs571_spring_25;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoryResponse {
    @SerializedName("_embedded")
    private Embedded embedded;
    public Embedded getEmbedded() {
        return embedded;
    }
    public static class Embedded {
        @SerializedName("genes")
        private List<Category> genes;
        public List<Category> getCategories() {
            return genes;
        }
    }

    public static class Category {
        private String id;
        private String name;
        private String description;
        @SerializedName("_links")
        private Links links;

        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }
        public Links getLinks() {
            return links;
        }
    }

    public static class Links {
        @SerializedName("thumbnail")
        private Thumbnail thumbnail;
        public Thumbnail getThumbnail() {
            return thumbnail;
        }
    }

    public static class Thumbnail {
        @SerializedName("href")
        private String href;

        public String getHref() {
            return href;
        }
    }
}
