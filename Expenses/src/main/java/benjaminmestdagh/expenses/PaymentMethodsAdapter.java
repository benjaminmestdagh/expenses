package benjaminmestdagh.expenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import benjaminmestdagh.expenses.data.DatabasePaymentMethodsManager;
import benjaminmestdagh.expenses.entities.PaymentMethod;

/**
 * Created by benjamin on 10/08/13.
 */
public class PaymentMethodsAdapter extends BaseAdapter {

    private List<PaymentMethod> paymentMethodList;

    public PaymentMethodsAdapter(Context context) {
        super();

        this.paymentMethodList = DatabasePaymentMethodsManager.getInstance(context)
                .getPaymentMethods();
    }

    @Override
    public int getCount() {
        return paymentMethodList.size();
    }

    @Override
    public PaymentMethod getItem(int i) {
        return paymentMethodList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater =
                    LayoutInflater.from(viewGroup.getContext());

            view = inflater.inflate(
                    R.layout.expense_listview_item, viewGroup, false);

        }

        PaymentMethod paymentMethod = paymentMethodList.get(i);

        ImageView imageView = (ImageView)
                view.findViewById(R.id.expense_list_image);

        Context c = imageView.getContext();
        int id = c.getResources().getIdentifier("ic_" + paymentMethod.getTypeIcon(),
                "drawable", c.getPackageName());
        imageView.setImageResource(id);

        TextView paymentMethodTextView = (TextView)
                view.findViewById(R.id.expense_view);

        paymentMethodTextView.setText(paymentMethod.getName());

        TextView paymentMethodSubTextView = (TextView)
                view.findViewById(R.id.expense_view_total);

        paymentMethodSubTextView.setText(paymentMethod.getType());

        return view;
    }
}
