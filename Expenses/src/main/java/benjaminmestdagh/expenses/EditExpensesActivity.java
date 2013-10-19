package benjaminmestdagh.expenses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Date;

import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.data.DatabaseOtherManager;
import benjaminmestdagh.expenses.data.DatabasePaymentMethodsManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.entities.Category;
import benjaminmestdagh.expenses.entities.Currency;
import benjaminmestdagh.expenses.entities.Expense;
import benjaminmestdagh.expenses.entities.PaymentMethod;

public class EditExpensesActivity extends Activity {

    private DatabaseOtherManager dbman = DatabaseOtherManager.getInstance(this);
    private Account account;
    private Expense expense = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expenses);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra(DisplayExpensesList.ACCOUNT);
        if(intent.getBooleanExtra(DisplayExpensesList.NEW_EXPENSE, true)) {
            ((TextView)findViewById(R.id.editexpense_title)).setText(
                    getResources().getString(R.string.new_expense));
        } else {
            ((TextView)findViewById(R.id.editexpense_title)).setText(
                    getResources().getString(R.string.edit_expense));
            expense = (Expense) intent.getSerializableExtra(DisplayExpensesList.EXPENSE);
            ((TextView) findViewById(R.id.editexpense_editname)).setText(expense.getName());
            ((TextView) findViewById(R.id.editexpense_editamount))
                    .setText(String.valueOf(expense.getAmount()));
        }

        setCurrencies();
        setCategories();
        setPaymentMethods();
    }

    public void onCancelExpense(View view) {
        finish();
    }

    public void onSaveExpense(View view) {
        String expenseName = ((EditText)
                findViewById(R.id.editexpense_editname)).getText().toString();

        Currency currency = (Currency)
                ((Spinner)findViewById(R.id.currencyselector)).getSelectedItem();

        double amount = 0;
        String amountText = ((EditText)
                findViewById(R.id.editexpense_editamount)).getText().toString();

        if(!amountText.equals("")) amount = Double.valueOf(amountText);

        Category category = (Category)
                ((Spinner)findViewById(R.id.categoryselector)).getSelectedItem();

        PaymentMethod paymentMethod = (PaymentMethod)
                ((Spinner)findViewById(R.id.paymentmethodselector)).getSelectedItem();

        if(expenseName.equals("") ||
                currency == null ||
                amount <= 0 ||
                category == null ||
                paymentMethod == null) {
            showErrorAlert();
        } else {
            DatabaseExpensesManager dbexman = DatabaseExpensesManager.getInstance(this);
            String message = "";

            if(expense == null) {
                dbexman.insertExpense(new Expense(expenseName, account.getId(), category, amount,
                                currency, new Date().getTime(), paymentMethod));
                message = getResources().getString(R.string.expense_create_message);
            } else {
                expense.setName(expenseName);
                expense.setCategory(category);
                expense.setAmount(amount);
                expense.setCurrency(currency);
                expense.setPaymentMethod(paymentMethod);

                dbexman.updateExpense(expense);
                message = getResources().getString(R.string.expense_update_message);
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void showErrorAlert() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error))
                .setMessage(getResources().getString(R.string.input_check))
                .setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_expenses, menu);
        return true;
    }

    public void setCurrencies() {
        Collection<Currency> currencies = dbman.getCurrencies();
        ArrayAdapter currencyAdapter = new ArrayAdapter<Currency>(this,
                android.R.layout.simple_spinner_dropdown_item, currencies.toArray(new Currency[currencies.size()]));
        ((Spinner) findViewById(R.id.currencyselector)).setAdapter(currencyAdapter);

        long currency_id;
        if(expense == null) {
            currency_id = account.getCurrency_id();
        } else {
            currency_id = expense.getCurrency_id();
        }

        for(int i = 0; i < currencyAdapter.getCount(); i++) {
            if(currency_id == ((Currency)currencyAdapter.getItem(i)).getId()) {
                ((Spinner) findViewById(R.id.currencyselector)).setSelection(i);
                break;
            }
        }
    }

    public void setCategories() {
        Collection<Category> categories = dbman.getCategories();
        ArrayAdapter categoryAdapter = new ArrayAdapter<Category>(this,
                android.R.layout.simple_spinner_dropdown_item, categories.toArray(new Category[categories.size()]));
        ((Spinner) findViewById(R.id.categoryselector)).setAdapter(categoryAdapter);

        if(expense != null) {
            for(int i = 0; i < categoryAdapter.getCount(); i++) {
                if(expense.getCategory_id() == ((Category)categoryAdapter.getItem(i)).getId()) {
                    ((Spinner) findViewById(R.id.categoryselector)).setSelection(i);
                    break;
                }
            }
        }
    }

    public void setPaymentMethods() {
        Collection<PaymentMethod> paymentMethods =
                DatabasePaymentMethodsManager.getInstance(this).getPaymentMethods();
        ArrayAdapter pmAdapter = new ArrayAdapter<PaymentMethod>(this,
                android.R.layout.simple_spinner_dropdown_item, paymentMethods.toArray(
                new PaymentMethod[paymentMethods.size()]));
        ((Spinner) findViewById(R.id.paymentmethodselector)).setAdapter(pmAdapter);

        if(expense != null) {
            for(int i = 0; i < pmAdapter.getCount(); i++) {
                if(expense.getPaymentMethod_id() == ((PaymentMethod)pmAdapter.getItem(i)).getId()) {
                    ((Spinner) findViewById(R.id.paymentmethodselector)).setSelection(i);
                    break;
                }
            }
        }
    }

}
