package com.bvic.contacto.ui.contactdetail.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

data class ContactPreview(
    val id: String,
    val picture: String?,
    val name: String?,
    val age: Int?,
    val nationality: String?,
    val phone: String?,
    val email: String?,
    val address: String?,
    val birthDate: String?,
)

class ContactPreviewProvider : PreviewParameterProvider<ContactPreview> {
    override val values =
        sequenceOf(
            ContactPreview(
                id = "123",
                picture = "https://randomuser.me/api/portraits/men/1.jpg",
                name = "Thibault Martin",
                age = 29,
                nationality = "FR",
                phone = "+33 6 12 34 56 78",
                email = "thibault.martin@example.com",
                address = "12 rue de la Paix, 75002 Paris",
                birthDate = "14/07/1996",
            ),
            ContactPreview(
                id = "12334",
                picture = null,
                name = "Thibault Martin",
                age = 29,
                nationality = "FR",
                phone = null,
                email = "thibault.martin@example.com",
                address = "12 rue de la Paix, 75002 Paris",
                birthDate = null,
            ),
            ContactPreview(
                id = "344",
                picture = null,
                name = null,
                age = null,
                nationality = null,
                phone = null,
                email = null,
                address = null,
                birthDate = null,
            ),
        )
}
