package com.maxflask.electricansbible

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.maxflask.electricansbible.ui.theme.ElectricansBibleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElectricansBibleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookReaderApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var books = remember { mutableStateOf(listOf<String>()) }
    val selectedBook = remember { mutableStateOf("") }
    val chapters = remember { mutableStateOf(listOf<String>()) }
    books.value = listOf("ПОТЭЭ", "Охрана труда")
    LaunchedEffect(selectedBook.value) {
        chapters.value = loadChapters(context, selectedBook.value)
    }

    ModalNavigationDrawer(

        drawerState = drawerState,
        drawerContent = {

                DrawerContent(books.value, selectedBook, drawerState, scope)

        },
        scrimColor = Color(0xFF7B1FA2).copy(alpha = 0.9f) // Фиолетовый с прозрачностью
    ) {


        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Библия электриков") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            containerColor = colorResource(id = R.color.fiolet)
        ) { paddingValues ->
            ChapterList(chapters.value, paddingValues)

        }
    }
}

@Composable
fun DrawerContent(
    books: List<String>,
    selectedBook: MutableState<String>,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    Column() {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Book Cover",
            modifier = Modifier
                .height(128.dp)
                .width(128.dp)
                .padding(5.dp),
            alpha = 1f
        )

        LazyColumn {
            items(books) { item ->
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .border(1.dp, Color.Gray)
                        .fillMaxWidth()

                ) {
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable {
                                selectedBook.value = item
                                scope.launch { drawerState.close() }
                            }
                    )
                }

            }
        }


    }
}

@Composable
fun ChapterList(chapters: List<String>, paddingValues: PaddingValues) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(chapters.size) { index ->
            Column(modifier = Modifier.fillMaxWidth().padding(5.dp).border(1.dp, Color.Gray,
                RoundedCornerShape(5.dp)
            )) {
                Text(text = chapters[index], modifier = Modifier.padding(8.dp))
            }

        }
    }
}

fun loadChapters(context: Context, bookName: String): List<String> {
    return try {
        context.assets.open("$bookName.txt").bufferedReader().use(BufferedReader::readLines)
    } catch (e: Exception) {
        listOf("Error loading file: ${e.message}")
    }
}