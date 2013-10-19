package benjaminmestdagh.expenses.statistics;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Map;

import benjaminmestdagh.expenses.R;
import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;

/**
 * Created by benjamin on 12/08/13.
 */
public class StatisticsAmountPerDateFragment extends Fragment {

    private View rootView;
    private GraphicalView graphicalView;
    private Account account;
    private Map<Calendar, Double> amountsPerDate;
    private double highestYValue;
    private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    private CategorySeries currentSeries;
    private XYSeriesRenderer currentRenderer;


    public StatisticsAmountPerDateFragment(Account account) {
        this.account = account;
    }

    public StatisticsAmountPerDateFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            dataset = (XYMultipleSeriesDataset) savedInstanceState.getSerializable("dataset");
            renderer = (XYMultipleSeriesRenderer) savedInstanceState.getSerializable("renderer");
            currentSeries = (CategorySeries) savedInstanceState.getSerializable("series");
            currentRenderer = (XYSeriesRenderer) savedInstanceState.getSerializable("currentrenderer");
            account = (Account) savedInstanceState.getSerializable("account");
        }
        rootView = inflater.inflate(R.layout.fragment_statistics_chart, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
        if (graphicalView == null) {
            setRenderer();
            setDataset();
            setChartSettings();

            graphicalView = ChartFactory.getBarChartView(this.getActivity(), dataset, renderer, BarChart.Type.DEFAULT);

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
        outState.putSerializable("series", currentSeries);
        outState.putSerializable("currentrenderer", currentRenderer);
        outState.putSerializable("account", account);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            dataset = (XYMultipleSeriesDataset) savedInstanceState.getSerializable("dataset");
            renderer = (XYMultipleSeriesRenderer) savedInstanceState.getSerializable("renderer");
            currentSeries = (CategorySeries) savedInstanceState.getSerializable("series");
            currentRenderer = (XYSeriesRenderer) savedInstanceState.getSerializable("currentrenderer");
            account = (Account) savedInstanceState.getSerializable("account");
        }
    }

    private void setChartSettings() {
        int i = 1;
        for(Map.Entry<Calendar, Double> entry : amountsPerDate.entrySet()) {
            renderer.addXTextLabel(i, DateFormat.getDateInstance().format(entry.getKey().getTime()));
            i++;
        }
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(renderer.getXAxisMin() + amountsPerDate.keySet().size());
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(highestYValue + 10);
        renderer.setBarSpacing(0.5);
        renderer.setXTitle(getResources().getString(R.string.date));
        renderer.setYTitle(getResources().getString(R.string.editexpense_amount) + " (" +
                SharedPreferencesManager.getInstance(this.getActivity()).getBaseCurrency().getCode() + ")");
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GRAY);
        renderer.setXLabels(0);
    }

    private void setDataset() {
        dataset = new XYMultipleSeriesDataset();
        highestYValue = 0;
        amountsPerDate = DatabaseExpensesManager.getInstance(this.getActivity())
                .getAmountPerDate(account.getId());
        currentSeries = new CategorySeries(getResources()
                .getString(R.string.stat_chart_amount_per_date));
        for(Map.Entry<Calendar, Double> entry : amountsPerDate.entrySet()) {
            currentSeries.add(entry.getValue() * SharedPreferencesManager.getInstance(this.getActivity())
                    .getBaseCurrency().getValue());
            if(entry.getValue() > highestYValue) highestYValue = entry.getValue();
        }

        dataset.addSeries(currentSeries.toXYSeries());
    }

    private void setRenderer() {
        renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[]{30, 40, 15, 0});
        currentRenderer = new XYSeriesRenderer();
        currentRenderer.setColor(Color.rgb(0, 153, 204));
        renderer.addSeriesRenderer(currentRenderer);
    }
}
