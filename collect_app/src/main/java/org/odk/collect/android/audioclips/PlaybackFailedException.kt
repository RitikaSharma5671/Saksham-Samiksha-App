package org.odk.collect.android.audioclips

data class PlaybackFailedException(val uRI: String, val exceptionMsg: Int) : Exception()
