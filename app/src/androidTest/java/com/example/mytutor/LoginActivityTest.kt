package com.example.mytutor

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytutor.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Test
    fun testLoginActivityViewsDisplayed() {
        ActivityScenario.launch(LoginActivity::class.java).use {
            onView(withId(R.id.email)).check(matches(isDisplayed()))
            onView(withId(R.id.password)).check(matches(isDisplayed()))
            onView(withId(R.id.loginBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.register)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testEnteringCredentials() {
        ActivityScenario.launch(LoginActivity::class.java).use {
            onView(withId(R.id.email))
                .perform(typeText("admin@gmail.com"), closeSoftKeyboard())

            onView(withId(R.id.password))
                .perform(typeText("admin123"), closeSoftKeyboard())

            onView(withId(R.id.loginBtn))
                .check(matches(isDisplayed()))
        }
    }
}
