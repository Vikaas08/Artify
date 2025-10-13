package com.example.hw4_cs571_spring_25;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SimilarFragment extends Fragment {
    private SimilarArtistsFragment similarArtistsFragment = new SimilarArtistsFragment();
    private String searchId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_similar, container, false);

        if (getArguments() != null) {
            searchId = getArguments().getString("searchId");
        }
        return view;
    }

    @Override

    public void onStart() {
        super.onStart();
        Log.d("SimilarFragment", "onStart called");
        if (searchId != null) {
            loadSimilarArtists();
        }
    }

    private void loadSimilarArtists() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SearchApi searchApi = retrofit.create(SearchApi.class);
        ArtistRequest request = new ArtistRequest(searchId);
        Call<SimilarArtistResponse> call = searchApi.getSimilarArtists(request);

        call.enqueue(new Callback<SimilarArtistResponse>() {
            @Override
            public void onResponse(Call<SimilarArtistResponse> call, Response<SimilarArtistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SimilarArtistResponse.Artist> results = response.body().getEmbedded().getArtists();
                    requireActivity().runOnUiThread(() -> {
//                        similarArtistsFragment.updateSimilarArtists(results);
                    });
                    Log.d("SimilarAPI", "Response: " + response.body().toString());

                } else {
                    Log.e("SimilarAPI", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SimilarArtistResponse> call, Throwable throwable) {
                Log.e("SimilarAPI", "Error: " + throwable.getMessage());
            }
        });
    }
}
