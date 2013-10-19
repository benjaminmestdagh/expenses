package benjaminmestdagh.expenses.statistics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Map;

import benjaminmestdagh.expenses.R;
import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;

/**
 * Created by benjamin on 12/08/13.
 */
public class StatisticsAmountPerPropertyFragment extends Fragment {

    private int type;
    private View rootView;
    private GraphicalView graphicalView;
    private Account account;
    private Map<String, Double> amounts;
    private DefaultRenderer renderer;
    private CategorySeries dataset;

    public StatisticsAmountPerPropertyFragment(Account account, int type) {
        this.account = account;
        this.type = type;
    }

    public StatisticsAmountPerPropertyFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            dataset = (CategorySeries) savedInstanceState.getSerializable("dataset");
            renderer = (DefaultRenderer) savedInstanceState.getSerializable("renderer");
            account = (Account) savedInstanceState.getSerializable("account");
        }

        renderer = new DefaultRenderer();
        setDataset();
        setChartSettings();

        rootView = inflater.inflate(R.layout.fragment_statistics_chart, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
        if (graphicalView == null) {
            graphicalView = ChartFactory.getPieChartView(this.getActivity(), dataset, renderer);

            layout.addView(graphicalView);
        } else {
            graphicalView.repaint();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", dataset);
        outState.putSerializable("renderer", renderer);
        outState.putSerializable("account", account);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            dataset = (CategorySeries) savedInstanceState.getSerializable("dataset");
            renderer = (DefaultRenderer) savedInstanceState.getSerializable("renderer");
            account = (Account) savedInstanceState.getSerializable("account");
        }
    }

    private void setDataset() {
        dataset = new CategorySeries(getTitle());
        amounts = DatabaseExpensesManager.getInstance(this.getActivity()).getAmountPer(account.getId(), type);

        for(Map.Entry<String, Double> entry : amounts.entrySet()) {
            double value = entry.getValue() * SharedPreferencesManager.getInstance(this.getActivity())
                    .getBaseCurrency().getValue();

            if(type == DatabaseExpensesManager.DAY_OF_WEEK) {
                dataset.add(getWeekdayName(entry.getKey()), value);
            } else {
                dataset.add(entry.getKey(), value);
            }
        }
    }

    private String getWeekdayName(String key) {
        switch(Integer.parseInt(key)) {
            case Calendar.MONDAY:
                return getResources().getString(R.string.monday);
            case Calendar.TUESDAY:
                return getResources().getString(R.string.tuesday);
            case Calendar.WEDNESDAY:
                return getResources().getString(R.string.wednesday);
            case Calendar.THURSDAY:
                return getResources().getString(R.string.thursday);
            case Calendar.FRIDAY:
                return getResources().getString(R.string.friday);
            case Calendar.SATURDAY:
                return getResources().getString(R.string.saturday);
            case Calendar.SUNDAY:
            default:
                return getResources().getString(R.string.sunday);
        }
    }

    private String getTitle() {
        switch(type) {
            case DatabaseExpensesManager.DAY_OF_WEEK:
                return getResources().getString(R.string.stat_chart_amount_per_weekday);
            case DatabaseExpensesManager.CURRENCY:
                return getResources().getString(R.string.stat_chart_amount_per_currency);
            case DatabaseExpensesManager.CATEGORY:
                return getResources().getString(R.string.stat_chart_amount_per_category);
            case DatabaseExpensesManager.PAYMENT_METHOD:
            default:
                return getResources().getString(R.string.stat_chart_amount_per_payment_method);
        }
    }

    private void setChartSettings() {
        for(int i = 0; i < amounts.keySet().size(); i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(StatisticsChartHelper.COLORS[(i % StatisticsChartHelper.COLORS.length)]);
            r.setChartValuesFormat(new DecimalFormat("#.##"));
            renderer.addSeriesRenderer(r);
        }

        renderer.setDisplayValues(true);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[] { 30, 40, 15, 0});
    }
}
