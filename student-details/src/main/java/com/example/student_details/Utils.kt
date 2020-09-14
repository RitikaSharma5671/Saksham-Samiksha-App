package com.example.student_details

import android.content.Context
import android.widget.EditText
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder


fun getViewModelProvider(
        fragment: Fragment,
        factory: ViewModelProvider.Factory
): ViewModelProvider {
    return ViewModelProvider(fragment, factory)
}


fun getViewModelProvider(
        activity: FragmentActivity,
        factory: ViewModelProvider.Factory
): ViewModelProvider {
    return ViewModelProvider(activity, factory)
}

fun getViewModelProvider(
        activity: FragmentActivity): ViewModelProvider {
    return ViewModelProvider(activity)
}

fun showNumberPickerDialog(
        context: Context,
        title: String,
        value: Double,
        range: ClosedRange<Double>,
        stepSize: Double,
        formatToString: (Double) -> String,
        valueChooseAction: (Double) -> Unit
) {
    val numberPicker = NumberPicker(context).apply {
        setFormatter { formatToString(it.toDouble() * stepSize) }
        wrapSelectorWheel = false

        minValue = (range.start / stepSize).toInt()
        maxValue = (range.endInclusive / stepSize).toInt()
        this.value = (value / stepSize).toInt()

        // NOTE: workaround for a bug that rendered the selected value wrong until user scrolled, see also: https://stackoverflow.com/q/27343772/3451975
        (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }.get(this) as EditText).filters = emptyArray()
    }

    MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(numberPicker)
            .setPositiveButton("OK") { _, _ -> valueChooseAction(numberPicker.value.toDouble() * stepSize) }
            .setNeutralButton("Cancel") { _, _ -> /* do nothing, closes dialog automatically */ }
            .show()
}


fun getViewModelProvider(fragment: Fragment): ViewModelProvider {
    return ViewModelProvider(fragment)
}

