// file: com/bvic/contacto/ui/contactDetail/di/ContactDetailModule.kt
package com.bvic.contacto.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.bvic.contacto.ui.shared.navigation.Route
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ContactDetailModule {
    @Provides
    fun provideContactIdDecoder(): @JvmSuppressWildcards (SavedStateHandle) -> String = ::decodeContactId
}

fun decodeContactId(handle: SavedStateHandle): String = handle.toRoute<Route.ContactDetail>().id
