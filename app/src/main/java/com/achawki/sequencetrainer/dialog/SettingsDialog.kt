package com.achawki.sequencetrainer.dialog

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import com.achawki.sequencetrainer.R
import com.achawki.sequencetrainer.common.Difficulty
import com.achawki.sequencetrainer.common.SettingsKey
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider


fun renderSettingsDialog(context: Context) {
    val sharedPref = context.getSharedPreferences(SettingsKey.PREFERENCES, Context.MODE_PRIVATE)
    setupDifficultySetting(sharedPref, context)
}

private fun setupDifficultySetting(sharedPref: SharedPreferences, context: Context) {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_settings, null)

    val existingDifficulty = sharedPref.getInt(SettingsKey.DIFFICULTY, Difficulty.EASY.label)
    val difficultySlider = view.findViewById<Slider>(R.id.slider_difficulty)
    difficultySlider.value = existingDifficulty.toFloat()
    if (!difficultySlider.hasLabelFormatter()) {
        difficultySlider.setLabelFormatter { value ->
            context.getString(
                context.resources.getIdentifier(
                    "difficulty_${value.toInt()}",
                    "string",
                    context.packageName
                )
            )
        }
    }

    MaterialAlertDialogBuilder(context).setView(view).setTitle(
        context.getString(R.string.settings)
    )
        .setNeutralButton(context.getString(R.string.close)) { _, _ ->
            storeDifficulty(sharedPref, existingDifficulty, difficultySlider.value.toInt())
        }.setOnCancelListener {
            storeDifficulty(sharedPref, existingDifficulty, difficultySlider.value.toInt())
        }.show()
}

private fun storeDifficulty(sharedPref: SharedPreferences, oldDifficulty: Int, newDifficulty: Int) {
    if (newDifficulty != oldDifficulty) {
        with(sharedPref.edit()) {
            putInt(SettingsKey.DIFFICULTY, newDifficulty)
            apply()
        }
    }
}

