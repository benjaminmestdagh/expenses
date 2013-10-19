package benjaminmestdagh.expenses.settings;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import benjaminmestdagh.expenses.R;

public class SettingsActivity extends Activity {

    public static final String KEY_PREF_CURRENCY_BASE = "pref_currency_base";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment(this))
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    
}
