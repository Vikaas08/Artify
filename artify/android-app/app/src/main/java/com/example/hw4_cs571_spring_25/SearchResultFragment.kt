package com.example.hw4_cs571_spring_25

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.hw4_cs571_spring_25.ui.theme.HW4_CS571_SPRING_25Theme

class SearchResultFragment : Fragment() {

    private var artists by mutableStateOf<List<SearchResponse.ResultItem>>(emptyList())
    fun updateResults(newArtists: List<SearchResponse.ResultItem>) {
        artists = newArtists
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HW4_CS571_SPRING_25Theme {
                    ArtistList(artists)
                }
            }
        }
    }
}