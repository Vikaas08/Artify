package com.example.hw4_cs571_spring_25;

import static java.lang.System.load;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button loginButton = findViewById(R.id.loginButton);
        TextView noFavoritesTextView = findViewById(R.id.noFavoritesTextView);
        TextView footerTextView = findViewById(R.id.footerTextView);
        TextView footerTextView2 = findViewById(R.id.footerTextView2);
        TextView currDateTextView = findViewById(R.id.currDateTextView);
        View favArtistFragment = findViewById(R.id.favArtistFragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ConstraintLayout layout = findViewById(R.id.main);

        setSupportActionBar(toolbar);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });



        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault());
        String currDate = sdf.format(new Date());
        currDateTextView.setText(currDate);


        currDateTextView.setOnClickListener(v -> {
            FavoriteManager.clearAllFavorites(MainActivity.this);

        });

        footerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.artsy.net/"));
            startActivity(intent);
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.favArtistFragment, new FavArtistComposeFragment())
                .commit();

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });


        boolean isLoggedIn = AuthManager.isLoggedIn(this);
        if(isLoggedIn) {
            loginButton.setVisibility(View.GONE);
            favArtistFragment.setVisibility(View.VISIBLE);
            footerTextView.setVisibility(View.GONE);
            footerTextView2.setVisibility(View.VISIBLE);

        } else {
            loginButton.setVisibility(View.VISIBLE);
            favArtistFragment.setVisibility(View.GONE);
            footerTextView.setVisibility(View.VISIBLE);
            footerTextView2.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem profileImage = menu.findItem(R.id.profileImage);
        MenuItem account = menu.findItem(R.id.account);
        View actionView = profileImage.getActionView();
        ImageView profileImageView = actionView.findViewById(R.id.profileImageView);

        if(AuthManager.isLoggedIn(this)) {
            account.setVisible(false);
            profileImage.setVisible(true);

            String profileImageUrl = AuthManager.getProfileImageUrl(this);

            Glide.with(this)
                    .load(profileImageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.account_outline)
                    .into(profileImageView);

            profileImageView.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.drop_down_menu, popup.getMenu());

                Menu menuPopup = popup.getMenu();
                for (int i = 0; i < menuPopup.size(); i++) {
                    MenuItem mi = menuPopup.getItem(i);
                    SpannableString spanString = new SpannableString(mi.getTitle());

                    if (mi.getItemId() == R.id.logout) {
                        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.secondary)),
                                0, spanString.length(), 0);
                    } else if (mi.getItemId() == R.id.delete_account) {
                        spanString.setSpan(new ForegroundColorSpan(Color.RED),
                                0, spanString.length(), 0);
                    }

                    mi.setTitle(spanString);
                }

                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.logout) {
                        AuthManager.logOut(this);
                        Snackbar.make(findViewById(android.R.id.content), "Logged out successfully", Snackbar.LENGTH_SHORT).show();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            recreate();
                        }, 1500);

                        return true;
                    } else if (id == R.id.delete_account) {

                        DeleteRequest deleteRequest = new DeleteRequest(AuthManager.getEmail(this));
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        SearchApi searchApi = retrofit.create(SearchApi.class);
                        Call<DeleteResponse> call = searchApi.deleteAccount(deleteRequest);

                        call.enqueue(new Callback<DeleteResponse>() {
                            @Override
                            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                                if(response.code() == 200 && response.body() != null) {
                                    String message = response.body().getMessage();
                                    Log.d("DeleteAPI", "Success: " + message);

                                    AuthManager.logOut(MainActivity.this);
                                    Snackbar.make(findViewById(android.R.id.content), "Deleted user successfully", Snackbar.LENGTH_SHORT).show();
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        recreate();
                                    }, 1500);
                                }
                                else{
                                    String eml = AuthManager.getEmail(MainActivity.this);
                                    if(response.body()!=null) {
                                        Log.d("DeleteAPI", "Failure" + eml + " " +response.body().getMessage());
                                    }
                                    Log.d("DeleteAPI","Failure");
                                }
                            }
                            @Override
                            public void onFailure(Call<DeleteResponse> call, Throwable throwable) {
                                Log.e("DeleteAPI", "Error: " + throwable.getMessage());
                            }
                        });

                        return true;
                    }
                    return false;
                });

                popup.show();
            });
        } else {
            account.setVisible(true);
            profileImage.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
         else if(id == R.id.account) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
         }

        return super.onOptionsItemSelected(item);
    }
}