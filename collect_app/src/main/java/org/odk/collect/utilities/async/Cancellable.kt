package org.odk.collect.utilities.async

interface Cancellable {
    fun cancel(): Boolean
}
