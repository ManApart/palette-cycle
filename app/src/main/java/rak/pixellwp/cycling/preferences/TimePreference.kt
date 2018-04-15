package rak.pixellwp.cycling.preferences

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import rak.pixellwp.R
import rak.pixellwp.cycling.models.DaySeconds


class TimePreference @JvmOverloads constructor(ctxt: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.dialogPreferenceStyle) : DialogPreference(ctxt, attrs, defStyle) {
    private val calendar = DaySeconds()
    private var picker: TimePicker = TimePicker(ctxt)

    init {

        setPositiveButtonText(R.string.set)
        setNegativeButtonText(R.string.cancel)
    }

    override fun onCreateDialogView(): View {
        picker = TimePicker(context)
        return picker
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker.currentHour = calendar.getHours()
        picker.currentMinute = calendar.getMinutes()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            calendar.setTime(picker as TimePicker)

            setSummary(summary)
            if (callChangeListener(calendar.getMilliseconds())) {
                persistLong(calendar.getMilliseconds())
                notifyChanged()
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {

        if (restoreValue) {
            if (defaultValue == null) {
                calendar.setTime(getPersistedLong(System.currentTimeMillis()))
            } else {
                calendar.setTime(java.lang.Long.parseLong(getPersistedString(defaultValue as String?)))
            }
        } else {
            if (defaultValue == null) {
                calendar.setTime(System.currentTimeMillis())
            } else {
                calendar.setTime(java.lang.Long.parseLong(defaultValue as String?))
            }
        }
        setSummary(summary)
    }

    override fun getSummary(): CharSequence {
        return calendar.get12HourFormattedString()
    }
}