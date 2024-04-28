package com.achawki.sequencetrainer.activity

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.achawki.sequencetrainer.R
import com.achawki.sequencetrainer.common.Difficulty
import com.achawki.sequencetrainer.di.GeneratorModule
import com.achawki.sequencetrainer.generator.GeneratorResult
import com.achawki.sequencetrainer.generator.SequenceGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton


@HiltAndroidTest
@UninstallModules(GeneratorModule::class)
class SequenceTrainerActivityTest {

    private lateinit var context: Context

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val scenarioRule = ActivityScenarioRule(SequenceTrainerActivity::class.java)

    @Module
    @InstallIn(SingletonComponent::class)
    object GeneratorTestModule {

        private var identifier = 0
        @Singleton
        @Provides
        fun provideGenerator(): SequenceGenerator {
            return SequenceTestGenerator()
        }

        class SequenceTestGenerator : SequenceGenerator {
            override fun generateSequence(difficulty: Difficulty): Result<GeneratorResult> {
                return Result.success(
                    GeneratorResult(
                        listOf(1, 5, 9, 13, 17),
                        listOf("1 + 4", "5 + 4", "9 + 4", "13 + 4"),
                        // identifier needs to be unique
                        identifier++.toString()
                    )
                )
            }
        }
    }

    @Before
    fun setup() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testSubmitSolution_correctAnswer() {
        onView(withId(R.id.txtView_sequence)).check(matches(withText("1  5  9  13  ?")))
        onView(withId(R.id.edittxt_solution)).perform(typeTextIntoFocusedView("17"), pressImeActionButton())
        onView(withText("17 ".plus(context.getString(R.string.correct_answer)))).check(matches(isDisplayed()))
    }

    @Test
    fun testSubmitSolution_wrongAnswer() {
        onView(withId(R.id.edittxt_solution)).perform(typeTextIntoFocusedView("2"), pressImeActionButton())
        onView(withText("2 ".plus(context.getString(R.string.wrong_answer)))).check(matches(isDisplayed()))
    }

    @Test
    fun testSurrender_cancelDialog() {
        onView(withId(R.id.btn_surrender)).perform(click())
        onView(withText(R.string.surrender_confirmation)).check(matches(isDisplayed()))
        onView(withText(R.string.cancel)).perform(click())
        onView(withText("17 ".plus(context.getString(R.string.correct_solution)))).check(doesNotExist())
    }

    @Test
    fun testSurrender_confirmDialog() {
        onView(withId(R.id.btn_surrender)).perform(click())
        onView(withText(R.string.surrender_confirmation)).check(matches(isDisplayed()))
        onView(withText(R.string.surrender_confirmation)).perform(click())
        onView(withText("17 ".plus(context.getString(R.string.correct_solution)))).check(matches(isDisplayed()))
        onView(withId(R.id.txtView_solution_path)).check(matches(withText("1\n1 + 4 = 5\n5 + 4 = 9\n9 + 4 = 13\n13 + 4 = 17\n")))
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun clearDatabase() {
            InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase("sequence-db")
        }
    }

}