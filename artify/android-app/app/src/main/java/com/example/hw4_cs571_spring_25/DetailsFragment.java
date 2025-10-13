package com.example.hw4_cs571_spring_25;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsFragment extends Fragment {
    private String searchId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);


        TextView toptext= view.findViewById(R.id.topText);
        TextView belowtext= view.findViewById(R.id.belowText);
        TextView paragraph= view.findViewById(R.id.paragraph);
        ProgressBar loading= view.findViewById(R.id.detailsLoading);
        ScrollView content= view.findViewById(R.id.detailsContent);

        content.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        if(getArguments()!=null){
            searchId = getArguments().getString("searchId");
        }

        if(searchId!=null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SearchApi searchApi = retrofit.create(SearchApi.class);
//            Call<ArtistDetails> call = searchApi.getArtistDetails(searchId);
            ArtistRequest request = new ArtistRequest(searchId);
            Call<ArtistDetailsResponse> call = searchApi.getArtistDetails(request);

            call.enqueue(new Callback<ArtistDetailsResponse>() {
                @Override
                public void onResponse(Call<ArtistDetailsResponse> call, Response<ArtistDetailsResponse> response) {
                    content.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        ArtistDetailsResponse artistDetails = response.body();
                        toptext.setText(artistDetails.getName() != null ? artistDetails.getName() : "N/A");

                        String nationality = artistDetails.getNationality();
                        String birthday = artistDetails.getBirthday();
                        String deathday = artistDetails.getDeathday();
                        String belowText = "";

                        if (nationality != null) belowText += nationality;
                        if (birthday != null) {
                            if (!belowText.isEmpty()) {
                                belowText += ", ";
                            }
                            belowText += birthday;
                        }
                        if (deathday != null) {
                            if (!belowText.isEmpty()) {
                                belowText += "-";
                            }
                            belowText += deathday;
                        }
                        belowtext.setText(belowText.isEmpty() ? "" : belowText);

                        paragraph.setText(artistDetails.getBiography());
                    } else {
                        Log.e("AboutAPI", "Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ArtistDetailsResponse> call, Throwable throwable) {
                    Log.e("AboutAPI", "Failure: " + throwable.getMessage());
                }
            });
        }
        return view;
    }
}