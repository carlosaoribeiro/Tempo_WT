package com.carlosribeiro.tempo_wt

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.carlosribeiro.tempo_wt.ui.MainActivity

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.java

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkHelloWorldIsDisplayed() {
        // Esse teste procura pelo texto "HWeather App!" na tela inicial
        onView(withText("Weather App"))
            .check(matches(isDisplayed()))
    }
}
