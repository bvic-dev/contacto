package com.bvic.lydiacontacts

import androidx.lifecycle.ViewModel
import com.bvic.lydiacontacts.ui.shared.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        val navigator: Navigator,
    ) : ViewModel()
