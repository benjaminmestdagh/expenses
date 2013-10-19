package benjaminmestdagh.expenses.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Map;
import java.util.TreeMap;

import benjaminmestdagh.expenses.entities.Currency;

/**
 * Created by benjamin on 30/07/13.
 */
public class ExpensesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Expenses.db";

    private static final String CREATE_DB_TEXT = "CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_DB_TEXT = "DROP TABLE IF EXISTS ";
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_ACCOUNTS =
            CREATE_DB_TEXT + ExpensesTableContract.AccountEntry.TABLE_NAME + " (" +
            ExpensesTableContract.AccountEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            ExpensesTableContract.AccountEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            ExpensesTableContract.AccountEntry.COLUMN_NAME_CURRENCY + " INTEGER" + COMMA_SEP +
            ExpensesTableContract.AccountEntry.COLUMN_NAME_DATE + DATE_TYPE +
        " )";

    private static final String SQL_CREATE_TABLE_EXPENSES =
            CREATE_DB_TEXT + ExpensesTableContract.ExpenseEntry.TABLE_NAME + " (" +
            ExpensesTableContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_DATE + DATE_TYPE + COMMA_SEP +
            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT + " INTEGER" + COMMA_SEP +
            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CATEGORY + " INTEGER" + COMMA_SEP +
            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_AMOUNT + " REAL" + COMMA_SEP +
            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_CURRENCY + " INTEGER" + COMMA_SEP +
            ExpensesTableContract.ExpenseEntry.COLUMN_NAME_PAYMENT_METHOD + " INTEGER" +
        " )";

    private static final String SQL_CREATE_TABLE_CATEGORIES =
        CREATE_DB_TEXT + ExpensesTableContract.CategoryEntry.TABLE_NAME + " (" +
            ExpensesTableContract.CategoryEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME + TEXT_TYPE +
        " )";

    private static final String SQL_CREATE_TABLE_CURRENCIES =
        CREATE_DB_TEXT + ExpensesTableContract.CurrencyEntry.TABLE_NAME + " (" +
            ExpensesTableContract.CurrencyEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT + TEXT_TYPE + COMMA_SEP +
            ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE + " REAL" +
        " )";

    private static final String SQL_CREATE_TABLE_PAYMENT_METHODS =
        CREATE_DB_TEXT + ExpensesTableContract.PaymentMethodEntry.TABLE_NAME + " (" +
                ExpensesTableContract.PaymentMethodEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE + " INTEGER" +
        " )";

    private static final String SQL_CREATE_TABLE_PAYMENT_METHOD_TYPES =
            CREATE_DB_TEXT + ExpensesTableContract.PaymentMethodTypesEntry.TABLE_NAME + " (" +
                    ExpensesTableContract.PaymentMethodTypesEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_ICON + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_TABLE_ACCOUNTS =
        DROP_DB_TEXT + ExpensesTableContract.AccountEntry.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_EXPENSES =
        DROP_DB_TEXT + ExpensesTableContract.ExpenseEntry.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_CATEGORIES =
        DROP_DB_TEXT + ExpensesTableContract.CategoryEntry.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_CURRENCIES =
        DROP_DB_TEXT + ExpensesTableContract.CurrencyEntry.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_PAYMENT_METHODS =
        DROP_DB_TEXT + ExpensesTableContract.PaymentMethodEntry.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_PAYMENT_METHOD_TYPES =
            DROP_DB_TEXT + ExpensesTableContract.PaymentMethodTypesEntry.TABLE_NAME;

    public ExpensesDbHelper(Context context) {
        super(new DatabaseContext(context), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ACCOUNTS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXPENSES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_CURRENCIES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PAYMENT_METHOD_TYPES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PAYMENT_METHODS);

        insertCategories(sqLiteDatabase);
        insertCurrencies(sqLiteDatabase);
        insertPaymentMethodTypes(sqLiteDatabase);
        insertPaymentMethods(sqLiteDatabase);
    }

    private void insertCategories(SQLiteDatabase sqLiteDatabase) {
        String[] categories = {"Automobile", "Cable", "Clothing", "Electricity", "Entertainment",
                "Gas", "Groceries", "Gifts", "Health Care", "Hotel/Camping", "Household",
                "Insurance", "Internet", "Loans", "Medical Costs", "Outgoing Payments or Transfers",
                "Personal Care", "Phone", "Retirement Savings", "Rent", "Restaurant",
                "Sports/Recreation", "Subscriptions", "Taxes", "Utilities", "Water" };

        ContentValues values = new ContentValues();

        for(String category : categories) {
            values.put(ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME, category);
            sqLiteDatabase.insert(ExpensesTableContract.CategoryEntry.TABLE_NAME, null, values);
            values.clear();
        }
    }

    private void insertCurrencies(SQLiteDatabase sqLiteDatabase) {
        try {
            Map<String, Double> exchangeRates =
                    OpenExchangeRatesClient.getInstance().getExchangeRates();
            ContentValues values = new ContentValues();

            for(Currency c : OpenExchangeRatesClient.getInstance().getCurrencies()) {
                values.put(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME, c.getName());
                values.put(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT, c.getCode());
                values.put(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE,
                        exchangeRates.get(c.getCode()));
                sqLiteDatabase.insert(ExpensesTableContract.CurrencyEntry.TABLE_NAME, null, values);
                values.clear();
            }
        } catch (Exception ex) {}
    }

    private void insertPaymentMethodTypes(SQLiteDatabase sqLiteDatabase) {
        Map<String, String> types = new TreeMap<String, String>();
        types.put("American Express", "amex");
        types.put("Cash", "banknotes");
        types.put("Cheque", "checkbook");
        types.put("Credit card", "bankcards");
        types.put("Debit card", "bankcards");
        types.put("JCB", "jcb");
        types.put("MasterCard", "mastercard");
        types.put("PayPal", "paypal");
        types.put("Visa", "visa");
        types.put("Other", "coins");

        ContentValues values = new ContentValues();

        for(String type : types.keySet()) {
            values.put(ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME, type);
            values.put(ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_ICON, types.get(type));
            sqLiteDatabase.insert(ExpensesTableContract.PaymentMethodTypesEntry.TABLE_NAME, null, values);
            values.clear();
        }
    }

    private void insertPaymentMethods(SQLiteDatabase sqLiteDatabase) {
        Map<String, Long> methods = new TreeMap<String, Long>();
        methods.put("Cash", 2L);
        methods.put("My debit card", 5L);
        methods.put("My credit card", 4L);

        ContentValues values = new ContentValues();

        for(String method : methods.keySet()) {
            values.put(ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME, method);
            values.put(ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE, methods.get(method));
            sqLiteDatabase.insert(ExpensesTableContract.PaymentMethodEntry.TABLE_NAME, null, values);
            values.clear();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_EXPENSES);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_ACCOUNTS);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_CURRENCIES);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_PAYMENT_METHOD_TYPES);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_PAYMENT_METHODS);
        onCreate(sqLiteDatabase);
    }
}
