package benjaminmestdagh.expenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

import benjaminmestdagh.expenses.data.DatabaseExpensesManager;
import benjaminmestdagh.expenses.entities.Expense;

/**
 * BaseAdapter to set the expenses ListView
 * Created by benjamin on 05/08/13.
 */
public class ExpensesAdapter extends BaseAdapter implements SectionIndexer {

    private List<Expense> expenseArrayList;
    private Context context;
    private List<Integer> positionForSection = new ArrayList<Integer>();
    private List<Integer> sectionForPosition = new ArrayList<Integer>();
    private String[] sections;

    public ExpensesAdapter(Context context, long account_id) {
        super();

        this.context = context;
        expenseArrayList = DatabaseExpensesManager.getInstance(context)
                .getExpensesByAccount(account_id);

        doSectioning();
    }

    private void doSectioning() {
        LinkedHashMap<String, Integer> dateIndexer = new LinkedHashMap<String, Integer>();
        String previousDate = "";
        String currentDate;

        for(int i = 0; i < expenseArrayList.size(); i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M");
            currentDate = sdf.format(expenseArrayList.get(i).getDate());

            if(!currentDate.equals(previousDate)) {
                dateIndexer.put(currentDate, i);
                positionForSection.add(i);
                previousDate = currentDate;
            }

            sectionForPosition.add(i, dateIndexer.get(currentDate));
        }

        sections = new String[dateIndexer.keySet().size()];
        dateIndexer.keySet().toArray(sections);
    }

    public void search(String query) {
        List<Expense> tempList = new ArrayList<Expense>();

        for(Expense expense : expenseArrayList) {
            if(expense.getName().toLowerCase().contains(query.toLowerCase())) {
                tempList.add(expense);
            }
        }

        expenseArrayList = tempList;
        doSectioning();
    }

    @Override
    public int getCount() {
        return expenseArrayList.size();
    }

    @Override
    public Expense getItem(int i) {
        return expenseArrayList.get(i);
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

        Expense ex = expenseArrayList.get(i);

        ImageView imageView = (ImageView)
                view.findViewById(R.id.expense_list_image);

        Context c = imageView.getContext();
        int id = c.getResources().getIdentifier("ic_" + ex.getPaymentMethodIcon(),
                "drawable", c.getPackageName());
        imageView.setImageResource(id);

        TextView accountTextView = (TextView)
                view.findViewById(R.id.expense_view);

        accountTextView.setText(ex.getName());

        TextView totalValueTextView = (TextView)
                view.findViewById(R.id.expense_view_total);

        totalValueTextView.setText(ex.getCurrencyCode() + " " +
                new DecimalFormat("#.##").format(ex.getAmount()));

        return view;
    }

    @Override
    public Object[] getSections() {
        return this.sections;
    }

    @Override
    public int getPositionForSection(int i) {
        return positionForSection.get(i);
    }

    @Override
    public int getSectionForPosition(int i) {
        return sectionForPosition.get(i);
    }
}
