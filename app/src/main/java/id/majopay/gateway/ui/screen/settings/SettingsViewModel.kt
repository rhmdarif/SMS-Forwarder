package id.majopay.gateway.ui.screen.settings

import androidx.lifecycle.ViewModel
import id.majopay.gateway.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Settings Screen to provide dependency injection.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    val appRepository: AppRepository
) : ViewModel() 