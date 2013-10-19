package benjaminmestdagh.expenses.statistics;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import benjaminmestdagh.expenses.DisplayExpensesList;
import benjaminmestdagh.expenses.R;
import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.entities.Account;
import benjaminmestdagh.expenses.entities.Currency;
import benjaminmestdagh.expenses.entities.Expense;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;

public class StatisticsActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    StatisticsPagerAdapter sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager viewPager;

    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra(DisplayExpensesList.ACCOUNT);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        sectionsPagerAdapter = new StatisticsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistics, menu);
        return true;
    }

    private class StatisticsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public StatisticsPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<Fragment>();
            fragments.add(new StatisticsOverviewFragment(account));
            fragments.add(new StatisticsAmountPerDateFragment(account));
            fragments.add(new StatisticsAmountPerPropertyFragment(account,
                    DatabaseExpensesManager.DAY_OF_WEEK));
            fragments.add(new StatisticsAmountPerPropertyFragment(account,
                    DatabaseExpensesManager.CURRENCY));
            fragments.add(new StatisticsAmountPerPropertyFragment(account,
                    DatabaseExpensesManager.CATEGORY));
            fragments.add(new StatisticsAmountPerPropertyFragment(account,
                    DatabaseExpensesManager.PAYMENT_METHOD));
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
        /*Fragment fragment = fragments.get(position);
        Bundle args = new Bundle();
        args.putInt(StatisticsOverviewFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);*/
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getResources().getString(
                            (R.string.title_fragment_overview_statistics)).toUpperCase(l);
                case 1:
                    return getResources().getString(
                            (R.string.stat_chart_amount_per_date)).toUpperCase(l);
                case 2:
                    return getResources().getString(
                            (R.string.stat_chart_amount_per_weekday)).toUpperCase(l);
                case 3:
                    return getResources().getString(
                            (R.string.stat_chart_amount_per_currency)).toUpperCase(l);
                case 4:
                    return getResources().getString(
                            (R.string.stat_chart_amount_per_category)).toUpperCase(l);
                case 5:
                    return getResources().getString(
                            (R.string.stat_chart_amount_per_payment_method)).toUpperCase(l);
            }
            return null;
        }
    }
}
