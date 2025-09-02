package com.bvic.contacto.data.mapper

import com.bvic.contacto.data.local.entity.RandomUserEntity
import com.bvic.contacto.data.remote.dto.CoordinatesDto
import com.bvic.contacto.data.remote.dto.DobDto
import com.bvic.contacto.data.remote.dto.IdDto
import com.bvic.contacto.data.remote.dto.LocationDto
import com.bvic.contacto.data.remote.dto.LoginDto
import com.bvic.contacto.data.remote.dto.NameDto
import com.bvic.contacto.data.remote.dto.PictureDto
import com.bvic.contacto.data.remote.dto.RandomUserDto
import com.bvic.contacto.data.remote.dto.RegisteredDto
import com.bvic.contacto.data.remote.dto.StreetDto
import com.bvic.contacto.data.remote.dto.TimezoneDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class RandomUserMapperTest {
    private val dto =
        RandomUserDto(
            login =
                LoginDto(
                    uuid = "uuid-123",
                    md5 = "md5hash",
                    password = "password123",
                    salt = "saltValue",
                    sha1 = "sha1hash",
                    sha256 = "sha256hash",
                    username = "johndoe",
                ),
            name = NameDto(title = "Mr", first = "John", last = "Doe"),
            email = "john@doe.com",
            phone = "0102030405",
            picture =
                PictureDto(
                    thumbnail = "thumb.jpg",
                    medium = "medium.jpg",
                    large = "large.jpg",
                ),
            dob = DobDto(age = 42, date = "1990-01-01T00:00:00Z"),
            nat = "FR",
            location =
                LocationDto(
                    street = StreetDto(number = 10, name = "Rue de Paris"),
                    city = "Paris",
                    state = "Ile-de-France",
                    country = "France",
                    coordinates = CoordinatesDto(latitude = 48.85, longitude = 2.35),
                    postcode = "75001",
                    timezone =
                        TimezoneDto(
                            offset = "+1:00",
                            description = "Paris Time",
                        ),
                ),
            cell = "0611223344",
            gender = "male",
            id = IdDto(name = "INSEE", value = "123456789"),
            registered =
                RegisteredDto(
                    date = "2010-05-01T12:00:00Z",
                    age = 13,
                ),
        )

    @Test
    fun `dto toEntity - maps all fields correctly`() {
        val entity = dto.toEntity(page = 1)

        assertEquals("uuid-123", entity.id)
        assertEquals("John", entity.firstName)
        assertEquals("Doe", entity.lastName)
        assertEquals("john@doe.com", entity.email)
        assertEquals("0102030405", entity.phone)
        assertEquals("thumb.jpg", entity.pictureThumbnailUrl)
        assertEquals("large.jpg", entity.pictureLarge)
        assertEquals(42, entity.age)
        assertEquals("FR", entity.nationality)
        assertEquals("10 Rue de Paris Paris Ile-de-France France", entity.address)
        assertEquals(48.85, entity.latitude)
        assertEquals(2.35, entity.longitude)
        assertEquals("1990-01-01T00:00:00Z", entity.birthDate)
        assertEquals(1, entity.page)
    }

    @Test
    fun `dto toEntity - image fallback works`() {
        val dto2 =
            dto.copy(
                picture =
                    PictureDto(
                        thumbnail = null,
                        medium = "medium.jpg",
                        large = null,
                    ),
            )

        val entity = dto2.toEntity(page = 2)

        assertEquals("medium.jpg", entity.pictureThumbnailUrl)
        assertEquals("medium.jpg", entity.pictureLarge)
    }

    @Test
    fun `dto toEntity - handles null values correctly`() {
        val dto3 =
            dto.copy(
                login = dto.login.copy(uuid = "uuid-789"),
                name = null,
                email = null,
                phone = null,
                picture = null,
                dob = null,
                nat = null,
                location = null,
            )

        val entity = dto3.toEntity(page = 3)

        assertEquals("uuid-789", entity.id)
        assertNull(entity.firstName)
        assertNull(entity.lastName)
        assertNull(entity.email)
        assertNull(entity.phone)
        assertNull(entity.pictureThumbnailUrl)
        assertNull(entity.pictureLarge)
        assertNull(entity.age)
        assertNull(entity.nationality)
        assertEquals("", entity.address)
        assertNull(entity.latitude)
        assertNull(entity.longitude)
        assertNull(entity.birthDate)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `entity toDomain - maps correctly and parses birthDate`() {
        val entity =
            RandomUserEntity(
                id = "uuid-123",
                firstName = "Jane",
                lastName = "Smith",
                email = "jane@smith.com",
                phone = "0606060606",
                pictureThumbnailUrl = "thumb.jpg",
                pictureLarge = "large.jpg",
                age = 30,
                nationality = "US",
                address = "123 Main Street New York USA",
                latitude = 40.71,
                longitude = -74.00,
                birthDate = "1993-05-10T12:00:00Z",
                page = 1,
            )

        val domain = entity.toDomain()

        assertEquals("uuid-123", domain.id)
        assertEquals("Jane Smith", domain.fullName)
        assertEquals("jane@smith.com", domain.email)
        assertEquals("0606060606", domain.phone)
        assertEquals("thumb.jpg", domain.pictureThumbnailUrl)
        assertEquals("large.jpg", domain.pictureLarge)
        assertEquals(30, domain.age)
        assertEquals("US", domain.nationality)
        assertEquals("123 Main Street New York USA", domain.address)
        assertEquals(40.71, domain.latitude)
        assertEquals(-74.00, domain.longitude)
        assertEquals(Instant.parse("1993-05-10T12:00:00Z"), domain.birthDate)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `entity toDomain - fullName handles nulls`() {
        val entity =
            RandomUserEntity(
                id = "uuid-000",
                firstName = null,
                lastName = "OnlyLast",
                email = null,
                phone = null,
                pictureThumbnailUrl = null,
                pictureLarge = null,
                age = null,
                nationality = null,
                address = "",
                latitude = null,
                longitude = null,
                birthDate = null,
                page = 1,
            )

        val domain = entity.toDomain()

        assertEquals("OnlyLast", domain.fullName) // pas de crash
        assertNull(domain.birthDate)
    }
}
