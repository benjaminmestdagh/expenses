package benjaminmestdagh.expenses.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import benjaminmestdagh.expenses.DatabaseThread;
import benjaminmestdagh.expenses.PaymentMethodsAdapter;
import benjaminmestdagh.expenses.R;
import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.data.DatabasePaymentMethodsManager;
import benjaminmestdagh.expenses.entities.PaymentMethod;

public class PaymentMethodsListActivity extends Activity {

    public static final String NEW_METHOD = "benjaminmestdagh.expenses.newmethod";
    public static final String PAYMENT_METHOD = "benjaminmestdagh.expenses.paymentmethod";
    private PaymentMethod selectedPaymentMethod;
    BaseAdapter adapter;
    Handler handler;
    Dialog dialog;
    ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods_list);
        // Show the Up button in the action bar.
        setupActionBar();

        dialog = ProgressDialog.show(this,
                getResources().getString(R.string.loading),
                getResources().getString(R.string.loading_payment_methods));

        handler = new Handler();
        setPaymentMethodsOverview();
    }

    private void setPaymentMethodsOverview() {
        Thread t = new DatabaseThread(this, (ListView) findViewById(R.id.payment_method_list)) {
            public void run() {
                adapter = new PaymentMethodsAdapter(context);

                handler.post(new DatabaseThread(this.context, listView) {
                    public void run() {
                        listView.setAdapter(adapter);

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            public boolean onItemLongClick(AdapterView parent, View view,
                                                           int position, long id) {
                                if (actionMode != null) {
                                    return false;
                                }

                                selectedPaymentMethod = (PaymentMethod) listView.getItemAtPosition(position);
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

    private void startEditIntent() {
        Intent intent = new Intent(this, EditPaymentMethodActivity.class);
        intent.putExtra(NEW_METHOD, false);
        intent.putExtra(PAYMENT_METHOD, selectedPaymentMethod);
        startActivity(intent);
    }

    private void deletePaymentMethodActions() {
        if(DatabaseExpensesManager.getInstance(this)
                .getExpensesByPaymentMethod(selectedPaymentMethod).size() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.error))
                    .setMessage(getResources().getString(R.string.delete_payment_method_error))
                    .setPositiveButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.delete_payment_method))
                    .setMessage(getResources().getString(R.string.delete_payment_method_confirm))
                    .setPositiveButton(getResources().getString(R.string.delete),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePaymentMethod();
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                    .show();
        }
    }

    private void deletePaymentMethod() {
        DatabasePaymentMethodsManager.getInstance(this).deletePaymentMethod(selectedPaymentMethod);
        selectedPaymentMethod = null;

        Toast.makeText(this,
                getResources().getString(R.string.payment_method_delete_message),
                Toast.LENGTH_LONG)
                .show();

        handler = new Handler();
        setPaymentMethodsOverview();
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payment_methods_list, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.new_payment_method_menu_item) {
            Intent intent = new Intent(this, EditPaymentMethodActivity.class);
            intent.putExtra(NEW_METHOD, true);
            intent.putExtra(PAYMENT_METHOD, selectedPaymentMethod);
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

    @Override
    protected void onResume() {
        super.onResume();

        handler = new Handler();
        setPaymentMethodsOverview();
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_payment_method, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_payment_method_menu_item:
                    startEditIntent();
                    mode.finish();
                    return true;
                case R.id.delete_payment_method_menu_item:
                    deletePaymentMethodActions();
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
