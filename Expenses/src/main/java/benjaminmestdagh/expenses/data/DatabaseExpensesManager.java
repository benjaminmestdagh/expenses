package benjaminmestdagh.expenses.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import benjaminmestdagh.expenses.entities.Expense;
import benjaminmestdagh.expenses.entities.PaymentMethod;
import benjaminmestdagh.expenses.settings.SharedPreferencesManager;

/**
 * Created by benjamin on 30/07/13.
 */
public class DatabaseExpensesManager {

    public static final int MAXIMUM = 0;
    public static final int MINIMUM = 1;
    public static final int AVERAGE = 2;
    public static final int CURRENCY = 3;
    public static final int CATEGORY = 4;
    public static final int PAYMENT_METHOD = 5;
    public static final int DAY_OF_WEEK = 6;

    private static DatabaseExpensesManager dbmanager = null;
    private SQLiteOpenHelper databaseHelper;
    private Context context;

    private DatabaseExpensesManager(Context context) {
        databaseHelper = new ExpensesDbHelper(context);
        this.context = context;
    }

    public static DatabaseExpensesManager getInstance(Context context) {
        if (dbmanager == null) {
            dbmanager = new DatabaseExpensesManager(context);
        }

        return dbmanager;
    }

    public void insertExpense(Expense expense) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(db != null) {
            ContentValues values = new ContentValues();

            values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_NAME, expense.getName());
            values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE, expense.getDate());
            values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT, expense.getAccount_id());
            values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY, expense.getCategory_id());
            values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_AMOUNT, expense.getAmount());
            values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY, expense.getCurrency_id());
            values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD, expense.getPaymentMethod_id());

            long newRowId;
            newRowId = db.insert(ExpensesTableContract.ExpenseEntry.TABLE_NAME, null, values);

            expense.setId(newRowId);
        }
    }

    public List<Expense> getExpensesByAccount(long account_id) {
        List<Expense> result = new ArrayList<Expense>();

        String[] projection = {
                ExpensesTableContract.ExpenseEntry._ID,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_AMOUNT,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD
            };

        String selection = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT + " = ?";
        String[] selectionArgs = { String.valueOf(account_id) };
        String sortOrder = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE + " DESC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.ExpenseEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );

            if(c.getCount() > 0) {

                Expense ex;

                do {
                    c.moveToNext();

                    long currency_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY));
                    long category_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY));
                    long paymentMethod_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD));

                    ex = new Expense(
                            c.getLong(c.getColumnIndex(ExpensesTableContract.ExpenseEntry._ID)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_NAME)),
                            c.getLong(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT)),
                            DatabaseOtherManager.getInstance(context).getCategoryById(category_id),
                            c.getDouble(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_AMOUNT)),
                            DatabaseOtherManager.getInstance(context).getCurrencyById(currency_id),
                            c.getLong(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE)),
                            DatabasePaymentMethodsManager.getInstance(context).getPaymentMethodById(paymentMethod_id)
                    );
                    result.add(ex);
                } while(!c.isLast());
            }
        }

        return Collections.unmodifiableList(result);
    }

    public void deleteExpense(Expense expense) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(db != null) {
            String[] selectionArgs = { String.valueOf(expense.getId()) };
            String selection = ExpensesTableContract.ExpenseEntry._ID + " LIKE ?";
            db.delete(ExpensesTableContract.ExpenseEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_NAME, expense.getName());
        values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE, expense.getDate());
        values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY, expense.getCategory_id());
        values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD, expense.getPaymentMethod_id());
        values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT, expense.getAccount_id());
        values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_AMOUNT, expense.getAmount());
        values.put(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY, expense.getCurrency_id());

        String selection = ExpensesTableContract.ExpenseEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(expense.getId()) };

        if(db != null) {
            db.update(
                    ExpensesTableContract.ExpenseEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    public List<Expense> getExpensesByPaymentMethod(PaymentMethod paymentMethod) {
        List<Expense> result = new ArrayList<Expense>();

        String[] projection = {
                ExpensesTableContract.ExpenseEntry._ID,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_AMOUNT,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY,
                ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD
        };

        String selection = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD + " = ?";
        String[] selectionArgs = { String.valueOf(paymentMethod.getId()) };
        String sortOrder = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE + " DESC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.ExpenseEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {

                Expense ex;

                do {
                    c.moveToNext();

                    long currency_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY));
                    long category_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY));
                    long paymentMethod_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD));

                    ex = new Expense(
                            c.getLong(c.getColumnIndex(ExpensesTableContract.ExpenseEntry._ID)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_NAME)),
                            c.getLong(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT)),
                            DatabaseOtherManager.getInstance(context).getCategoryById(category_id),
                            c.getDouble(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_AMOUNT)),
                            DatabaseOtherManager.getInstance(context).getCurrencyById(currency_id),
                            c.getLong(c.getColumnIndex(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE)),
                            DatabasePaymentMethodsManager.getInstance(context).getPaymentMethodById(paymentMethod_id)
                    );
                    result.add(ex);
                } while(!c.isLast());
            }
        }

        return Collections.unmodifiableList(result);
    }

    public double getTotalAccountValue(long account_id) {
        double result = 0.0;

        for(Expense e : getExpensesByAccount(account_id)) {
            result += e.getAmountInUsd();
        }

        return result;
    }

    public int getNumberOfExpenses(long account_id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.rawQuery("select count(*) from " +
                    ExpensesTableContract.ExpenseEntry.TABLE_NAME +
                    " where " + ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT + " = ?",
                    new String[] { String.valueOf(account_id) });

            c.moveToFirst();
            if (c.getCount() > 0 && c.getColumnCount() > 0) {
                return c.getInt(0);
            }
        }

        return 0;
    }

    public Expense getAmount(long account_id, int type) {
        List<Expense> expenses = getExpensesByAccount(account_id);
        Expense result = null;

        for(int i = 0; i < expenses.size(); i++) {
            if(i == 0) {
                result = expenses.get(i);
                continue;
            }

            if(type == MAXIMUM && expenses.get(i).getAmountInUsd() > result.getAmountInUsd()) {
                result = expenses.get(i);
                continue;
            }

            if(type == MINIMUM && expenses.get(i).getAmountInUsd() < result.getAmountInUsd()) {
                result = expenses.get(i);
            }
        }

        if(result == null) {
            return new Expense("n/a", account_id, null, 0.0,
                    SharedPreferencesManager.getInstance(context).getBaseCurrency(), null);
        } else {
            return result;
        }
    }

    public double getAverageAmountInUsd(long account_id) {
        List<Expense> expenses = getExpensesByAccount(account_id);
        double sum = 0.0;

        for(Expense expense : expenses) {
            sum += expense.getAmountInUsd();
        }

        return sum > 0 ? sum / expenses.size() : 0.0;
    }

    public Object getMostOrLeastUsedProperty(long account_id, int type, int property) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            String table;
            switch(property) {
                case CURRENCY:
                    table = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY;
                    break;
                case CATEGORY:
                    table = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY;
                    break;
                case PAYMENT_METHOD:
                    table = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD;
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            String orderMethod;
            switch(type) {
                case MAXIMUM:
                    orderMethod = "desc";
                    break;
                case MINIMUM:
                    orderMethod = "asc";
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(table);
            sb.append(", count(");
            sb.append(table);
            sb.append(") from ");
            sb.append(ExpensesTableContract.ExpenseEntry.TABLE_NAME);
            sb.append(" where ");
            sb.append(ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT);
            sb.append(" = ?");
            sb.append(" group by ");
            sb.append(table);
            sb.append(" order by count(");
            sb.append(table);
            sb.append(") ");
            sb.append(orderMethod);
            sb.append(" limit 1");

            String[] selectionArgs = { String.valueOf(account_id) };

            Cursor c = db.rawQuery(sb.toString(), selectionArgs);

            c.moveToFirst();
            if(c.getCount() > 0) {
                long id = c.getLong(0);

                switch(property) {
                    case CURRENCY:
                        return DatabaseOtherManager.getInstance(context).getCurrencyById(id);
                    case CATEGORY:
                        return DatabaseOtherManager.getInstance(context).getCategoryById(id);
                    case PAYMENT_METHOD:
                        return DatabasePaymentMethodsManager.getInstance(context).getPaymentMethodById(id);
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }

        return "n/a";
    }

    public long[] getDateRange(long account_id) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        if(sqLiteDatabase != null) {
            String query = "select max(" +
                    ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE +
                    "), min(" +
                    ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE +
                    ") from " +
                    ExpensesTableContract.ExpenseEntry.TABLE_NAME +
                    " where " +
                    ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT +
                    " = ?";

            String[] selectionArgs = { String.valueOf(account_id) };

            Cursor c = sqLiteDatabase.rawQuery(query, selectionArgs);

            c.moveToFirst();
            if(c.getCount() > 0) {
                return new long[] { c.getLong(0), c.getLong(1)};
            }
        }

        return new long[] {};
    }

    public Map<Calendar, Double> getAmountPerDate(long account_id) {
        Map<Calendar, Double> result = new TreeMap<Calendar, Double>();
        Calendar date;

        for(Expense expense : getExpensesByAccount(account_id)) {
            date = Calendar.getInstance();
            date.setTimeInMillis(expense.getDate());
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);

            if(result.containsKey(date)) {
                result.put(date, result.get(date) + expense.getAmountInUsd());
            } else {
                result.put(date, expense.getAmountInUsd());
            }
        }

        return result;
    }

    public Map<String, Double> getAmountPer(long account_id, int type) {
        Map<String, Double> result = new TreeMap<String, Double>();
        String key;

        for(Expense expense : getExpensesByAccount(account_id)) {
            switch(type) {
                case DAY_OF_WEEK:
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(expense.getDate());
                    key = String.valueOf(date.get(Calendar.DAY_OF_WEEK));
                    break;
                case CURRENCY:
                    key = expense.getCurrencyCode();
                    break;
                case CATEGORY:
                    key = expense.getCategoryName();
                    break;
                case PAYMENT_METHOD:
                default:
                    key = expense.getPaymentMethodName();
                    break;
            }

            if(result.containsKey(key)) {
                result.put(key, result.get(key) + expense.getAmountInUsd());
            } else {
                result.put(key, expense.getAmountInUsd());
            }
        }

        return result;
    }
}
