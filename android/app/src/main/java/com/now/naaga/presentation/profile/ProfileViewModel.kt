package com.now.naaga.presentation.profile

import androidx.lifecycle.ViewModel
import com.now.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel()
