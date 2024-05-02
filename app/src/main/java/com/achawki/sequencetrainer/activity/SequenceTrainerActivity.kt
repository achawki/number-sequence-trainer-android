package com.achawki.sequencetrainer.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import com.achawki.sequencetrainer.R
import com.achawki.sequencetrainer.common.Difficulty
import com.achawki.sequencetrainer.common.SequenceStatus
import com.achawki.sequencetrainer.common.SettingsKey
import com.achawki.sequencetrainer.data.Sequence
import com.achawki.sequencetrainer.data.toSequence
import com.achawki.sequencetrainer.generator.SequenceGenerator
import com.achawki.sequencetrainer.viewmodel.SequenceTrainerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SequenceTrainerActivity : AppCompatActivity() {

    @Inject
    lateinit var generator: SequenceGenerator
    private val sequenceViewModel: SequenceTrainerViewModel by viewModels()
    private lateinit var currentSequence: Sequence
    private var surrendered: Boolean = false
    private var difficulty: Int = 1
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequence_trainer)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        TextViewCompat.setAutoSizeTextTypeWithDefaults(
            findViewById(R.id.txtView_sequence),
            TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
        )
        TextViewCompat.setAutoSizeTextTypeWithDefaults(
            findViewById(R.id.txtView_solution_path),
            TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
        )

        val sharedPref = getSharedPreferences(SettingsKey.PREFERENCES, Context.MODE_PRIVATE)
        difficulty = sharedPref.getInt(SettingsKey.DIFFICULTY, Difficulty.EASY.label)

        lifecycleScope.launch {
            sequenceViewModel.getActiveSequence(difficulty)?.let {
                currentSequence = it
                populateRound()
            } ?: run {
                initializeRound()
            }
        }

        setupInputText()
        setupSurrenderButton()
    }

    override fun onDestroy() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        super.onDestroy()
    }

    private fun setupInputText() {
        val inputText = findViewById<EditText>(R.id.edittxt_solution)

        inputText.setOnEditorActionListener { view, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val providedAnswer = inputText.text.toString().trim()
                    if (providedAnswer.isNotEmpty()) handleAnswer(
                        inputText.text.toString(),
                        view
                    )
                    true
                }

                else -> false
            }
        }


        inputText.post {
            inputText.requestFocus()
        }

        val inputLayout = findViewById<TextInputLayout>(R.id.edittxtlayout_solution)
        inputLayout.setEndIconOnClickListener {
            val providedAnswer = inputText.text.toString().trim()
            if (providedAnswer.isNotEmpty()) handleAnswer(
                inputText.text.toString(), it
            )
        }
    }

    private fun setupSurrenderButton() {
        val surrenderButton = findViewById<Button>(R.id.btn_surrender)
        surrenderButton.setOnClickListener {
            if (!isFinishing) {
                if (surrendered) return@setOnClickListener
                dialog = MaterialAlertDialogBuilder(this).setTitle(getString(R.string.surrender))
                    .setMessage(getString(R.string.surrender_message))
                    .setNeutralButton(getString(R.string.cancel)) { _, _ ->
                    }.setPositiveButton(getString(R.string.surrender_confirmation)) { _, _ ->
                        surrendered = true
                        currentSequence.status = SequenceStatus.GIVEN_UP
                        sequenceViewModel.updateSequence(currentSequence)
                        findViewById<TextView>(R.id.txtView_solution_path).text = currentSequence.formatSolutionPaths()
                        Snackbar.make(
                            it,
                            "${currentSequence.solution} ".plus(getString(R.string.correct_solution)),
                            Snackbar.LENGTH_INDEFINITE
                        ).apply {
                            setAction(R.string.next_sequence) {
                                initializeRound()
                            }
                            setBackgroundTint(Color.LTGRAY)
                            anchorView = it
                        }.show()
                    }.show()
            }
        }
    }

    private fun handleAnswer(answer: String, view: View) {
        if (surrendered) return

        if (currentSequence.solution.toString() == answer) {
            Snackbar.make(view, "$answer ".plus(getString(R.string.correct_answer)), Snackbar.LENGTH_SHORT)
                .setBackgroundTint(
                    Color.GREEN
                ).setAnchorView(view).show()
            currentSequence.status = SequenceStatus.SOLVED
            sequenceViewModel.updateSequence(currentSequence)
            initializeRound()
        } else {
            Snackbar.make(view, "$answer ".plus(getString(R.string.wrong_answer)), Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .setAnchorView(view).show()
            currentSequence.failedAttempts++
            sequenceViewModel.updateSequence(currentSequence)
        }
        findViewById<EditText>(R.id.edittxt_solution).text.clear()
    }

    private fun populateRound() {
        findViewById<TextView>(R.id.txtView_sequence).text = currentSequence.question
        findViewById<EditText>(R.id.edittxt_solution).text.clear()
        surrendered = false
    }

    private fun initializeRound() {
        lifecycleScope.launch {
            tryPopulateRound()
        }
    }

    private suspend fun tryPopulateRound(retryCount: Int = 0, maxRetries: Int = 25) {
        if (retryCount >= maxRetries) {
            printGenericError()
            return
        }

        generator.generateSequence(Difficulty.fromLabel(difficulty)).onSuccess { generatorResult ->
            val sequenceIdentifier = generatorResult.retrieveIdentifier()
            if (sequenceViewModel.getSequenceByIdentifier(sequenceIdentifier) != null) {
                tryPopulateRound(retryCount + 1, maxRetries)
            } else {
                currentSequence = toSequence(generatorResult, difficulty)
                findViewById<TextView>(R.id.txtView_sequence).text = currentSequence.question
                findViewById<EditText>(R.id.edittxt_solution).text.clear()
                findViewById<TextView>(R.id.txtView_solution_path).text = ""
                surrendered = false
                sequenceViewModel.insertSequence(currentSequence)
                populateRound()
            }
        }.onFailure {
            printGenericError()
        }
    }

    private fun printGenericError() {
        Snackbar.make(
            findViewById<TextView>(R.id.txtView_sequence),
            getString(R.string.generic_error_message),
            Snackbar.LENGTH_INDEFINITE
        ).setBackgroundTint(Color.RED).show()
    }
}