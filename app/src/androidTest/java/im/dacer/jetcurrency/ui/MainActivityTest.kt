package im.dacer.jetcurrency.ui

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import im.dacer.jetcurrency.ui.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
@HiltAndroidTest
class MainActivityTest {
    private var activityScenario: ActivityScenario<MainActivity>? = null

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java).run {
            activityScenario = this
            moveToState(Lifecycle.State.RESUMED)
        }
    }

    @After
    fun clear() {
        activityScenario?.moveToState(Lifecycle.State.DESTROYED)
    }

    // TODO
}
