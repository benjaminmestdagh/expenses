package benjaminmestdagh.expenses.statistics;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;

import benjaminmestdagh.expenses.R;
import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.entities.Currency;
import benjaminmestdagh.expenses.entities.Expense;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;

/**
 * Created by benjamin on 12/08/13.
 */
public class StatisticsOverviewFragment extends Fragment {
    private View rootView;
    private Account account;
    Handler handler;
    Dialog dialog;

    public StatisticsOverviewFragment(Account account) {
        this.account = account;
    }

    public StatisticsOverviewFragment () {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        rootView = inflater.inflate(R.layout.fragment_statistics_overview, container, false);

        dialog = ProgressDialog.show(this.getActivity(),
                getResources().getString(R.string.loading),
                getResources().getString(R.string.loading_stats));

        handler = new Handler();

        setViews();

        return rootView;
    }

    private void setViews() {
        new Thread() {
            @Override
            public void run() {
                final String subtitleString = getSubtitle();
                final String maxAmountString = getAmountString(DatabaseExpensesManager.MAXIMUM);
                final String minAmountString = getAmountString(DatabaseExpensesManager.MINIMUM);
                final String avgAmountString = getAmountString(DatabaseExpensesManager.AVERAGE);
                final String maxCurrencyString = getPropertyString(DatabaseExpensesManager.MAXIMUM,
                        DatabaseExpensesManager.CURRENCY);
                final String minCurrencyString = getPropertyString(DatabaseExpensesManager.MINIMUM,
                        DatabaseExpensesManager.CURRENCY);
                final String maxCategoryString = getPropertyString(DatabaseExpensesManager.MAXIMUM,
                        DatabaseExpensesManager.CATEGORY);
                final String minCategoryString = getPropertyString(DatabaseExpensesManager.MINIMUM,
                        DatabaseExpensesManager.CATEGORY);
                final String maxPaymentMethodString = getPropertyString(DatabaseExpensesManager.MAXIMUM,
                        DatabaseExpensesManager.PAYMENT_METHOD);
                final String minPaymentMethodString = getPropertyString(DatabaseExpensesManager.MINIMUM,
                        DatabaseExpensesManager.PAYMENT_METHOD);
                final String dateRange = getDateRange();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView) rootView.findViewById(R.id.statistics_overview_title);
                        textView.setText(account.getName());

                        textView = (TextView) rootView.findViewById(R.id.statistics_overview_subtitle);
                        textView.setText(subtitleString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_max_amount);
                        textView.setText(textView.getText() + " " + maxAmountString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_min_amount);
                        textView.setText(textView.getText() + " " + minAmountString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_avg_amount);
                        textView.setText(textView.getText() + " " + avgAmountString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_max_category);
                        textView.setText(textView.getText() + " " + maxCategoryString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_min_category);
                        textView.setText(textView.getText() + " " + minCategoryString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_max_currency);
                        textView.setText(textView.getText() + " " + maxCurrencyString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_min_currency);
                        textView.setText(textView.getText() + " " + minCurrencyString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_max_payment_method);
                        textView.setText(textView.getText() + " " + maxPaymentMethodString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_min_payment_method);
                        textView.setText(textView.getText() + " " + minPaymentMethodString);

                        textView = (TextView) rootView.findViewById(R.id.stat_overview_date_range);
                        textView.setText(textView.getText() + " " + dateRange);

                        dialog.dismiss();
                    }
                });
            }
        }.start();
    }

    private String getSubtitle() {
        Currency baseCurrency = SharedPreferencesManager.getInstance(this.getActivity()).getBaseCurrency();

        int numberOfExpenses = DatabaseExpensesManager.getInstance(this.getActivity())
                .getNumberOfExpenses(account.getId());
        double totalAmount = DatabaseExpensesManager.getInstance(this.getActivity())
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

        return sb.toString();
    }

    private String getAmountString(int type) {
        switch (type) {
            case DatabaseExpensesManager.MAXIMUM:
                Expense result = DatabaseExpensesManager.getInstance(this.getActivity()).getAmount(
                        account.getId(), DatabaseExpensesManager.MAXIMUM);
                return getFormattedAmount(result);
            case DatabaseExpensesManager.MINIMUM:
                result = DatabaseExpensesManager.getInstance(this.getActivity()).getAmount(
                        account.getId(), DatabaseExpensesManager.MINIMUM);
                return getFormattedAmount(result);
            case DatabaseExpensesManager.AVERAGE:
            default:
                double amount = DatabaseExpensesManager.getInstance(this.getActivity())
                        .getAverageAmountInUsd(account.getId());
                amount = amount * SharedPreferencesManager.getInstance(this.getActivity())
                        .getBaseCurrency().getValue();
                return SharedPreferencesManager.getInstance(this.getActivity()).getBaseCurrency().getCode() +
                        " " + new DecimalFormat("#.##").format(amount);
        }
    }

    private String getPropertyString(int type, int property) {
        Object object = DatabaseExpensesManager.getInstance(this.getActivity()).getMostOrLeastUsedProperty(
                account.getId(), type, property);

        return object.toString();
    }

    private String getDateRange() {
        long[] dates = DatabaseExpensesManager.getInstance(this.getActivity()).getDateRange(account.getId());

        String beginDate = DateFormat.getDateInstance().format(dates[1]);
        String endDate = DateFormat.getDateInstance().format(dates[0]);

        return beginDate + " - " + endDate;
    }

    private String getFormattedAmount(Expense expense) {
        Currency baseCurrency = SharedPreferencesManager.getInstance(this.getActivity()).getBaseCurrency();
        StringBuilder sb = new StringBuilder();
        sb.append(expense.getCurrencyCode());
        sb.append(" ");
        sb.append(new DecimalFormat("#.##").format(expense.getAmount()));

        if(expense.getCurrency_id() != baseCurrency.getId()) {
            sb.append(" (");
            sb.append(baseCurrency.getCode());
            sb.append(" ");
            sb.append(new DecimalFormat("#.##").format(expense.getAmountIn(baseCurrency)));
            sb.append(")");
        }

        return sb.toString();
    }
}
