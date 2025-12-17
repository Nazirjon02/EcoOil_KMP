package org.example.project.home

import org.example.data.CarResponse

data class MainUiState(
    val isLoading: Boolean = false,
    val isRefresh: Boolean = false,
    val carResponse: CarResponse? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean
)
