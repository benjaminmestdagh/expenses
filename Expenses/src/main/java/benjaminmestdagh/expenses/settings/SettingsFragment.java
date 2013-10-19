package benjaminmestdagh.expenses.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.List;

import benjaminmestdagh.expenses.R;
import benjaminmestdagh.expenses.data.DatabaseOtherManager;
import benjaminmestdagh.expenses.entities.Currency;

/**
 * Created by benjamin on 07/08/13.
 */
public class SettingsFragment extends PreferenceFragment {

    private Context context;

    public SettingsFragment(Context context) {
        super();
        this.context = context;
    }

    // Empty constructor needed when switching screen orientation
    public SettingsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        setBaseCurrencySelectionPreference();
    }

    private void setBaseCurrencySelectionPreference() {
        ListPreference lp = (ListPreference)findPreference(SettingsActivity.KEY_PREF_CURRENCY_BASE);
        lp.setEntries(getCurrencyText());
        lp.setEntryValues(getCurrencyCodes());
        if(SharedPreferencesManager.getInstance(context)
                .containsKey(SettingsActivity.KEY_PREF_CURRENCY_BASE)) {
            lp.setValue(SharedPreferencesManager.getInstance(context).getBaseCurrency().getCode());
            lp.setSummary(SharedPreferencesManager.getInstance(context).getBaseCurrency().getCode());
        } else {
            lp.setValue("EUR");
            lp.setSummary("EUR");
        }

        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString());
                return true;
            }
        });
    }

    private String[] getCurrencyText() {
        List<String> currencies = new ArrayList<String>();

        for(Currency c : DatabaseOtherManager.getInstance(this.context).getCurrencies()) {
            currencies.add(c.getCode() + " (" + c.getName() + ")");
        }

        return currencies.toArray(new String[currencies.size()]);
    }

    private String[] getCurrencyCodes() {
        List<String> currencies = new ArrayList<String>();

        for(Currency c : DatabaseOtherManager.getInstance(this.context).getCurrencies()) {
            currencies.add(c.getCode());
        }

        return currencies.toArray(new String[currencies.size()]);
    }
}
