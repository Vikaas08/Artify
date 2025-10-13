package com.example.hw4_cs571_spring_25;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    @SerializedName("_embedded")
    private Embedded embedded;

    public Embedded getEmbedded() {
        return embedded;
    }

    public static class Embedded {
        @SerializedName("results")
        private List<ResultItem> results;

        public List<ResultItem> getResults() {
            return results;
        }
    }

    public static class ResultItem {
        private String title;

        @SerializedName("_links")
        private Links links;

        public String getTitle() {
            return title;
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
