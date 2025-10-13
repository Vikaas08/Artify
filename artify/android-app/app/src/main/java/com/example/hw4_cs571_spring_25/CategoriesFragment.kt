//package com.example.hw4_cs571_spring_25
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.ComposeView
//import androidx.fragment.app.Fragment
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class CategoriesFragment : Fragment() {
//    private var searchId: String? = null
//    private var categories by mutableStateOf<List<CategoryResponse.Category>>(emptyList())
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        searchId = arguments?.getString("searchId")
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        return ComposeView(requireContext()).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//            setContent {
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    if (categories.isEmpty()) {
//                        EmptyCategoriesView()
//                    } else {
//                        CategoryList(categories)
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        Log.d("CategoriesFragment", "onStart called")
//        loadCategories()
//    }
//
//    private fun loadCategories() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/") // Update with your base URL
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val categoryApi = retrofit.create(SearchApi::class.java)
//        val request = ArtistRequest(searchId)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = categoryApi.getCategories(request).execute()
//                if (response.isSuccessful && response.body() != null) {
//                    val results = response.body()!!.embedded.categories
//                    withContext(Dispatchers.Main) {
//                        categories = results
//                        Log.d("CategoriesFragment", "Loaded ${results.size} categories")
//                    }
//                } else {
//                    Log.e("CategoriesFragment", "API error: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                Log.e("CategoriesFragment", "Network error: ${e.message}")
//            }
//        }
//    }
//}
