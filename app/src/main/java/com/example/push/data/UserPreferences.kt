package com.example.push.data

import android.content.Context

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(id: Int, name: String, role: String) {
        prefs.edit()
            .putInt("user_id", id)
            .putString("name", name)
            .putString("role", role)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun getUserName(): String = prefs.getString("name", "Гість") ?: "Гість"
    fun getUserRole(): String = prefs.getString("role", "Без ролі") ?: "Без ролі"
}