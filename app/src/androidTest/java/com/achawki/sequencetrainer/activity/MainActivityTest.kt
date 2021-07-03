package com.achawki.sequencetrainer.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.achawki.sequencetrainer.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val scenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
    }

    @Test
    fun testSettingButton() {
        onView(withId(R.id.btn_settings)).perform(click())

        onView(withId(R.id.slider_difficulty)).check(matches(isDisplayed()))
        onView(withText(R.string.close)).perform(click())
    }

    @Test
    fun testAboutButton() {
        onView(withId(R.id.btn_about)).perform(click())

        onView(withText(R.string.about)).check(matches(isDisplayed()))
        onView(withText(R.string.close)).perform(click())
    }

    @Test
    fun testStatisticsButton() {
        onView(withId(R.id.btn_statistics)).perform(click())

        onView(withText(R.string.statistics)).check(matches(isDisplayed()))
        onView(withText(R.string.close)).perform(click())
    }

    @Test
    fun testLicensesButton() {
        onView(withId(R.id.btn_licenses)).perform(click())
        intended(hasComponent(OssLicensesMenuActivity::class.java.name))
    }

    @Test
    fun testPlayButtonExistence() {
        // do not click to prevent side effects on other tests
        onView(withId(R.id.btn_play)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        Intents.release()
    }
}