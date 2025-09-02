package com.bvic.contacto.data.repository

import com.bvic.contacto.core.Error
import com.bvic.contacto.core.Result
import com.bvic.contacto.core.network.NetworkError
import com.bvic.contacto.data.local.dao.RandomUserDao
import com.bvic.contacto.data.local.entity.RandomUserEntity
import com.bvic.contacto.data.remote.api.RandomUserApi
import com.bvic.contacto.data.remote.dto.CoordinatesDto
import com.bvic.contacto.data.remote.dto.DobDto
import com.bvic.contacto.data.remote.dto.IdDto
import com.bvic.contacto.data.remote.dto.InfoDto
import com.bvic.contacto.data.remote.dto.LocationDto
import com.bvic.contacto.data.remote.dto.LoginDto
import com.bvic.contacto.data.remote.dto.NameDto
import com.bvic.contacto.data.remote.dto.PictureDto
import com.bvic.contacto.data.remote.dto.RandomUserDto
import com.bvic.contacto.data.remote.dto.RegisteredDto
import com.bvic.contacto.data.remote.dto.ResponseDto
import com.bvic.contacto.data.remote.dto.StreetDto
import com.bvic.contacto.data.remote.dto.TimezoneDto
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.time.ExperimentalTime

class RandomUserRepositoryImplTest {
    private val api: RandomUserApi = mockk()
    private val dao: RandomUserDao = mockk(relaxed = true)
    private val repo = RandomUserRepositoryImpl(api, dao)

    suspend fun <T, E : Error> Flow<Result<T, E>>.collectToList(): List<Result<T, E>> {
        val results = mutableListOf<Result<T, E>>()
        collect { results.add(it) }
        return results
    }

    private fun dto(
        uuid: String = "uuid-1",
        first: String? = "John",
        last: String? = "Doe",
        lat: Double? = 48.85,
        lon: Double? = 2.35,
        birth: String? = "1990-01-01T00:00:00Z",
    ) = RandomUserDto(
        login =
            LoginDto(
                uuid = uuid,
                md5 = "m",
                password = "p",
                salt = "s",
                sha1 = "1",
                sha256 = "256",
                username = "u",
            ),
        name = NameDto(title = "Mr", first = first, last = last),
        email = "john@doe.com",
        phone = "0102030405",
        picture = PictureDto(thumbnail = "thumb", medium = "med", large = "large"),
        dob = DobDto(age = 30, date = birth),
        nat = "FR",
        location =
            LocationDto(
                street = StreetDto(number = 10, name = "Rue"),
                city = "Paris",
                state = "IDF",
                country = "France",
                coordinates = CoordinatesDto(latitude = lat, longitude = lon),
                postcode = "75001",
                timezone = TimezoneDto(offset = "+1:00", description = "Paris"),
            ),
        cell = "0611223344",
        gender = "male",
        id = IdDto(name = "INSEE", value = "123"),
        registered = RegisteredDto(date = "2010-05-01T12:00:00Z", age = 13),
    )

    private fun responseDto(results: List<RandomUserDto>) = ResponseDto(results = results, info = null)

    private fun entity(
        id: String = "uuid-1",
        page: Int = 1,
    ) = RandomUserEntity(
        id = id,
        firstName = "John",
        lastName = "Doe",
        email = "john@doe.com",
        phone = "0102030405",
        pictureThumbnailUrl = "thumb",
        pictureLarge = "large",
        age = 30,
        nationality = "FR",
        address = "10 Rue Paris IDF France",
        latitude = 48.85,
        longitude = 2.35,
        birthDate = "1990-01-01T00:00:00Z",
        page = page,
    )

    @Test
    fun `empty cache - fetch network - save - success`() =
        runTest {
            coEvery { dao.getPage(1) } returnsMany listOf(emptyList(), listOf(entity(page = 1)))
            coEvery {
                api.getRandomUsers(
                    seed = "lydia",
                    results = 20,
                    page = 1,
                )
            } returns
                Response.success(
                    ResponseDto(
                        results = listOf(dto()),
                        info =
                            InfoDto(
                                seed = "lydia",
                                results = 1,
                                page = 1,
                                version = "1.4",
                            ),
                    ),
                )
            coEvery { dao.insertAll(any()) } just Runs

            val emissions =
                repo
                    .fetchRandomUserPage(page = 1, pageSize = 20, forceRefresh = false)
                    .collectToList()

            assertThat(emissions).hasSize(2)
            assertThat(emissions[0]).isInstanceOf(Result.Loading::class.java)
            val success = emissions[1] as Result.Success
            assertThat(success.data).hasSize(1)
            coVerifySequence {
                dao.getPage(1)
                api.getRandomUsers(seed = "lydia", results = 20, page = 1)
                dao.insertAll(withArg { list -> assertThat(list).hasSize(1) })
                dao.getPage(1)
            }
        }

