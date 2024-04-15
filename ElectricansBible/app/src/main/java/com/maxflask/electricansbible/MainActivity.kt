package com.maxflask.electricansbible

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.ColorFilter
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
    books.value = listOf("titles")
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
                    title = { Text("Book Titles") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            ChapterList(chapters.value, paddingValues)
        }
    }
}

@Composable
fun DrawerContent(books: List<String>, selectedBook: MutableState<String>, drawerState: DrawerState, scope: CoroutineScope) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.iconbook), // Замените placeholder на реальный resource id
            contentDescription = "Book Cover",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(16.dp),
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        books.forEach { book ->
            Text(
                text = book,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        selectedBook.value = book
                        scope.launch { drawerState.close() }
                    }
            )
        }
    }
}

@Composable
fun ChapterList(chapters: List<String>, paddingValues: PaddingValues) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(chapters.size) { index ->
            Text(text = chapters[index], modifier = Modifier.padding(8.dp))
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