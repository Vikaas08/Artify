package com.example.hw4_cs571_spring_25;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SimilarArtistResponse {
    @SerializedName("_embedded")
    private Embedded embedded;

    public Embedded getEmbedded() {
        return embedded;
    }

    public static class Embedded {
        @SerializedName("artists")
        private List<Artist> artists;

        public List<Artist> getArtists() {
            return artists;
        }
    }

    public static class Artist {
        private String id;
        private String name;

        @SerializedName("_links")
        private Links links;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Links getLinks() {
            return links;
        }
    }

    public static class Links {

        private Link self;
        private Link thumbnail;
        public Link getSelf() {
            return self;
        }
        public Link getThumbnail() {
            return thumbnail;
        }
    }

    public static class Link {
        private String href;

        public String getHref() {
            return href;
        }
    }
}
