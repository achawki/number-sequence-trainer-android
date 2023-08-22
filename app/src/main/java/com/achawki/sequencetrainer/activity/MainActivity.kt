package com.achawki.sequencetrainer.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.achawki.sequencetrainer.R
import com.achawki.sequencetrainer.dialog.renderSettingsDialog
import com.achawki.sequencetrainer.dialog.renderStatisticsDialog
import com.achawki.sequencetrainer.viewmodel.SequenceTrainerViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val sequenceViewModel: SequenceTrainerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeButtons()
    }

    private fun initializeButtons() {
        val playButton = findViewById<Button>(R.id.btn_play)
        playButton.setOnClickListener {
            val intent = Intent(this, SequenceTrainerActivity::class.java)
            startActivity(intent)
        }
        val settingsButton = findViewById<Button>(R.id.btn_settings)
        settingsButton.setOnClickListener {
            renderSettingsDialog(this)
        }

        val statisticsButton = findViewById<Button>(R.id.btn_statistics)
        statisticsButton.setOnClickListener {
            lifecycleScope.launch {
                renderStatisticsDialog(this@MainActivity, sequenceViewModel.getStatistics())
            }
        }

        val licensesButton = findViewById<Button>(R.id.btn_licenses)
        licensesButton.setOnClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        }

        val aboutButton = findViewById<Button>(R.id.btn_about)

        aboutButton.setOnClickListener {
            val aboutTextView = TextView(this)
            val message = SpannableString(getString(R.string.about_text))
            Linkify.addLinks(message, Linkify.WEB_URLS)
            aboutTextView.text = message
            aboutTextView.setPadding(10)
            aboutTextView.movementMethod = LinkMovementMethod.getInstance()
            aboutTextView.setLinkTextColor(Color.BLUE)
            MaterialAlertDialogBuilder(this).setTitle(getString(R.string.about)).setView(aboutTextView)
                .setNeutralButton(getString(R.string.close)) { _, _ ->
                }.show()
        }


    }
}

