package benjaminmestdagh.expenses.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import benjaminmestdagh.expenses.data.DatabaseOtherManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.entities.Currency;

/**
 * Created by benjamin on 07/08/13.
 */
public class SharedPreferencesManager {

    private static SharedPreferencesManager instance;
    private SharedPreferences sharedPreferences;
    private Context context;

    private SharedPreferencesManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static SharedPreferencesManager getInstance(Context context) {
        if(instance == null) {
            instance = new SharedPreferencesManager(context);
        }

        return instance;
    }

    public boolean containsKey(String key) {
        return sharedPreferences.contains(key);
    }

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean("firstRun", true);
    }

    public void setRunned() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("firstRun", false);
        edit.commit();
    }

    public Currency getBaseCurrency() {
        //System.out.println(sharedPreferences.contains(SettingsActivity.KEY_PREF_CURRENCY_BASE));
        String code = sharedPreferences.getString(SettingsActivity.KEY_PREF_CURRENCY_BASE, "EUR");
        return DatabaseOtherManager.getInstance(context).getCurrencyByCode(code);
    }
}
