package com.bvic.lydiacontacts.ui.contactDetail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bvic.lydiacontacts.ui.contactDetail.components.BackButton
import com.bvic.lydiacontacts.ui.contactDetail.components.BottomCTAs
import com.bvic.lydiacontacts.ui.contactDetail.components.ContactPreview
import com.bvic.lydiacontacts.ui.contactDetail.components.ContactPreviewProvider
import com.bvic.lydiacontacts.ui.contactDetail.components.HeaderHero
import com.bvic.lydiacontacts.ui.contactDetail.components.HeaderTexts
import com.bvic.lydiacontacts.ui.contactDetail.components.InfoCard
import com.bvic.lydiacontacts.ui.contactDetail.components.QuickActions
import com.bvic.lydiacontacts.ui.shared.preview.SharedTransitionPreviewHarness
import com.bvic.lydiacontacts.ui.shared.snackbar.CollectErrors
import com.bvic.lydiacontacts.ui.shared.snackbar.ErrorSnackbar
import com.bvic.lydiacontacts.ui.shared.theme.LydiaContactsTheme
import kotlinx.coroutines.flow.map
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ContactDetailScreen(
    modifier: Modifier = Modifier,
    contactDetailViewModel: ContactDetailViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val state by contactDetailViewModel.contactsUiState.collectAsStateWithLifecycle()

    CollectErrors(
        errors =
            remember(contactDetailViewModel.effects) {
                contactDetailViewModel.effects.map { (it as? ContactDetailEffect.ShowError)?.error }
            },
        host = snackbarHostState,
    )
    state.contact?.let { contact ->
        ContactDetailScreen(
            modifier = modifier,
            id = contact.id,
            pictureLarge = contact.pictureLarge,
            name = contact.fullName,
            age = contact.age,
            nationality = contact.nationality,
            phone = contact.phone,
            email = contact.email,
            address = contact.address,
            birthDate =
                contact.birthDate?.let {
                    val customFormat =
                        DateTimeComponents.Format {
                            day()
                            char('/')
                            monthNumber()
                            char('/')
                            year()
                        }
                    it.format(customFormat)
                },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            snackbarHostState = snackbarHostState,
            onClickBack = {
                contactDetailViewModel.submitAction(ContactDetailAction.BackClicked)
            },
            onClickFavorite = {
                // TODO: impl favoris
            },
            onClickMessage = {
                contactDetailViewModel.submitAction(ContactDetailAction.MessageClicked)
            },
            onClickCall = {
                contactDetailViewModel.submitAction(ContactDetailAction.CallClicked)
            },
            onClickMail = {
                contactDetailViewModel.submitAction(ContactDetailAction.MailClicked)
            },
            onClickMap = {
                contactDetailViewModel.submitAction(ContactDetailAction.MapClicked)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ContactDetailScreen(
    modifier: Modifier = Modifier,
    id: String,
    pictureLarge: String?,
    name: String?,
    age: Int?,
    nationality: String?,
    phone: String?,
    email: String?,
    address: String?,
    birthDate: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    snackbarHostState: SnackbarHostState,
    onClickBack: () -> Unit,
    onClickMessage: () -> Unit,
    onClickFavorite: () -> Unit,
    onClickCall: () -> Unit,
    onClickMail: () -> Unit,
    onClickMap: () -> Unit,
) {
    val scroll = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    ErrorSnackbar(
                        data = it,
                    )
                },
            )
        },
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            HeaderHero(
                modifier = Modifier.align(Alignment.TopCenter),
                id = id,
                scrollValue = scroll.value,
                name = name,
                pictureLarge = pictureLarge,
                headerHeight = 300.dp,
                nameForContentDesc = name,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(innerPadding),
            ) {
                Spacer(Modifier.height(220.dp))

                Surface(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 64.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        HeaderTexts(
                            id = id,
                            name = name,
                            age = age,
                            nationality = nationality,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                        )

                        Spacer(Modifier.height(12.dp))

                        QuickActions(
                            onCall = onClickCall,
                            onMail = onClickMail,
                            onMap = onClickMap,
                        )

                        Spacer(Modifier.height(12.dp))

                        InfoCard(icon = Icons.Filled.Phone, title = "Téléphone", value = phone)
                        with(sharedTransitionScope) {
                            InfoCard(
                                modifier =
                                    Modifier.sharedBounds(
                                        rememberSharedContentState(key = "email-$id"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                                icon = Icons.Filled.Email,
                                title = "Email",
                                value = email,
                            )
                        }
                        InfoCard(icon = Icons.Filled.Home, title = "Adresse", value = address)
                        InfoCard(icon = Icons.Filled.Cake, title = "Naissance", value = birthDate)

                        Spacer(Modifier.height(16.dp))

                        BottomCTAs(onMessage = onClickMessage, onFavorite = onClickFavorite)
                    }
                }
            }

            BackButton(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(innerPadding)
                        .padding(16.dp),
                onClick = onClickBack,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDesc = "Retour",
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    name = "Contact Detail - Light",
    showBackground = true,
)
@Composable
private fun PreviewContactDetailLight(
    @PreviewParameter(ContactPreviewProvider::class) data: ContactPreview,
) {
    LydiaContactsTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            ContactDetailScreen(
                id = data.id,
                pictureLarge = data.picture,
                name = data.name,
                age = data.age,
                nationality = data.nationality,
                phone = data.phone,
                email = data.email,
                address = data.address,
                birthDate = data.birthDate,
                sharedTransitionScope = sts,
                animatedVisibilityScope = avs,
                snackbarHostState = remember { SnackbarHostState() },
                onClickBack = {},
                onClickMessage = {},
                onClickFavorite = {},
                onClickCall = {},
                onClickMail = {},
                onClickMap = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    name = "Contact Detail - Dark",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewContactDetailDark(
    @PreviewParameter(ContactPreviewProvider::class) data: ContactPreview,
) {
    LydiaContactsTheme {
        SharedTransitionPreviewHarness { sts, avs ->
            ContactDetailScreen(
                id = data.id,
                pictureLarge = data.picture,
                name = data.name,
                age = data.age,
                nationality = data.nationality,
                phone = data.phone,
                email = data.email,
                address = data.address,
                birthDate = data.birthDate,
                snackbarHostState = remember { SnackbarHostState() },
                sharedTransitionScope = sts,
                animatedVisibilityScope = avs,
                onClickBack = {},
                onClickMessage = {},
                onClickFavorite = {},
                onClickCall = {},
                onClickMail = {},
                onClickMap = {},
            )
        }
    }
}
