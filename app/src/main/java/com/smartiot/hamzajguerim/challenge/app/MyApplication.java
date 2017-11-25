package com.smartiot.hamzajguerim.challenge.app;

import android.app.Application;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hamzajguerim on 2017-11-25.
 */

public class MyApplication extends Application {
    private final String FACE_API_SUBSCRIPTION_KEY = "71bd7167190141efafc2a84dc6e15849";
    private final String SPEAKER_API_SUBSCRIPTION_KEY = "6ddeebbff8ac419aa832843084838e92";
    private static FaceServiceClient faceServiceClient;

    public static FaceServiceClient getFaceServiceClient() {
        return faceServiceClient;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        faceServiceClient = new FaceServiceRestClient(FACE_API_SUBSCRIPTION_KEY);

    }
}
