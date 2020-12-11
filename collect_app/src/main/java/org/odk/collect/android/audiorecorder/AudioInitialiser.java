package org.odk.collect.android.audiorecorder;

import android.app.Application;
import android.content.Context;

import org.odk.collect.android.async.Scheduler;
import org.odk.collect.android.audiorecorder.recorder.Recorder;
import org.odk.collect.android.audiorecorder.recorder.RecordingResourceRecorder;
import org.odk.collect.android.audiorecorder.recording.internal.RecordingRepository;

import java.io.File;

public class AudioInitialiser {

    private static Context jjj;
    private static Application app;

    public static  void setiini(Context context, Application application) {
        jjj = context;
        app = application;
    }

    public static Context getJjj() {
        return jjj;
    }

//    @Provides
//    open fun providesCacheDir(application: Application): File {
//        val externalFilesDir = application.getExternalFilesDir(null)
//        return File(externalFilesDir, "recordings").also { it.mkdirs() }
//    }
//    @Provides
//    open fun providesRecorder(cacheDir: File): Recorder {
//        return RecordingResourceRecorder(cacheDir) { output ->
//                when (output) {
//            Output.AMR -> {
//                AMRRecordingResource(MediaRecorder())
//            }
//
//            Output.AAC -> {
//                AACRecordingResource(MediaRecorder(), 64)
//            }
//
//            Output.AAC_LOW -> {
//                AACRecordingResource(MediaRecorder(), 24)
//            }
//        }
//        }
//    }
//
//    @Provides
//    @Singleton
//    open fun providesRecordingRepository(): RecordingRepository {
//        return RecordingRepository()
//    }

    public static RecordingRepository providesRecordingRepository() {
        return new RecordingRepository();
    }

    public static Recorder providesRecorder() {
       return new AudioInit().jj(jjj, app);
    }


    public static Scheduler providesScheduler( )  {
       return new AudioInit().getScheduler();
    }
}