    @Test
    fun `cache present and forceRefresh=false - returns cache without network`() =
        runTest {
            val cached = listOf(entity())
            coEvery { dao.getPage(1) } returns cached

            val emissions =
                repo
                    .fetchRandomUserPage(page = 1, pageSize = 20, forceRefresh = false)
                    .collectToList()

            assertThat((emissions[1] as Result.Success).data).hasSize(1)
            coVerify(exactly = 0) { api.getRandomUsers(any(), any(), any()) }
        }

    @Test
    fun `forceRefresh=true - deleteAll then insert`() =
        runTest {
            coEvery { dao.getPage(1) } returnsMany listOf(emptyList(), listOf(entity()))
            coEvery { api.getRandomUsers(seed = "lydia", results = 20, page = 1) } returns
                Response.success(
                    responseDto(listOf(dto())),
                )
            coEvery { dao.deleteAllRandomUsers() } just Runs
            coEvery { dao.insertAll(any()) } just Runs

            repo.fetchRandomUserPage(page = 1, pageSize = 20, forceRefresh = true).collectToList()

            coVerifyOrder {
                dao.getPage(1)
                api.getRandomUsers(seed = "lydia", results = 20, page = 1)
                dao.deleteAllRandomUsers()
                dao.insertAll(any())
                dao.getPage(1)
            }
        }

    @Test
    fun `IOException - returns NetworkError_NoInternet`() =
        runTest {
            coEvery { dao.getPage(1) } returns emptyList()
            coEvery {
                api.getRandomUsers(
                    seed = "lydia",
                    results = 20,
                    page = 1,
                )
            } throws java.io.IOException()

            val emissions = repo.fetchRandomUserPage(1, 20, false).collectToList()

            val err = emissions[1] as Result.Error
            assertThat(err.error).isEqualTo(NetworkError.NoInternet)
        }

    @Test
    fun `HTTP 404 - returns NetworkError_NotFound`() =
        runTest {
            coEvery { dao.getPage(1) } returns emptyList()
            coEvery { api.getRandomUsers(seed = "lydia", results = 20, page = 1) } returns
                Response.error(
                    404,
                    "".toResponseBody(null),
                )

            val emissions = repo.fetchRandomUserPage(1, 20, false).collectToList()

            val err = emissions[1] as Result.Error
            assertThat(err.error).isEqualTo(NetworkError.NotFound)
        }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `full mapping to domain - lat lon birthDate`() =
        runTest {
            coEvery { dao.getPage(1) } returnsMany listOf(emptyList(), listOf(entity()))
            coEvery { api.getRandomUsers(seed = "lydia", results = 20, page = 1) } returns
                Response.success(
                    responseDto(
                        listOf(
                            dto(
                                lat = 48.85,
                                lon = 2.35,
                                birth = "1990-01-01T00:00:00Z",
                            ),
                        ),
                    ),
                )
            coEvery { dao.insertAll(any()) } just Runs

            val emissions = repo.fetchRandomUserPage(1, 20, false).collectToList()
            val user = (emissions[1] as Result.Success).data.first()

            assertThat(user.fullName).isEqualTo("John Doe")
            assertThat(user.latitude).isWithin(1e-6).of(48.85)
            assertThat(user.longitude).isWithin(1e-6).of(2.35)
            assertThat(user.birthDate!!.toString()).isEqualTo("1990-01-01T00:00:00Z")
        }

    @Test
    fun `searchLocalUsers trims input and appends percent - emits Loading then Success`() =
        runTest {
            val entities = listOf(entity(id = "u1"), entity(id = "u2"))
            coEvery { dao.search("john%") } returns entities

            val emissions = repo.searchLocalUsers("  john ").collectToList()

            assertThat(emissions[0]).isInstanceOf(Result.Loading::class.java)
            val success = emissions[1] as Result.Success
            assertThat(success.data).hasSize(2)
            coVerify { dao.search("john%") }
        }

    @Test
    fun `getLocalUser when not found - emits Error NotFound`() =
        runTest {
            coEvery { dao.getRandomUserById("missing") } returns null

            val emissions = repo.getLocalUser("missing").collectToList()

            val err = emissions[1] as Result.Error
            assertThat(err.error).isEqualTo(NetworkError.NotFound)
        }

    @Test
    fun `getLocalUser when found - emits Success with domain`() =
        runTest {
            coEvery { dao.getRandomUserById("uuid-1") } returns entity(id = "uuid-1")

            val emissions = repo.getLocalUser("uuid-1").collectToList()

            val success = emissions[1] as Result.Success
            assertThat(success.data.id).isEqualTo("uuid-1")
        }

    @Test
    fun `cleanContact calls deleteAllRandomUsers once`() =
        runTest {
            coEvery { dao.deleteAllRandomUsers() } just Runs

            repo.cleanContact()

            coVerify(exactly = 1) { dao.deleteAllRandomUsers() }
        }
}
