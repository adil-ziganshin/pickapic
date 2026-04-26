package com.example.pickapic

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern

/**
 * Сценарий: главный экран → Favorites → заголовок избранного.
 *
 * Без [androidx.compose.ui.test] / Espresso: на API 35+ Espresso 3.6 падает на
 * InputManager.getInstance; UiAutomator ходит по accessibility-дереву и не
 * трогает Espresso.
 */
@RunWith(AndroidJUnit4::class)
class HomeFavoritesUserJourneyTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun user_opensFavoritesFromHome() {
        val dev = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        assertTrue(
            "Topics should appear on home",
            dev.wait(Until.hasObject(By.text("Topics")), 30_000L)
        )
        assertTrue(
            "Favorites FAB should appear",
            dev.wait(Until.hasObject(By.text("Favorites")), 30_000L)
        )
        val fab = dev.findObject(By.text("Favorites"))
        assertNotNull("Favorites node", fab)
        fab.click()
        assertTrue(
            "Favorites screen with title",
            dev.wait(
                Until.hasObject(By.text(Pattern.compile(".*favorite.*", Pattern.CASE_INSENSITIVE))),
                20_000L
            )
        )
    }
}
