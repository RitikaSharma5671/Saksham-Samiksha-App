package org.odk.collect.async

import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Run and schedule tasks in the foreground (UI thread) and background. Based on terminology
 * used in Android's Background Processing documentation: https://developer.android.com/guide/background
 */
interface Scheduler {

    /**
     * Run a task in the background (off the UI thread). Cancelled if application closed.
     *
     * @param background the task to be run
     * @param foreground run on the foreground once the task is complete
     */
    fun <T> immediate(background: Supplier<T>, foreground: Consumer<T>)

    /**
     * Schedule a task to run in the background repeatedly even if the app isn't running. The task
     * will only be run when the network is available.
     *
     * @param tag used to identify this task in future. Previously scheduled tasks using the same
     * tag will be replaced
     * @param spec defines the task to be run
     * @param repeatPeriod the period between each run of the task
     */
    fun networkDeferred(tag: String, spec: TaskSpec, repeatPeriod: Long)

    /**
     * Cancel deferred task scheduled with tag
     */
    fun cancelDeferred(tag: String)

    /**
     * Returns true if a deferred task scheduled with tag is currently running
     */
    fun isRunning(tag: String): Boolean

    /**
     * Run a task and then repeat in the foreground
     *
     * @param foreground the task to be run
     * @param repeatPeriod the period between each run of the task
     * @return object that allows task to be cancelled
     */
    fun repeat(foreground: Runnable, repeatPeriod: Long): Cancellable
}