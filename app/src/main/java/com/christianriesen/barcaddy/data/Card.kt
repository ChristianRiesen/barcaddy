package com.christianriesen.barcaddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CodeKind { BARCODE, QR }

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: String,
    val name: String,
    val value: String,
    val kind: CodeKind,
    val format: String,
    val description: String,
    val palette: String,
    val monogram: String,
    val position: Int,
)
