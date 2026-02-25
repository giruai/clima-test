package com.giruai.climatest.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.giruai.climatest.domain.model.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentSearchesManager @Inject constructor(
    @ApplicationContext context: Context,
    private val gson: Gson
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("recent_searches", Context.MODE_PRIVATE)

    fun getRecentSearches(): List<City> {
        val json = prefs.getString(KEY_RECENT, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<City>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addRecentSearch(city: City) {
        val current = getRecentSearches().toMutableList()
        
        // Remove if already exists (to move to front)
        current.removeAll { it.id == city.id }
        
        // Add to front
        current.add(0, city)
        
        // Keep only 5 most recent
        val updated = current.take(MAX_RECENT)
        
        // Save
        val json = gson.toJson(updated)
        prefs.edit().putString(KEY_RECENT, json).apply()
    }

    fun clearRecentSearches() {
        prefs.edit().remove(KEY_RECENT).apply()
    }

    companion object {
        private const val KEY_RECENT = "recent_cities"
        private const val MAX_RECENT = 5
    }
}
