package benjaminmestdagh.expenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import benjaminmestdagh.expenses.data.DatabaseOtherManager;
import benjaminmestdagh.expenses.entities.Currency;

/**
 * Created by benjamin on 08/08/13.
 */
public class CurrencyAdapter extends BaseAdapter {

    private ArrayList<Currency> currencyArrayList = new ArrayList<Currency>();
    private Context context;

    public CurrencyAdapter(Context context) {
        super();

        this.context = context;
        DatabaseOtherManager dbman = DatabaseOtherManager.getInstance(context);

        for(Currency c : dbman.getCurrencies()) {
            currencyArrayList.add(c);
        }
    }

    @Override
    public int getCount() {
        return currencyArrayList.size();
    }

    @Override
    public Currency getItem(int i) {
        return currencyArrayList.get(i);
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
                    android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
        }

        Currency currency = currencyArrayList.get(i);

        ((TextView)view).setText(currency.getCode() + " (" + currency.getName() + ")");

        return view;
    }
}
