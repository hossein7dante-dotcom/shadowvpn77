package com.example.submgr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigViewModel(private val repo: SubRepository) : ViewModel() {

    val configs = repo.configsFlow().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun refresh(url: String, onError: (String)->Unit = {}) {
        viewModelScope.launch {
            try {
                repo.refreshFromUrl(url)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Error")
            }
        }
    }

    fun toggle(item: ConfigItem) {
        viewModelScope.launch {
            repo.toggle(item)
        }
    }
}
