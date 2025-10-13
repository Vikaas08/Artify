package com.example.hw4_cs571_spring_25

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

data class Artist(
    val id: String,
    val name: String,
    val nationality: String,
    val birthday: String,
    val timestamp: Long
)

fun timeAgo(timestamp: Long, now: Long): String {
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

@Composable
fun FavArtistCard(artist: Artist) {
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = System.currentTimeMillis()
            delay(1000)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = artist.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = "${artist.nationality}, ${artist.birthday}", fontSize = 11.sp)
        }


        val context = LocalContext.current
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                val searchId=artist.id
                val intent = Intent(context, ArtistInfo::class.java).apply {
                    putExtra("searchId", searchId)
                    putExtra("title", artist.name)
                }
                context.startActivity(intent)
            })
        {
            Text(text = timeAgo(artist.timestamp, currentTime.value), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                painter = painterResource(id = R.drawable.chevron_right),
                contentDescription = "chevron_right",
                modifier = Modifier.size(20.dp)
            )
        }

    }
}

@Composable
fun FavArtistList() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var artists by remember { mutableStateOf(listOf<Artist>()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val artistIds = FavoriteManager.getAllFavorites(context)
                val fetchedArtists = artistIds.mapNotNull { id ->
                    val details = FavoriteManager.getFavoriteDetails(context, id)
                    details?.let {
                        Artist(
                            id = id,
                            name = it[0],
                            nationality = it[1],
                            birthday = it[2],
                            timestamp = it[3].toLong()
                        )
                    }
                }
                artists = fetchedArtists
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        if (artists.isEmpty()) {
            Text(
                text = "No favorites",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .background(
                        shape = RoundedCornerShape(16.dp),
                        color = colorResource(R.color.main)
                    )
                    .padding(vertical = 15.dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            )
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                items(artists) { artist ->
                    FavArtistCard(artist)
                }
            }
        }
    }
}



