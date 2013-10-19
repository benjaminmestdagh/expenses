package benjaminmestdagh.expenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import benjaminmestdagh.expenses.data.DatabaseAccountsManager;
import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.entities.Currency;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;

/**
 * Created by benjamin on 05/08/13.
 */
public class AccountsAdapter extends BaseAdapter {

    private ArrayList<Account> accountArrayList = new ArrayList<Account>();
    private Context context;

    public AccountsAdapter(Context context) {
        super();

        this.context = context;
        DatabaseAccountsManager dbacman = DatabaseAccountsManager.getInstance(context);

        for(Account a : dbacman.getAccounts()) {
            accountArrayList.add(a);
        }
    }

    @Override
    public int getCount() {
        return accountArrayList.size();
    }

    @Override
    public Account getItem(int i) {
        return accountArrayList.get(i);
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
                    R.layout.custom_listview_item, viewGroup, false);

        }

        Account account = accountArrayList.get(i);

        TextView accountTextView = (TextView)
                view.findViewById(R.id.custom_view);

        accountTextView.setText(account.toString());

        TextView totalValueTextView = (TextView)
                view.findViewById(R.id.custom_view_total);

        double totalValue = DatabaseExpensesManager.getInstance(context)
                .getTotalAccountValue(account.getId());

        Currency baseCurrency = SharedPreferencesManager.getInstance(context).getBaseCurrency();
        totalValueTextView.setText(baseCurrency.getCode() + " " +
                new DecimalFormat("#.##").format(totalValue * baseCurrency.getValue()));

        return view;
    }
}
