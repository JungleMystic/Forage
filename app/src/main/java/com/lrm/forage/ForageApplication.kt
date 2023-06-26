package com.lrm.forage

import android.app.Application
import com.lrm.forage.database.ItemRoomDatabase

class ForageApplication: Application() {
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}