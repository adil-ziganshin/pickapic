package com.example.pickapic

import android.os.Build
import android.os.ParcelFileDescriptor
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
 * User journey: on Home, enter a search query in the bar, tap the search action,
 * and land on the Pictures screen where the result title matches the query.
 *
 * This is a different flow from [HomeFavoritesUserJourneyTest] (Favorites FAB).
 * Uses UiAutomator only (no Espresso) for API 35+ compatibility.
 */
@RunWith(AndroidJUnit4::class)
class HomeSearchToPicturesJourneyTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun user_searchesFromHome_opensPicturesWithSameTitle() {
        val dev = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        assertTrue("Home (Topics) should load", dev.wait(Until.hasObject(By.text("Topics")), 30_000L))

        // Compose TextField: prefer semantics hint, else visible placeholder text
        val searchNode = if (dev.wait(Until.hasObject(By.hint("Search here...")), 3_000L)) {
            dev.findObject(By.hint("Search here..."))
        } else {
            assertTrue("Search placeholder", dev.wait(Until.hasObject(By.text("Search here...")), 15_000L))
            dev.findObject(By.text("Search here..."))
        }
        assertNotNull("Search field", searchNode)
        searchNode.click()
        // Short query: PicturesViewModel uses it as the screen title (length < 14)
        val query = "sun"
        // setText on the hint/placeholder node does not fill a Compose TextField; type into focus
        typeQueryIntoFocusedField(query)

        val searchAction = dev.findObject(By.desc("Search Icon"))
        assertNotNull("Search action (trailing icon)", searchAction)
        searchAction.click()

        assertTrue(
            "Pictures screen should show the topic as title (or see API/network error)",
            dev.wait(
                Until.hasObject(By.text(Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE))),
                60_000L
            )
        )
    }

    private fun typeQueryIntoFocusedField(query: String) {
        val inst = InstrumentationRegistry.getInstrumentation()
        // Let Compose move focus to the text field
        Thread.sleep(400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            inst.sendStringSync(query)
        } else {
            // ASCII-only: spaces as %s for "input text"
            val arg = query.replace(" ", "%s")
            inst.uiAutomation.executeShellCommand("input text $arg").useQuietly()
        }
    }
}

/**
 * [ParcelFileDescriptor] from [android.app.UiAutomation.executeShellCommand] must be closed;
 * the stream may be read or ignored, but the descriptor should not leak.
 */
private fun ParcelFileDescriptor?.useQuietly() {
    this?.close()
}
