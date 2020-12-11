package org.odk.collect.android.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.odk.collect.android.async.Cancellable

internal class ScopeCancellable(private val scope: CoroutineScope) : Cancellable {

    override fun cancel(): Boolean {
        scope.cancel()
        return true
    }
}
