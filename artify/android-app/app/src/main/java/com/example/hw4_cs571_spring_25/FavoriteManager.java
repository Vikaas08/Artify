package com.example.hw4_cs571_spring_25;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.Set;

public class FavoriteManager {
    private static final String PREF_NAME = "favorites_pref";
    private static final String FAVORITES_KEY = "favorites";
    private static final String FAVORITE_DETAILS_KEY = "favorite_details";


    public static void toggleFavorite(Context context, String artistId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(FAVORITES_KEY, new HashSet<>()));

        if (favorites.contains(artistId)) {
            favorites.remove(artistId);
        } else {
            favorites.add(artistId);
        }

        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply();
    }

    public static boolean isFavorited(Context context, String artistId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(FAVORITES_KEY, new HashSet<>());
        return favorites.contains(artistId);
    }

    public static Set<String> getAllFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(FAVORITES_KEY, new HashSet<>());
    }

    public static void addFavoriteDetails(Context context, String artistId, String name, String nationality, String birthday) {


        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long timestamp = System.currentTimeMillis();
//        String artistDetails = name + "|" + nationality + "|" + birthday;
        String combined = name + "||" + nationality + "||" + birthday + "||" + timestamp;
        editor.putString(artistId, combined);
        editor.apply();
    }

    public static void removeFavoriteDetails(Context context, String artistId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(artistId);
        editor.apply();
    }

    public static String[] getFavoriteDetails(Context context, String artistId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String artistDetails = prefs.getString(artistId, null);

        if (artistDetails != null) {
            String[] parts = artistDetails.split("\\|\\|");
            if (parts.length == 4) {
                return parts; // [name, nationality, birthday, timestamp]
            }
        }
        return null;
    }

    public static void clearAllFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(FAVORITES_KEY);

        Set<String> favorites = prefs.getStringSet(FAVORITES_KEY, new HashSet<>());
        for (String artistId : favorites) {
            editor.remove(artistId);
        }

        editor.apply();
    }

}
