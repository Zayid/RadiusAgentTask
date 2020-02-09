package com.task.radiusagent.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.task.radiusagent.util.Constants.REALM_DATABASE_NAME;

public class RadiusApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initRealmConfiguration();
    }

    private void initRealmConfiguration() {
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(REALM_DATABASE_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
