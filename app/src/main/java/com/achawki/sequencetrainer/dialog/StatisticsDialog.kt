package com.achawki.sequencetrainer.dialog

import android.content.Context
import androidx.core.text.HtmlCompat
import com.achawki.sequencetrainer.R
import com.achawki.sequencetrainer.data.Statistic
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun renderStatisticsDialog(context: Context, statistics: List<Statistic>) {
    val sb = StringBuilder()

    statistics.forEach { statistic ->
        sb.append("<b>")
        sb.append(
            context.getString(
                context.resources.getIdentifier(
                    "difficulty_${statistic.difficulty}",
                    "string",
                    context.packageName
                )
            )
        )
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


    MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.statistics)).setMessage(message)
        .setNeutralButton(context.getString(R.string.close)) { _, _ ->
        }.show()
}
