package com.bvic.contacto

import androidx.lifecycle.ViewModel
import com.bvic.contacto.ui.shared.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        val navigator: Navigator,
    ) : ViewModel()
