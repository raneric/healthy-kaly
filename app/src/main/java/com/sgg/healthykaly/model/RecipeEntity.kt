package com.sgg.healthykaly.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
        @PrimaryKey val id: Int,
        val title: String,
        val image: String,
        @ColumnInfo(name = "image_type")
        val imageType: String,
        val calories: Int?,
        val protein: String?,
        val fat: String?,
        val carbs: String?
) {
}