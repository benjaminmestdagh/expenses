package benjaminmestdagh.expenses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;

import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.entities.Currency;
import benjaminmestdagh.expenses.entities.Expense;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;
import benjaminmestdagh.expenses.statistics.StatisticsActivity;

public class DisplayExpensesList extends Activity {

    public static final String NEW_EXPENSE = "benjaminmestdagh.expenses.newExpense";
    public static final String ACCOUNT = "benjaminmestdagh.expenses.account";
    public static final String EXPENSE = "benjaminmestdagh.expenses.expense";
    private Currency baseCurrency;
    private Account account;
    Expense selectedExpense;
    Handler handler;
    ProgressDialog dialog;
    ExpensesAdapter adapter;
    ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayexpenses);
        // Show the Up button in the action bar.
        setupActionBar();

        Intent intent = getIntent();
        account = (Account)intent.getSerializableExtra(MainActivity.ACCOUNT);
        baseCurrency = SharedPreferencesManager.getInstance(this).getBaseCurrency();

        dialog = ProgressDialog.show(this,
                getResources().getString(R.string.loading),
                getResources().getString(R.string.loading_expenses));

        handler = new Handler();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            setExpensesOverview(true, query);
        } else {
            setExpensesOverview();
        }
    }

    public void setExpensesOverview(final boolean isSearching, final String query) {
        Thread t = new DatabaseThread(this, (ListView) findViewById(R.id.expenses_list)) {
            public void run() {
                adapter = new ExpensesAdapter(context, account.getId());

                if(isSearching) adapter.search(query);

                handler.post(new DatabaseThread(this.context, listView) {
                    public void run() {
                        listView.setAdapter(adapter);

                        setTitle(isSearching);
                        setSubtitle(isSearching);

                        // Create a message handling object as an anonymous class.
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView parent, View v, int position, long id) {
                                selectedExpense = (Expense) listView.getItemAtPosition(position);
                                showAlert(selectedExpense);
                            }
                        });

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            public boolean onItemLongClick(AdapterView parent, View view,
                                                           int position, long id) {
                                if (actionMode != null) {
                                    return false;
                                }

                                selectedExpense = (Expense) listView.getItemAtPosition(position);
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

    public void setExpensesOverview() {
        setExpensesOverview(false, null);
    }

    private void setTitle(boolean isSearching) {
        TextView textView = (TextView)findViewById(R.id.expenses_title);
        if(isSearching) {
            textView.setText(account.getName() + " (filtered)");
        } else {
            textView.setText(account.getName());
        }
    }

    private void setSubtitle(boolean isSearching) {
        TextView textView = (TextView)findViewById(R.id.expenses_subtitle);

        if(isSearching) {
            textView.setText(((ListView)findViewById(R.id.expenses_list)).getAdapter().getCount()
                    + " results found.");
        } else {
            int numberOfExpenses = DatabaseExpensesManager.getInstance(this)
                    .getNumberOfExpenses(account.getId());
            double totalAmount = DatabaseExpensesManager.getInstance(this)
                    .getTotalAccountValue(account.getId());

            StringBuilder sb = new StringBuilder();
            sb.append(numberOfExpenses);
            sb.append(" ");
            sb.append(getResources().getString(R.string.expenses_small));
            sb.append(", ");
            sb.append(account.getCurrencyCode());
            sb.append(" ");
            sb.append(new DecimalFormat("#.##").format(totalAmount * account.getCurrencyValue()));

            if(account.getCurrency_id() != baseCurrency.getId()) {
                sb.append(" (");
                sb.append(baseCurrency.getCode());
                sb.append(" ");
                sb.append(new DecimalFormat("#.##").format(totalAmount * baseCurrency.getValue()));
                sb.append(")");
            }

            textView.setText(sb.toString());
        }
    }

    private void showAlert(Expense expense) {
        StringBuilder sb = new StringBuilder();
        sb.append(expense.getCurrencyCode());
        sb.append(" ");
        sb.append(new DecimalFormat("#.##").format(expense.getAmount()));

        if(expense.getCurrency_id() != baseCurrency.getId()) {
            sb.append("\n");
            sb.append(baseCurrency.getCode());
            sb.append(" ");
            sb.append(new DecimalFormat("#.##").format(expense.getAmountIn(baseCurrency)));
        }

        sb.append("\n");
        sb.append(getResources().getString(R.string.editexpense_payment_method));
        sb.append(": ");
        sb.append(expense.getPaymentMethodName());

        sb.append("\n");
        sb.append(getResources().getString(R.string.editexpense_category));
        sb.append(": ");
        sb.append(expense.getCategoryName());

        sb.append("\n");
        sb.append(getResources().getString(R.string.date));
        sb.append(": ");
        sb.append(DateFormat.getDateInstance().format(expense.getDate()));

        new AlertDialog.Builder(this)
                .setTitle(expense.getName())
                .setMessage(sb.toString())
                .setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }

    private void startEditExpenseIntent() {
        Intent intent = new Intent(this, EditExpensesActivity.class);
        intent.putExtra(NEW_EXPENSE, false);
        intent.putExtra(EXPENSE, selectedExpense);
        intent.putExtra(ACCOUNT, account);
        startActivity(intent);
    }

    private void deleteExpenseActions() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.delete_expense))
                .setMessage(getResources().getString(R.string.delete_expense_confirm))
                .setPositiveButton(getResources().getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteExpense();
                            }
                })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }

    private void deleteExpense() {
        DatabaseExpensesManager.getInstance(this).deleteExpense(selectedExpense);
        selectedExpense = null;

        Toast.makeText(this,
                getResources().getString(R.string.expense_delete_message),
                Toast.LENGTH_LONG).show();

        handler = new Handler();
        setExpensesOverview();
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler = new Handler();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            setExpensesOverview(true, query);
        } else {
            setExpensesOverview();
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_expenses_list, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_expense_menu_item).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public void startActivity(Intent intent) {
        // check if search intent, and add current account to it
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(MainActivity.ACCOUNT, account);
        }

        super.startActivity(intent);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_expense_menu_item:
                Intent intent = new Intent(this, EditExpensesActivity.class);
                intent.putExtra(NEW_EXPENSE, true);
                intent.putExtra(ACCOUNT, account);
                startActivity(intent);

                return true;
            case R.id.stat_expense_menu_item:
                intent = new Intent(this, StatisticsActivity.class);
                intent.putExtra(ACCOUNT, account);
                startActivity(intent);

                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_expenses, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_expense_menu_item:
                    startEditExpenseIntent();
                    mode.finish();
                    return true;
                case R.id.delete_expense_menu_item:
                    deleteExpenseActions();
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
