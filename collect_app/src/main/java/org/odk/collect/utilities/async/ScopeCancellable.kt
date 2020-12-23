package org.odk.collect.utilities.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.odk.collect.utilities.async.Cancellable

internal class ScopeCancellable(private val scope: CoroutineScope) : Cancellable {

    override fun cancel(): Boolean {
        scope.cancel()
        return true
    }
}
