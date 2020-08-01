package dev.blackcat.minauta.ui.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.blackcat.minauta.util.PreferencesStore
import dev.blackcat.minauta.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val passwordPreference: EditTextPreference? = findPreference(PreferencesStore.PASSWORD)
        passwordPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordPreference?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
            if (preference.text != null) "*".repeat(preference.text.length) else "*"
        }
    }

}