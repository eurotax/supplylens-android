package com.eurotax.supplylens.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eurotax.supplylens.core.network.token.RealTokenService
import com.eurotax.supplylens.core.network.token.TokenSearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTokenClick: (String, String) -> Unit = { _, _ -> }
) {
    val viewModel: SearchViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search Tokens") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.updateQuery(it) },
                label = { Text("Search by symbol or address") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.results.isNotEmpty() -> {
                    LazyColumn {
                        items(uiState.results) { token ->
                            TokenSearchItem(
                                token = token,
                                onClick = { onTokenClick(token.chain, token.address) }
                            )
                        }
                    }
                }
                uiState.query.isNotEmpty() -> Text("No results found")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenSearchItem(token: TokenSearchResult, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${token.symbol} - ${token.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Price: $${token.price}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

class SearchViewModel : ViewModel() {
    private val tokenService = RealTokenService()
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        if (query.length >= 2) {
            searchTokens(query)
        } else {
            _uiState.value = _uiState.value.copy(results = emptyList())
        }
    }
    
    private fun searchTokens(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val results = tokenService.searchTokens(query)
                _uiState.value = _uiState.value.copy(isLoading = false, results = results)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, results = emptyList())
            }
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<TokenSearchResult> = emptyList()
)
