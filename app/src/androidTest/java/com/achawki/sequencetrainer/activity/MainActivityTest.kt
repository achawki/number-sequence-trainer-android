package com.achawki.sequencetrainer.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.achawki.sequencetrainer.R
import com.achawki.sequencetrainer.common.SequenceStatus
import com.achawki.sequencetrainer.data.AppDatabase
import com.achawki.sequencetrainer.data.Sequence
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val scenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var database: AppDatabase

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
    fun testResetStatisticsButtonWhenNoStatistics() {
        onView(withId(R.id.btn_statistics)).perform(click())

        onView(withText(R.string.no_data_available)).check(matches(isDisplayed()))
        onView(withText(R.string.reset_statistics)).check(doesNotExist())
        onView(withText(R.string.close)).perform(click())
    }

    @Test
    fun testResetStatisticsWithData() {
        runBlocking {
            val sequenceDao = database.sequenceDao()

            sequenceDao.insert(
                Sequence(
                    solution = 5,
                    question = "1 2 3 4 ?",
                    difficulty = 1,
                    status = SequenceStatus.SOLVED,
                    failedAttempts = 1,
                    identifier = "test1",
                    solutionPaths = listOf("1+1", "2+1", "3+1", "4+1")
                )
            )
            
            sequenceDao.insert(
                Sequence(
                    solution = 6,
                    question = "2 3 4 5 ?",
                    difficulty = 1,
                    status = SequenceStatus.GIVEN_UP,
                    failedAttempts = 3,
                    identifier = "test2",
                    solutionPaths = listOf("2+1", "3+1", "4+1", "5+1")
                )
            )

            val initialStats = sequenceDao.getStatistics(SequenceStatus.SOLVED.name, SequenceStatus.ONGOING.name)
            assert(initialStats.isNotEmpty()) { "Statistics should exist before test" }
        }

        onView(withId(R.id.btn_statistics)).perform(click())

        onView(withText(R.string.reset_statistics)).check(matches(isDisplayed()))

        onView(withText(R.string.reset_statistics)).perform(click())
        

        onView(withText(R.string.reset_statistics_confirmation)).check(matches(isDisplayed()))
        onView(withText(R.string.reset)).perform(click())

        runBlocking {
            val statistics = database.sequenceDao().getStatistics(SequenceStatus.SOLVED.name, SequenceStatus.ONGOING.name)
            assert(statistics.isEmpty()) { "Statistics should be empty after reset" }
        }
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