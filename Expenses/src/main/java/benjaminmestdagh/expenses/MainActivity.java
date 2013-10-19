package benjaminmestdagh.expenses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import benjaminmestdagh.expenses.data.OpenExchangeRatesClient;
import benjaminmestdagh.expenses.data.DatabaseAccountsManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.statistics.StatisticsActivity;

public class MainActivity extends Activity {

    public static final String ACCOUNT = "benjaminmestdagh.expenses.account";
    public static final String NEW_ACCOUNT = "benjaminmestdagh.expenses.new_account";
    BaseAdapter adapter;
    Handler handler;
    ProgressDialog dialog;
    ActionMode actionMode;
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = ProgressDialog.show(this,
                getResources().getString(R.string.loading),
                getResources().getString(R.string.loading_accounts));

        handler = new Handler();
        setAccountsOverview();
    }

    private void setAccountsOverview() {

        Thread t = new DatabaseThread(this, (ListView) findViewById(R.id.accounts_list)) {
            public void run() {

                adapter = new AccountsAdapter(this.context);

                handler.post(new DatabaseThread(this.context, listView) {
                    public void run() {

                        listView.setAdapter(adapter);

                        // Create a message handling object as an anonymous class.
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView parent, View v, int position, long id) {
                                account = (Account) listView.getItemAtPosition(position);
                                openExpensesView();
                            }
                        });

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            public boolean onItemLongClick(AdapterView parent, View view,
                                                           int position, long id) {
                                if (actionMode != null) {
                                    return false;
                                }

                                account = (Account) listView.getItemAtPosition(position);
                                actionMode = startActionMode(actionModeCallback);
                                view.setSelected(true);

                                return true;
                            }
                        });

                        dialog.dismiss();
                }});
            }
        };

        t.start();

    }

    private void openExpensesView() {
        Intent intent = new Intent(this, DisplayExpensesList.class);
        intent.putExtra(ACCOUNT, account);
        startActivity(intent);
    }

    private void startEditAccountIntent() {
        Intent intent = new Intent(this, EditAccountsActivity.class);
        intent.putExtra(NEW_ACCOUNT, false);
        intent.putExtra(ACCOUNT, account);
        startActivity(intent);
    }

    private void startStatisticsIntent() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra(ACCOUNT, account);
        startActivity(intent);
    }

    private void deleteAccountActions() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.delete_account))
                .setMessage(getResources().getString(R.string.delete_account_confirm))
                .setPositiveButton(getResources().getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAccount();
                            }
                })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }

    private void deleteAccount() {
        DatabaseAccountsManager.getInstance(this).deleteAccount(account);
        account = null;

        Toast.makeText(this,
                getResources().getString(R.string.account_delete_message),
                Toast.LENGTH_LONG)
                .show();

        handler = new Handler();
        setAccountsOverview();
    }

    private void updateCurrencies() {
        dialog = ProgressDialog.show(this,
                getResources().getString(R.string.updating),
                getResources().getString(R.string.currency_update));

        handler = new Handler();

        Thread t = new DatabaseThread(this) {
            public void run() {
                try {
                    OpenExchangeRatesClient.getInstance().updateExchangeRates(context);
                    handler.post(new Runnable() {
                        public void run() {
                            dialog.dismiss();

                            Toast.makeText(context,
                                    getResources().getString(R.string.currency_update_success),
                                    Toast.LENGTH_LONG)
                                    .show();

                            setAccountsOverview();
                        }});
                } catch (Exception ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            dialog.dismiss();

                            new AlertDialog.Builder(context)
                                    .setTitle(getResources().getString(R.string.error))
                                    .setMessage(getResources().getString(R.string.currency_update_error))
                                    .setPositiveButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                    .show();
                        }});
                }
            }
        };

        t.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler = new Handler();
        setAccountsOverview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.new_account_menu_item:
                Intent intent = new Intent(this, EditAccountsActivity.class);
                intent.putExtra(NEW_ACCOUNT, true);
                startActivity(intent);
                return true;
            case R.id.currencyUpdate_menu_item:
                updateCurrencies();
                return true;
            case R.id.settings_menu_item:
                intent = new Intent(this, benjaminmestdagh.expenses.settings.SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_accounts, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_account_menu_item:
                    startEditAccountIntent();
                    mode.finish();
                    return true;
                case R.id.delete_account_menu_item:
                    deleteAccountActions();
                    mode.finish();
                    return true;
                case R.id.stat_account_menu_item:
                    startStatisticsIntent();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };
}
