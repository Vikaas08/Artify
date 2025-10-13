package com.example.hw4_cs571_spring_25;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArtworkResponse {

    @SerializedName("_embedded")
    private Embedded embedded;

    public Embedded getEmbedded() {
        return embedded;
    }

    public static class Embedded {
        @SerializedName("artworks")
        private List<Artwork> artworks;

        public List<Artwork> getArtworks() {
            return artworks;
        }
    }

    public static class Artwork {
        private String id;
        private String title;

        @SerializedName("date")
        private String date;

        @SerializedName("_links")
        private Links links;

        public String getId() {
            return id;
        }
        public String getTitle() {
            return title;
        }
        public String getDate() {
            return date;
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
