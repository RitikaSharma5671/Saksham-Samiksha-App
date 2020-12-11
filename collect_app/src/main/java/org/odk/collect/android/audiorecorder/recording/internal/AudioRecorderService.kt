package org.odk.collect.android.audiorecorder.recording.internal

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.odk.collect.android.async.Cancellable
import org.odk.collect.android.async.Scheduler
import org.odk.collect.android.audiorecorder.AudioInitialiser
import org.odk.collect.android.audiorecorder.recorder.Output
import org.odk.collect.android.audiorecorder.recorder.Recorder
import org.odk.collect.android.audiorecorder.recorder.RecordingException

internal class AudioRecorderService : Service() {

     internal lateinit var recorder: Recorder

     internal lateinit var recordingRepository: RecordingRepository

     internal lateinit var scheduler: Scheduler

    private lateinit var notification: RecordingForegroundServiceNotification
    private var duration = 0L
    private var durationUpdates: Cancellable? = null
    private var amplitudeUpdates: Cancellable? = null

    override fun onCreate() {
        super.onCreate()
        scheduler = AudioInitialiser.providesScheduler()
        recorder = AudioInitialiser.providesRecorder()
        recordingRepository = AudioInitialiser.providesRecordingRepository()
        notification = RecordingForegroundServiceNotification(this, recordingRepository)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
                val output = intent.getSerializableExtra(EXTRA_OUTPUT) as Output

                if (!recorder.isRecording() && sessionId != null) {
                    startRecording(sessionId, output)
                }
            }

            ACTION_PAUSE -> {
                recorder.pause()
                recordingRepository.setPaused(true)

                stopUpdates()
            }

            ACTION_RESUME -> {
                recorder.resume()
                recordingRepository.setPaused(false)

                startUpdates()
            }

            ACTION_STOP -> {
                stopRecording()
            }

            ACTION_CLEAN_UP -> {
                cleanUp()
            }
        }

        return START_STICKY
    }

    private fun startRecording(sessionId: String, output: Output) {
        notification.show()

        try {
            recorder.start(output)
            recordingRepository.start(sessionId)
            startUpdates()
        } catch (e: RecordingException) {
            notification.dismiss()
            recordingRepository.failToStart(sessionId)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        cleanUp()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun stopRecording() {
        stopUpdates()
        notification.dismiss()

        val file = recorder.stop()
        recordingRepository.recordingReady(file)
    }

    private fun cleanUp() {
        stopUpdates()
        notification.dismiss()

        recorder.cancel()
        recordingRepository.clear()
    }

    private fun startUpdates() {
        durationUpdates = scheduler.repeat(
            {
                recordingRepository.setDuration(duration)
                duration += 1000
            },
            1000L
        )

        amplitudeUpdates = scheduler.repeat({ recordingRepository.setAmplitude(recorder.amplitude) }, 100L)
    }

    private fun stopUpdates() {
        amplitudeUpdates?.cancel()
        durationUpdates?.cancel()
    }

    companion object {
        const val ACTION_START = "START"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_RESUME = "RESUME"
        const val ACTION_STOP = "STOP"
        const val ACTION_CLEAN_UP = "CLEAN_UP"

        const val EXTRA_SESSION_ID = "EXTRA_SESSION_ID"
        const val EXTRA_OUTPUT = "EXTRA_OUTPUT"
    }
}
