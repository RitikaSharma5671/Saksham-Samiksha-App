package org.odk.collect.android.audiorecorder

import android.app.Application
import android.content.Context
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import org.odk.collect.android.async.CoroutineScheduler
import org.odk.collect.android.async.Scheduler
import org.odk.collect.android.audiorecorder.mediarecorder.AACRecordingResource
import org.odk.collect.android.audiorecorder.mediarecorder.AMRRecordingResource
import org.odk.collect.android.audiorecorder.recorder.Output
import org.odk.collect.android.audiorecorder.recorder.Recorder
import org.odk.collect.android.audiorecorder.recorder.RecordingResourceRecorder
import java.io.File

class AudioInit() {

    fun getScheduler(): Scheduler {
        return CoroutineScheduler(Dispatchers.Main, Dispatchers.IO)
    }

    fun jj(jjj: Context, app: Application): Recorder {
        val externalFilesDir = app.getExternalFilesDir(null)
        val cacheDir = File(externalFilesDir, "recordings").also { it.mkdirs() }
        return RecordingResourceRecorder(cacheDir) { output ->
            when (output) {
                Output.AMR -> {
                    AMRRecordingResource(MediaRecorder())
                }

                Output.AAC -> {
                    AACRecordingResource(MediaRecorder(), 64)
                }

                Output.AAC_LOW -> {
                    AACRecordingResource(MediaRecorder(), 24)
                }
            }
        }
    }

}