package com.example.bookscout.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookscout.domain.model.Book
import com.example.bookscout.ui.components.BookCoverImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBookClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BookScout Search") },
                actions = {
                    IconButton(onClick = { viewModel.updateLayoutPreference(!uiState.isGridView) }) {
                        Icon(
                            imageVector = if (uiState.isGridView) Icons.Default.List else Icons.Default.GridOn,
                            contentDescription = "Toggle Layout"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChanged(it) },
                label = { Text("Search by title, author, or ISBN...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            val isOffline = uiState.errorMessage?.contains("ConnectException", ignoreCase = true) == true ||
                    uiState.errorMessage?.contains("UnknownHostException", ignoreCase = true) == true

            AnimatedVisibility(visible = isOffline) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You are currently offline. Check your network connection.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.errorMessage != null && !isOffline -> {
                        Text(
                            text = uiState.errorMessage ?: "Unknown error occurred.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.books.isEmpty() && uiState.query.isNotBlank() && !uiState.isLoading -> {
                        Text(
                            text = "No books found. Try a different query.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.isGridView -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.books.size) { index ->
                                val book = uiState.books[index]
                                if (index >= uiState.books.size - 2) {
                                    viewModel.searchBooks(isInitialSearch = false)
                                }
                                BookGridItem(book = book, onClick = { onBookClick(book.id) })
                            }
                            if (uiState.isPaginationLoading) {
                                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.books.size) { index ->
                                val book = uiState.books[index]
                                if (index >= uiState.books.size - 2) {
                                    viewModel.searchBooks(isInitialSearch = false)
                                }
                                BookListItem(book = book, onClick = { onBookClick(book.id) })
                            }
                            if (uiState.isPaginationLoading) {
                                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListItem(book: Book, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookCoverImage(
                coverUrl = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier.size(width = 50.dp, height = 75.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = book.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text(text = book.author, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                Text(text = book.firstPublishYear, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookGridItem(book: Book, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BookCoverImage(
                coverUrl = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = book.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
            Text(text = book.author, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}