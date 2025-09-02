package com.bvic.contacto.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import com.bvic.contacto.data.local.entity.RandomUserEntity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Database(
    entities = [RandomUserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TestRandomUserDb : RoomDatabase() {
    abstract fun randomUserDao(): RandomUserDao
}

class RandomUserDaoTest {
    private lateinit var db: TestRandomUserDb
    private lateinit var dao: RandomUserDao

    @BeforeEach
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db =
            Room
                .inMemoryDatabaseBuilder(context, TestRandomUserDb::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.randomUserDao()
    }

    @AfterEach
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAll_replace_deDuplicatesById_keepsLastOne() =
        runTest {
            val v1 = entity(id = "same-id", firstName = "Alice", lastName = "A", page = 1)
            val v2 = entity(id = "same-id", firstName = "Bob", lastName = "B", page = 1)

            dao.insertAll(listOf(v1))
            dao.insertAll(listOf(v2)) // should replace v1

            val page = dao.getPage(1)
            assertEquals(1, page.size)
            assertEquals("Bob", page.first().firstName)
            assertEquals("B", page.first().lastName)
        }

    @Test
    fun getPage_returnsOnlyRequestedPage() =
        runTest {
            dao.insertAll(
                listOf(
                    entity(id = "p1-1", page = 1),
                    entity(id = "p1-2", page = 1),
                    entity(id = "p2-1", page = 2),
                ),
            )

            val page1 = dao.getPage(1)
            val page2 = dao.getPage(2)

            assertEquals(2, page1.size)
            assertEquals(1, page2.size)
            assertEquals(setOf("p1-1", "p1-2"), page1.map { it.id }.toSet())
            assertEquals("p2-1", page2.first().id)
        }

    @Test
    fun search_appliesLikeOnFields_and_ordersByLastAndFirstName() =
        runTest {
            dao.insertAll(
                listOf(
                    entity(
                        id = "1",
                        firstName = "John",
                        lastName = "Zeta",
                        email = "john@z.com",
                        phone = "0101",
                        page = 1,
                    ),
                    entity(
                        id = "2",
                        firstName = "Alice",
                        lastName = "Alpha",
                        email = "alice@a.com",
                        phone = "0202",
                        page = 1,
                    ),
                    entity(
                        id = "3",
                        firstName = "Bob",
                        lastName = "Beta",
                        email = "beta@b.com",
                        phone = "0303",
                        page = 1,
                    ),
                    entity(
                        id = "4",
                        firstName = "Zoe",
                        lastName = "Alpha",
                        email = "zoe@a.com",
                        phone = "0404",
                        page = 1,
                    ),
                ),
            )

            val results = dao.search("a%")

            assertEquals(listOf("2", "4"), results.map { it.id })
        }

    private fun entity(
        id: String,
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "john@doe.com",
        phone: String = "0102030405",
        pictureThumb: String? = "thumb",
        pictureLarge: String? = "large",
        age: Int? = 30,
        nationality: String? = "FR",
        address: String? = "10 Rue Paris IDF France",
        latitude: Double? = 48.85,
        longitude: Double? = 2.35,
        birthDate: String? = "1990-01-01T00:00:00Z",
        page: Int = 1,
    ) = RandomUserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        pictureThumbnailUrl = pictureThumb,
        pictureLarge = pictureLarge,
        age = age,
        nationality = nationality,
        address = address,
        latitude = latitude,
        longitude = longitude,
        birthDate = birthDate,
        page = page,
    )
}