@Composable
fun ArtistCard(title:String, href:String, thumbnail:Any, onClick:(String)->Unit,
               showFavoriteIcon: Boolean = false,
               isFavorited: Boolean = false,
               onFavoriteClick: () -> Unit = {}){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .padding(horizontal = 15.dp, vertical = 9.dp)
            .clickable{onClick(href)},
        shape= RoundedCornerShape(14.dp),
    ){
        Box {
            AsyncImage(
                model = thumbnail,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,

                )

            if (showFavoriteIcon) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 22.dp, end = 22.dp)
                        .size(30.dp)
                        .background(color = MaterialTheme.colorScheme.primary,  shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isFavorited) R.drawable.star_filled else R.drawable.star
                        ),
                        contentDescription = "Favorite",
                        modifier = Modifier.size(35.dp),
                    )
                }
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .align(Alignment.BottomStart)
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Icon(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = "chevron_right",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistList(artists: List<SearchResponse.ResultItem>) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Box {
        LazyColumn {
            items(artists) { artist ->
                val title = artist.title ?: "N/A"
                val href = artist.links?.self?.href ?: "N/A"
                val thumbnail =
                    if (artist.links?.thumbnail?.href == "/assets/shared/missing_image.png")
                        R.drawable.artsy_logo
                    else artist.links?.thumbnail?.href ?: "N/A"

                val artistId = href.substringAfterLast("/")
                val isUserLoggedIn = AuthManager.isLoggedIn(context)
                val isFavorited =
                    remember { mutableStateOf(FavoriteManager.isFavorited(context, artistId)) }

                ArtistCard(
                    title = title, href = href, thumbnail = thumbnail,
                    onClick = { clickedHref ->
                        val searchId = clickedHref.substringAfterLast("/")
                        val intent = Intent(context, ArtistInfo::class.java).apply {
                            putExtra("searchId", searchId)
                            putExtra("title", title)
                        }
                        context.startActivity(intent)
                    },
                    showFavoriteIcon = isUserLoggedIn,
                    isFavorited = isFavorited.value,
                    onFavoriteClick = {
                        val isNowFavorited = !isFavorited.value
                        FavoriteManager.toggleFavorite(context, artistId)
                        isFavorited.value = isNowFavorited

                        scope.launch {
                            val message = if (isNowFavorited) {
                                "Added to favorites"
                            } else {
                                "Removed from favorites"
                            }
                            snackbarHostState.showSnackbar(message)
                        }

                        val favorites = FavoriteManager.getAllFavorites(context)
                        Log.d("Favorites", "Current Favorites: $favorites")

                        for (favoriteId in favorites) {
                            val details = FavoriteManager.getFavoriteDetails(context, favoriteId)
                            if (details != null) {
                                val (name, nationality, birthday) = details
                                Log.d(
                                    "ArtistDetails",
                                    "ID: $favoriteId, Name: $name, Nationality: $nationality, Birthday: $birthday"
                                )
                            } else {
                                Log.d("ArtistDetails", "ID: $favoriteId has no details stored")
                            }
                        }

                        if (isNowFavorited) {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val api = retrofit.create(SearchApi::class.java)
                            val request = ArtistRequest(artistId)

                            api.getArtistDetails(request)
                                .enqueue(object : Callback<ArtistDetailsResponse> {
                                    override fun onResponse(
                                        call: Call<ArtistDetailsResponse>,
                                        response: Response<ArtistDetailsResponse>
                                    ) {
                                        if (response.isSuccessful && response.body() != null) {
                                            val artist = response.body()!!
                                            FavoriteManager.addFavoriteDetails(
                                                context,
                                                artistId,
                                                artist.name ?: "N/A",
                                                artist.nationality ?: "N/A",
                                                artist.birthday ?: "N/A"
                                            )
                                            Log.d("ArtistDetails", "Saved: ${artist.name}")
                                        } else {
                                            Log.e("ArtistDetails", "Failed: ${response.code()}")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ArtistDetailsResponse>,
                                        t: Throwable
                                    ) {
                                        Log.e("ArtistDetails", "Error: ${t.message}")
                                    }
                                })
                        } else {
                            FavoriteManager.removeFavoriteDetails(context, artistId)
                            Log.d("ArtistDetails", "Removed artist info for $artistId")
                        }
                    }
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SimilarArtistList(similarArtists: List<SimilarArtistResponse.Artist>) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
    )
    {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical=16.dp),
        ) {
            items(similarArtists) { artist ->
                val title = artist.name ?: "N/A"
                val href = artist.links?.self?.href ?: "N/A"
                val thumbnail =
                    if (artist.links?.thumbnail?.href == "/assets/shared/missing_image.png")
                        R.drawable.artsy_logo
                    else artist.links?.thumbnail?.href ?: "N/A"

                val artistId = href.substringAfterLast("/")
                val isUserLoggedIn = AuthManager.isLoggedIn(context)
                val isFavorited = remember { mutableStateOf(FavoriteManager.isFavorited(context, artistId)) }

                ArtistCard(
                    title = title,
                    href = href,
                    thumbnail = thumbnail,
                    onClick = { clickedHref ->
                        val searchId = clickedHref.substringAfterLast("/")
                        val intent = Intent(context, ArtistInfo::class.java).apply {
                            putExtra("searchId", searchId)
                            putExtra("title", title)
                        }
                        context.startActivity(intent)
                    },
                    showFavoriteIcon = isUserLoggedIn,
                    isFavorited = isFavorited.value,
                    onFavoriteClick = {
                        val isNowFavorited = !isFavorited.value
                        FavoriteManager.toggleFavorite(context, artistId)
                        isFavorited.value = isNowFavorited

                        scope.launch {
                            val message = if (isNowFavorited) {
                                "Added to favorites"
                            } else {
                                "Removed from favorites"
                            }
                            snackbarHostState.showSnackbar(message)
                        }

                        if (isNowFavorited) {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val api = retrofit.create(SearchApi::class.java)
                            val request = ArtistRequest(artistId)

                            api.getArtistDetails(request).enqueue(object : Callback<ArtistDetailsResponse> {
                                override fun onResponse(
                                    call: Call<ArtistDetailsResponse>,
                                    response: Response<ArtistDetailsResponse>
                                ) {
                                    if (response.isSuccessful && response.body() != null) {
                                        val artist = response.body()!!
                                        FavoriteManager.addFavoriteDetails(
                                            context,
                                            artistId,
                                            artist.name ?: "N/A",
                                            artist.nationality ?: "N/A",
                                            artist.birthday ?: "N/A"
                                        )
                                    }
                                }

                                override fun onFailure(call: Call<ArtistDetailsResponse>, t: Throwable) {
                                    Log.e("ArtistDetails", "Error: ${t.message}")
                                }
                            })
                        } else {
                            FavoriteManager.removeFavoriteDetails(context, artistId)
                        }
                    }
                )
            }
            item{ Spacer(modifier = Modifier.height(100.dp)) }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun EmptySimilarArtistsView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 7.dp)
            .wrapContentHeight(Alignment.Top)
    ) {
        Text(
            text = "No Similar Artist",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 15.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.Black // Optional: match text color if needed
        )
    }
}


@Composable
fun ArtworkCard(id:String,title:String, date:String, href:String, thumbnail:Any, onViewCategories:(String)->Unit) {
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(640.dp)
            .padding(horizontal = 15.dp, vertical = 9.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
            ) {
                AsyncImage(
                    model = thumbnail,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,

                    )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Text(
                        text = "$title, $date",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W900,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onViewCategories(id) },
                        modifier = Modifier
                            .width(180.dp)
                            .height(40.dp)
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = secondaryColor)
                    ) {
                        Text(text = "View Categories", color = MaterialTheme.colorScheme.secondary)
                    }

                }
            }
        }
    }
}

