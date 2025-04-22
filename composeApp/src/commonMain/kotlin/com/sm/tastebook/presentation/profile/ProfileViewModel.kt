package com.sm.tastebook.presentation.profile

import androidx.datastore.core.DataStore
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.datastore.toUserAuthResultData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dataStore: DataStore<UserSettings>
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Use DataStore to get user information
                dataStore.data.map { settings ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        firstName = settings.firstName ?: "",
                        lastName = settings.lastName ?: "",
                        email = settings.email ?: "",
                        username = settings.username ?: "",
                        avatar = settings.avatar,
                        userId = settings.id
                    )
                }.collect {}
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val username: String = "",
    val avatar: String? = null,
    val userId: Int = -1
)


//class ProfileViewModel(
//  private val dataStore: DataStore<UserSettings>,
//  private val userRepository: UserRepository
//) : ViewModel() {
//
//  private val _state = MutableStateFlow(ProfileUiState())
//  val state: StateFlow<ProfileUiState> = _state.asStateFlow()
//
//  init {
//    viewModelScope.launch {
//      // 1) load local immediately
//      dataStore.data
//        .map { it.toUserAuthResultData() }
//        .filter { it.id != -1 }
//        .distinctUntilChanged()
//        .collectLatest { user ->
//          _state.update { it.copy(
//            firstName = user.firstName,
//            lastName  = user.lastName,
//            username  = user.username,
//            email     = user.email,
//            avatar    = user.avatar,
//            userId    = user.id
//          ) }
//
//          // 2) then fire off a network refresh
//          refreshFromServer(user.id)
//        }
//    }
//  }
//
//  private suspend fun refreshFromServer(userId: Int) {
//    _state.update { it.copy(isLoading = true) }
//    when (val result = userRepository.getUserProfile(userId)) {
//      is Result.Success -> {
//        val d = result.data
//        _state.update { it.copy(
//          isLoading  = false,
//          firstName  = d.firstName,
//          lastName   = d.lastName,
//          username   = d.username,
//          email      = d.email,
//          avatar     = d.avatar
//        ) }
//      }
//      is Result.Error -> {
//        _state.update { it.copy(
//          isLoading = false,
//          error     = result.message
//        ) }
//      }
//    }
//  }
//}