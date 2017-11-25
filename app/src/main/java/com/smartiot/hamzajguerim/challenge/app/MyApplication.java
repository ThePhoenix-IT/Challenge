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
    private final String SUBSCRIPTION_KEY = "";
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
        faceServiceClient =
                new FaceServiceRestClient(SUBSCRIPTION_KEY);

    }
}
