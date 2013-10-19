package benjaminmestdagh.expenses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import benjaminmestdagh.expenses.data.DatabaseAccountsManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.entities.Currency;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;

public class EditAccountsActivity extends Activity {

    private Account account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editaccounts_activity);

        Intent intent = getIntent();
        if(intent.getBooleanExtra(MainActivity.NEW_ACCOUNT, true)) {
            ((TextView)findViewById(R.id.editaccounts_title)).setText(
                    getResources().getString(R.string.new_account));
        } else {
            ((TextView)findViewById(R.id.editaccounts_title)).setText(
                    getResources().getString(R.string.edit_account));
            account = (Account) intent.getSerializableExtra(MainActivity.ACCOUNT);
            ((TextView) findViewById(R.id.editaccount_editname)).setText(account.getName());
        }

        setCurrencies();
    }

    public void onCancelAccount(View view) {
        finish();
    }

    public void onSaveAccount(View view) {
        String accountname =
                ((TextView) findViewById(R.id.editaccount_editname)).getText().toString();

        Currency currency = (Currency)
                ((Spinner) findViewById(R.id.editaccount_currencyselector)).getSelectedItem();

        if(accountname.equals("") || currency == null) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.error))
                    .setMessage(getResources().getString(R.string.input_check))
                    .setPositiveButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                    .show();
        } else {
            DatabaseAccountsManager dbacman = DatabaseAccountsManager.getInstance(this);
            String message = "";

            if(account == null) {
                dbacman.insertAccount(new Account(accountname, currency));
                message = getResources().getString(R.string.account_create_message);
            } else {
                account.setName(accountname);
                account.setCurrency(currency);
                dbacman.updateAccount(account);
                message = getResources().getString(R.string.account_update_message);
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_accounts, menu);
        return true;
    }

    public void setCurrencies() {
        CurrencyAdapter adapter = new CurrencyAdapter(this);
        ((Spinner) findViewById(R.id.editaccount_currencyselector)).setAdapter(adapter);
        long currency_id;

        if(account == null) {
            currency_id = SharedPreferencesManager.getInstance(this).getBaseCurrency().getId();
        } else {
            currency_id = account.getCurrency_id();
        }

        for(int i = 0; i < adapter.getCount(); i++) {
            if(currency_id == adapter.getItem(i).getId()) {
                ((Spinner) findViewById(R.id.editaccount_currencyselector)).setSelection(i);
                break;
            }
        }
    }

}
