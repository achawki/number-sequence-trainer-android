package com.achawki.sequencetrainer.dialog

import android.content.Context
import androidx.core.text.HtmlCompat
import com.achawki.sequencetrainer.R
import com.achawki.sequencetrainer.data.Statistic
import com.achawki.sequencetrainer.viewmodel.SequenceTrainerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import android.view.View

fun renderStatisticsDialog(context: Context, statistics: List<Statistic>, viewModel: SequenceTrainerViewModel? = null, anchorView: View? = null) {
    val sb = StringBuilder()

    statistics.forEach { statistic ->
        sb.append("<b>")
        val difficultyText = when(statistic.difficulty) {
            1 -> context.getString(R.string.difficulty_1)
            2 -> context.getString(R.string.difficulty_2)
            3 -> context.getString(R.string.difficulty_3)
            else -> "${context.getString(R.string.difficulty)} ${statistic.difficulty}"
        }
        sb.append(difficultyText)
        sb.append("</b><br>")
        sb.append(
            "\t" + context.getString(R.string.success_rate) + ": ${statistic.successRate}%<br>" +
                    "\t" + context.getString(R.string.number_of_sequences) + ": ${statistic.numberOfSequences}<br>" +
                    "\t" + context.getString(R.string.failed_attempts) + ": ${statistic.failedAttemptsPerSequence}"
        )
        sb.append("<br><br>")
    }

    val message = if (sb.toString().isEmpty()) context.getString(R.string.no_data_available) else HtmlCompat.fromHtml(
        sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
    )


    val dialogBuilder = MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.statistics))
        .setMessage(message)
        .setNeutralButton(context.getString(R.string.close)) { _, _ -> }
    
    // Add reset button if viewModel is provided and there are statistics to reset
    if (viewModel != null && statistics.isNotEmpty()) {
        dialogBuilder.setNegativeButton(context.getString(R.string.reset_statistics)) { _, _ ->
            // Show confirmation dialog
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.reset_statistics))
                .setMessage(context.getString(R.string.reset_statistics_confirmation))
                .setPositiveButton(context.getString(R.string.reset)) { _, _ ->
                    viewModel.resetStatistics()
                    anchorView?.let { view ->
                        Snackbar.make(
                            view,
                            context.getString(R.string.statistics) + " " + context.getString(R.string.reset).lowercase(),
                            Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(Color.GREEN).show()
                    }
                }
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show()
        }
    }
    
    dialogBuilder.show()
}
