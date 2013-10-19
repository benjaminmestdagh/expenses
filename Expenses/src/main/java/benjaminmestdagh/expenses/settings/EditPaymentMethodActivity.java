package benjaminmestdagh.expenses.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import benjaminmestdagh.expenses.MainActivity;
import benjaminmestdagh.expenses.R;
import benjaminmestdagh.expenses.data.DatabasePaymentMethodsManager;
import benjaminmestdagh.expenses.entities.PaymentMethod;
import benjaminmestdagh.expenses.entities.PaymentMethodType;

public class EditPaymentMethodActivity extends Activity {

    private PaymentMethod paymentMethod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_payment_method);

        Intent intent = getIntent();
        if(intent.getBooleanExtra(PaymentMethodsListActivity.NEW_METHOD, true)) {
            ((TextView)findViewById(R.id.editpayment_method_title)).setText(
                    getResources().getString(R.string.new_payment_method));
        } else {
            ((TextView)findViewById(R.id.editpayment_method_title)).setText(
                    getResources().getString(R.string.edit_payment_method));
            paymentMethod = (PaymentMethod)
                    intent.getSerializableExtra(PaymentMethodsListActivity.PAYMENT_METHOD);
            ((TextView) findViewById(R.id.editpayment_method_editname)).setText(paymentMethod.getName());
        }

        setPaymentMethodTypes();
    }

    private void setPaymentMethodTypes() {
        List<PaymentMethodType> types = DatabasePaymentMethodsManager.getInstance(this)
                .getPaymentMethodTypes();
        ArrayAdapter adapter = new ArrayAdapter<PaymentMethodType>(this,
                android.R.layout.simple_spinner_dropdown_item, types.toArray(new PaymentMethodType[types.size()]));
        ((Spinner) findViewById(R.id.editpayment_method_type_selection)).setAdapter(adapter);

        if(paymentMethod != null) {
            for(int i = 0; i < adapter.getCount(); i++) {
                if(paymentMethod.getType_id() == ((PaymentMethodType)adapter.getItem(i)).getId()) {
                    ((Spinner) findViewById(R.id.editpayment_method_type_selection)).setSelection(i);
                    break;
                }
            }
        }
    }

    public void onCancelPaymentMethod(View view) {
        finish();
    }

    public void onSavePaymentMethod(View view) {
        String methodName =
                ((TextView) findViewById(R.id.editpayment_method_editname)).getText().toString();

        PaymentMethodType type = (PaymentMethodType)
                ((Spinner) findViewById(R.id.editpayment_method_type_selection)).getSelectedItem();

        if(methodName.equals("") || type == null) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.error))
                    .setMessage(getResources().getString(R.string.input_check))
                    .setPositiveButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                    .show();
        } else {
            DatabasePaymentMethodsManager dbman = DatabasePaymentMethodsManager.getInstance(this);
            String message = "";

            if(paymentMethod == null) {
                dbman.insertPaymentMethod(new PaymentMethod(methodName, type));
                message = getResources().getString(R.string.payment_method_create_message);
            } else {
                paymentMethod.setName(methodName);
                paymentMethod.setType(type);
                dbman.updatePaymentMethod(paymentMethod);
                message = getResources().getString(R.string.payment_method_update_message);
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_payment_method, menu);
        return true;
    }

}
