package com.example.hw4_cs571_spring_25;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtistInfo extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private boolean isFavorited = false;
    private String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artistinfo);

        String searchId = getIntent().getStringExtra("searchId");
        String title = getIntent().getStringExtra("title");

        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout tabLayout= findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle detailsBundle = new Bundle();
        detailsBundle.putString("searchId", searchId);
        detailsFragment.setArguments(detailsBundle);

        ArtworksFragment artworksFragment = new ArtworksFragment();
        Bundle artworksBundle = new Bundle();
        artworksBundle.putString("searchId", searchId);
        artworksFragment.setArguments(artworksBundle);

//        SimilarArtistsFragment similarArtistsFragment = new SimilarArtistsFragment();
//        Bundle similarBundle = new Bundle();
//        similarBundle.putString("searchId", searchId);
//        similarArtistsFragment.setArguments(similarBundle);

//        Fragment[] fragments = {detailsFragment, artworksFragment, similarArtistsFragment};
//        String[] titles = {"Details", "Artworks", "Similar"};
        Fragment[] fragments;
        String[] titles;

        if (AuthManager.isLoggedIn(this)) {
            SimilarArtistsFragment similarArtistsFragment = new SimilarArtistsFragment();
            Bundle similarBundle = new Bundle();
            similarBundle.putString("searchId", searchId);
            similarArtistsFragment.setArguments(similarBundle);

            fragments = new Fragment[] { detailsFragment, artworksFragment, similarArtistsFragment };
            titles = new String[] { "Details", "Artworks", "Similar" };
        } else {
            fragments = new Fragment[] { detailsFragment, artworksFragment };
            titles = new String[] { "Details", "Artworks" };
        }

        adapter = new ViewPagerAdapter(this, fragments, titles);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(titles[position]);
        }).attach();

//        viewPager.setCurrentItem(2, false);
        tabLayout.post(()->{
            int tabCount = tabLayout.getTabCount();
            if(tabLayout.getTabCount()>=2) {
                tabLayout.getTabAt(0).setIcon(R.drawable.details);
                tabLayout.getTabAt(1).setIcon(R.drawable.artworks);
                if (tabCount >= 3) {
                    tabLayout.getTabAt(2).setIcon(R.drawable.account_search_outline);
                }

                ColorStateList iconColor = ContextCompat.getColorStateList(this, R.color.secondary);
                for (int i = 0; i <tabCount; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null && tab.getIcon() != null) {
                        tab.getIcon().setTintList(iconColor);
                    }
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AuthManager.isLoggedIn(this)) {
            getMenuInflater().inflate(R.menu.artist_info_menu, menu);
            MenuItem favoriteItem = menu.findItem(R.id.action_favorite);

            artistId = getIntent().getStringExtra("searchId");
            Set<String> allFavorites = FavoriteManager.getAllFavorites(this);
            isFavorited = allFavorites.contains(artistId);
            updateFavoriteIcon(favoriteItem);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            isFavorited = !isFavorited;
            FavoriteManager.toggleFavorite(this, artistId);
            updateFavoriteIcon(item);

            if (isFavorited) {
                fetchAndStoreArtistDetails(artistId);
                Snackbar.make(findViewById(android.R.id.content), "Added to favorites", Snackbar.LENGTH_SHORT).show();
            } else {
                FavoriteManager.removeFavoriteDetails(this, artistId);
                Snackbar.make(findViewById(android.R.id.content), "Removed from favorites", Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFavoriteIcon(MenuItem item) {
        int iconRes = !isFavorited ? R.drawable.star : R.drawable.star_filled;
        item.setIcon(ContextCompat.getDrawable(this, iconRes));
    }
    private void fetchAndStoreArtistDetails(String artistId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SearchApi api = retrofit.create(SearchApi.class);
        ArtistRequest request = new ArtistRequest(artistId);

        api.getArtistDetails(request).enqueue(new Callback<ArtistDetailsResponse>() {
            @Override
            public void onResponse(Call<ArtistDetailsResponse> call, Response<ArtistDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArtistDetailsResponse artist = response.body();
                    FavoriteManager.addFavoriteDetails(
                            ArtistInfo.this,
                            artistId,
                            artist.getName() != null ? artist.getName() : "N/A",
                            artist.getNationality() != null ? artist.getNationality() : "N/A",
                            artist.getBirthday() != null ? artist.getBirthday() : "N/A"
                    );
                }
            }

            @Override
            public void onFailure(Call<ArtistDetailsResponse> call, Throwable t) {
                Log.e("ArtistInfo", "Failed to fetch details: " + t.getMessage());
            }
        });
    }

}