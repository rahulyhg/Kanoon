package com.rvsoftlab.kanoon.helper

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.content.Context
import com.rvsoftlab.kanoon.models.User
import io.realm.Realm

class RealmHelper {
    companion object {
        private var instance:RealmHelper? = null
        fun with(fragment: Fragment):RealmHelper{
            if (instance == null) {
                instance = RealmHelper(fragment.activity.application)
            }
            return instance as RealmHelper
        }

        fun with(activity: Activity):RealmHelper{
            if (instance == null) {
                instance = RealmHelper(activity.application)
            }
            return instance as RealmHelper
        }

        fun with(application: Application):RealmHelper{
            if (instance == null) {
                instance = RealmHelper(application)
            }
            return instance as RealmHelper
        }

        fun with(context: Context):RealmHelper{
            return RealmHelper(context)
        }
    }

    private var realm:Realm

    constructor(application: Application){
        realm = Realm.getDefaultInstance()
    }

    constructor(context: Context){
        realm = Realm.getDefaultInstance()
    }

    fun saveUser(user: User) {
        realm.executeTransaction {
            it.insertOrUpdate(user)
        }
    }

    fun getUser():User{
        return realm.where(User::class.java).findFirst()!!
    }
}