@Composable
fun ArtworkList(similarArtists: List<ArtworkResponse.Artwork>) {
    val context = LocalContext.current
    val showDialogForIdState = remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
    )
    {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical=16.dp),
        ) {
            items(similarArtists) { artist ->
                val id  = artist.id?:"N/A"
                val title = artist.title ?: "N/A"
                val href = artist.links?.thumbnail?.href ?: "N/A"
                val date = artist.date ?: "N/A"
                val thumbnail =
                    if (artist.links?.thumbnail?.href == "/assets/shared/missing_image.png")
                        R.drawable.artsy_logo
                    else artist.links?.thumbnail?.href ?: "N/A"
                ArtworkCard(
                    id = id,
                    title = title,
                    date=date,
                    href = href,
                    thumbnail = thumbnail,
                    onViewCategories = { clickedHref ->
                        println("VIKAS  "+id)
                        showDialogForIdState.value = id
//                        val fragment = CategoriesFragment().apply {
//                            arguments= Bundle().apply{
//                                putString("searchId", searchId)
//                            }
//                        }
                    }
                )
                if (showDialogForIdState.value == id) {
                    CategoriesDialogContent(
                        searchId = id,
                        onDismiss = { showDialogForIdState.value = null }
                    )
                }
            }
            item{ Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun EmptyArtworksView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 7.dp)
            .wrapContentHeight(Alignment.Top)
    ) {
        Text(
            text = "No Artworks",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 15.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}



@Composable
fun CategoriesDialogContent(searchId: String, onDismiss: () -> Unit) {
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    var categories by remember { mutableStateOf<List<CategoryResponse.Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(searchId) {
        isLoading = true
        val retrofit = Retrofit.Builder()
            .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val categoryApi = retrofit.create(CategoryApi::class.java)
        val request = CategoryRequest(searchId)

        Log.d("CategoriesDialog", "Search ID: $searchId")

        try {
            val response = categoryApi.getCategories(request)
            categories = response.embedded.categories
            Log.d("CategoriesDialog : SUCCESS", "Loaded ${categories.size} categories")
        } catch (e: Exception) {
            Log.e("CategoriesDialog", "Network error: ${e.message}")
        }finally {
            isLoading = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .width(380.dp)
                .height(610.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge
                )
                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    categories.isEmpty() -> {
                        EmptyCategoriesView()
                    }
                    else -> {
                        CategoryList(categories)
                    }
                }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryResponse.Category, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(250.dp)
            .height(460.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = category.links.thumbnail.href,
                contentDescription = category.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.35f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.65f)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Column {
                    Text(
                        text = category.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = category.description,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryList(categories: List<CategoryResponse.Category>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            items(categories) { category ->
                CategoryCard(category = category)
            }
        }

        IconButton(
            onClick = {
                val previousIndex = listState.firstVisibleItemIndex - 1
                val target = if (previousIndex < 0) categories.size - 1 else previousIndex
                coroutineScope.launch {
                    listState.animateScrollToItem(target)
                }
            },
            enabled = listState.firstVisibleItemIndex > 0,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 0.dp)
                .size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "Scroll Left",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        IconButton(
            onClick = {
                val nextIndex = listState.firstVisibleItemIndex + 1
                val target = if (nextIndex >= categories.size) 0 else nextIndex
                coroutineScope.launch {
                    listState.animateScrollToItem(target)
                }
            },
            enabled = listState.firstVisibleItemIndex < categories.size - 1,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 0.dp)
                .size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chevron_right),
                contentDescription = "Scroll Right",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun EmptyCategoriesView() {
    Text(text = "No categories available")
}
