package net.codinux.log

import io.quarkus.runtime.Startup
import jakarta.inject.Singleton

@Singleton
@Startup
class QuarkusConfiguration {

    private val log by logger()


    init {
        showLogExample()
    }

    private fun showLogExample() {

        // produce some log outputs so that user can play with parameters in application.properties and sees what happens

        log.warn(Throwable("Test", Throwable("Inner #1", Throwable("Inner #2")))) { "Just a test, no animals have been harmed" }
    }

}