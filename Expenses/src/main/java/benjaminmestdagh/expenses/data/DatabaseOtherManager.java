package benjaminmestdagh.expenses.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import benjaminmestdagh.expenses.entities.Category;
import benjaminmestdagh.expenses.entities.Currency;
import benjaminmestdagh.expenses.entities.PaymentMethod;
import benjaminmestdagh.expenses.entities.PaymentMethodType;

/**
 * Created by benjamin on 06/08/13.
 */
public class DatabaseOtherManager {
    private static DatabaseOtherManager dbmanager = null;
    private SQLiteOpenHelper databaseHelper;

    private DatabaseOtherManager(Context context) {
        databaseHelper = new ExpensesDbHelper(context);
    }

    public static DatabaseOtherManager getInstance(Context context) {
        if (dbmanager == null) {
            dbmanager = new DatabaseOtherManager(context);
        }

        return dbmanager;
    }


    ////////////////
    // CURRENCIES //
    ////////////////

    public List<Currency> getCurrencies() {
        List<Currency> result = new ArrayList<Currency>();

        String[] projection = {
                ExpensesTableContract.CurrencyEntry._ID,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE
        };

        String sortOrder = ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.CurrencyEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {

                Currency currency;

                do {
                    c.moveToNext();
                    currency = new Currency(
                            c.getLong(c.getColumnIndex(ExpensesTableContract.CurrencyEntry._ID)),
                            c.getDouble(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT))
                    );
                    result.add(currency);
                } while(!c.isLast());
            }
        }

        return Collections.unmodifiableList(result);
    }

    public Currency getCurrencyById(long currency_id) {
        String[] projection = {
                ExpensesTableContract.CurrencyEntry._ID,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE
        };

        String selection = ExpensesTableContract.CurrencyEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(currency_id) };
        String sortOrder = ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.CurrencyEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {
                    c.moveToNext();
                    return new Currency(
                            c.getLong(c.getColumnIndex(ExpensesTableContract.CurrencyEntry._ID)),
                            c.getDouble(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT))
                    );
            }
        }

        return null;
    }

    public Currency getCurrencyByCode(String code) {
        String[] projection = {
                ExpensesTableContract.CurrencyEntry._ID,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT,
                ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE
        };

        String selection = ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT + " LIKE ?";
        String[] selectionArgs = { code };
        String sortOrder = ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.CurrencyEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {
                c.moveToNext();
                return new Currency(
                        c.getLong(c.getColumnIndex(ExpensesTableContract.CurrencyEntry._ID)),
                        c.getDouble(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE)),
                        c.getString(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME)),
                        c.getString(c.getColumnIndex(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT))
                );
            }
        }

        return null;
    }

    public void updateCurrency(Currency currency) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_NAME, currency.getName());
        values.put(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_SHORT, currency.getCode());
        values.put(ExpensesTableContract.CurrencyEntry.COLUMN_NAME_VALUE, currency.getValue());

        String selection = ExpensesTableContract.CurrencyEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(currency.getId()) };

        if(db != null) {
            db.update(
                    ExpensesTableContract.CurrencyEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }



    ////////////////
    // CATEGORIES //
    ////////////////

    public List<Category> getCategories() {
        List<Category> result = new ArrayList<Category>();

        String[] projection = {
                ExpensesTableContract.CategoryEntry._ID,
                ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME
        };

        String sortOrder = ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.CategoryEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {

                Category category;

                do {
                    c.moveToNext();
                    category = new Category(
                            c.getLong(c.getColumnIndex(ExpensesTableContract.CategoryEntry._ID)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME))
                    );
                    result.add(category);
                } while(!c.isLast());
            }
        }

        return Collections.unmodifiableList(result);
    }

    public Category getCategoryById(long category_id) {
        String[] projection = {
                ExpensesTableContract.CategoryEntry._ID,
                ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME
        };

        String selection = ExpensesTableContract.CategoryEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(category_id) };
        String sortOrder = ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.CategoryEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {
                c.moveToNext();
                return new Category(
                        c.getLong(c.getColumnIndex(ExpensesTableContract.CategoryEntry._ID)),
                        c.getString(c.getColumnIndex(ExpensesTableContract.CategoryEntry.COLUMN_NAME_NAME))
                );
            }
        }

        return null;
    }
}
