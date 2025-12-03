package com.example.submgr

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configs")
data class ConfigItem(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val raw: String,
    val type: String,
    val remark: String? = null,
    var enabled: Boolean = false
)
