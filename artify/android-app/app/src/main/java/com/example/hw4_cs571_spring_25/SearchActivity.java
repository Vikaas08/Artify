package com.example.hw4_cs571_spring_25;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    private String searchQuery = "";
    private SearchResultFragment searchResultFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        searchResultFragment = new SearchResultFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.searchResultFragment, searchResultFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        TextView noResultsTextView = findViewById(R.id.noResultsTextView);
//        MenuItem accountItem =menu.findItem(R.id.account);
        MenuItem searchItem =menu.findItem(R.id.search);
        SearchView searchView = (SearchView)  searchItem.getActionView();
        searchItem.expandActionView();
        searchView.setIconified(false);
        searchView.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT);
        }

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search artists... ");

//        searchView.setQuery(searchQuery, false);
/*        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
//                accountItem.setVisible(false);
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
//                accountItem.setVisible(true);
                invalidateOptionsMenu();
                return true;
            }
        });*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>2){
                    searchQuery = newText;
                    Log.d("SearchView", "onQueryTextChange: " + newText);
                    noResultsTextView.setVisibility(View.GONE);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    SearchApi searchApi = retrofit.create(SearchApi.class);
                    Call<SearchResponse> call = searchApi.searchArtists(newText);

                    call.enqueue(new Callback<SearchResponse>(){
                        @Override
                        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                            if(response.isSuccessful() && response.body()!=null){
                                List<SearchResponse.ResultItem> results = response.body().getEmbedded().getResults();
//                                for (SearchResponse.ResultItem item : results) {
//                                    String title = item.getTitle();
//                                    String href = item.getLinks().getSelf().getHref();
//                                    String thumbnail = item.getLinks().getThumbnail().getHref();
//                                    Log.d("API", "Title: " + title + ", Href: " + href+", Thumbnail: "+thumbnail);
//                                }
                                if(results.isEmpty())  noResultsTextView.setVisibility(View.VISIBLE);
                                else noResultsTextView.setVisibility(View.GONE);

                                runOnUiThread(()-> searchResultFragment.updateResults(results));

                            } else {
                                Log.e("API", "Error: " + response.code());
                            }
                        }
                        @Override
                        public void onFailure(Call<SearchResponse> call, Throwable throwable) {
                            Log.e("API", "Error: " + throwable.getMessage());
                        }
                    });
                }
                else if (newText.length() == 0) {
                    runOnUiThread(() -> searchResultFragment.updateResults(new ArrayList<>()));

                    noResultsTextView.setVisibility(View.VISIBLE);
                }
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        int closeButtonId = androidx.appcompat.R.id.search_close_btn;
        ImageView clearButton = searchView.findViewById(closeButtonId);
        if(clearButton!=null){
            clearButton.setVisibility(ImageView.VISIBLE);
            clearButton.setOnClickListener(v -> {
                searchView.setQuery("",false);
                searchQuery="";
                finish();
            });
        }

        return super.onCreateOptionsMenu(menu);
    }
}


