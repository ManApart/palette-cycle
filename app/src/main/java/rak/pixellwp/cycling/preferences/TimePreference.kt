package rak.pixellwp.cycling.preferences

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import rak.pixellwp.R
import java.util.*


class TimePreference @JvmOverloads constructor(ctxt: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.dialogPreferenceStyle) : DialogPreference(ctxt, attrs, defStyle) {
    private val calendar: Calendar?
    private var picker: TimePicker? = null

    init {

        setPositiveButtonText(R.string.set)
        setNegativeButtonText(R.string.cancel)
        calendar = GregorianCalendar()
    }

    override fun onCreateDialogView(): View {
        picker = TimePicker(context)
        return picker as TimePicker
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker!!.currentHour = calendar!!.get(Calendar.HOUR_OF_DAY)
        picker!!.currentMinute = calendar!!.get(Calendar.MINUTE)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            calendar!!.set(Calendar.HOUR_OF_DAY, picker!!.currentHour)
            calendar!!.set(Calendar.MINUTE, picker!!.currentMinute)

            setSummary(summary)
            if (callChangeListener(calendar!!.timeInMillis)) {
                persistLong(calendar!!.timeInMillis)
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
                calendar!!.setTimeInMillis(getPersistedLong(System.currentTimeMillis()))
            } else {
                calendar!!.setTimeInMillis(java.lang.Long.parseLong(getPersistedString(defaultValue as String?)))
            }
        } else {
            if (defaultValue == null) {
                calendar!!.setTimeInMillis(System.currentTimeMillis())
            } else {
                calendar!!.setTimeInMillis(java.lang.Long.parseLong(defaultValue as String?))
            }
        }
        setSummary(summary)
    }

    override fun getSummary(): CharSequence? {
        return if (calendar == null) {
            null
        } else DateFormat.getTimeFormat(context).format(Date(calendar!!.timeInMillis))
    }
}