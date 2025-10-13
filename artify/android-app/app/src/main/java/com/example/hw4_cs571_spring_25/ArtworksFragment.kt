package com.example.hw4_cs571_spring_25

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.example.hw4_cs571_spring_25.ui.theme.HW4_CS571_SPRING_25Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtworksFragment : Fragment() {
    private var searchId: String? = null
    private var artworks by mutableStateOf<List<ArtworkResponse.Artwork>>(emptyList())
    private var isLoading by mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchId = arguments?.getString("searchId")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                HW4_CS571_SPRING_25Theme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        when {
                            isLoading -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AndroidView(
                                        factory = { context ->
                                            ProgressBar(context).apply {
                                                isIndeterminate = true
                                            }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 24.dp)
                                    )
                                }
                            }

                            artworks.isEmpty() -> {
                                EmptyArtworksView()
                            }

                            else -> {
                                ArtworkList(artworks)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("ArtworksFragment", "onStart called")
        searchId?.let { loadArtworks(it) }
    }

    private fun loadArtworks(artistId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val artworkApi = retrofit.create(SearchApi::class.java)
        val request = ArtistRequest(searchId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = artworkApi.getArtworks(request).execute()
                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!.embedded.artworks
                    withContext(Dispatchers.Main) {
                        artworks = results
                        isLoading = false
                        Log.d("ArtworksFragment", "Loaded ${results.size} artworks")
                    }
                } else {
                    withContext(Dispatchers.Main){
                        isLoading = false
                    }
                    Log.e("ArtworksFragment", "API error: ${response.code()}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    isLoading = false
                }
                Log.e("ArtworksFragment", "Network error: ${e.message}")
            }
        }
    }
}
