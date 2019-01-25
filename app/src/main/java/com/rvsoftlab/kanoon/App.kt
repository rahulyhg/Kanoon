package com.rvsoftlab.kanoon

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.zxy.tiny.Tiny
import io.realm.Realm
import io.realm.RealmConfiguration

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        //region REALM INIT
        Realm.init(this)
        val config:RealmConfiguration =RealmConfiguration.Builder()
                .name("kanoon.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
        //endregion
        //region FIREBASE INIT
        val firestore = FirebaseFirestore.getInstance()
        val firebaseSetting = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        firestore.firestoreSettings = firebaseSetting
        //endregion


    }
}