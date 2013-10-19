package benjaminmestdagh.expenses.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import benjaminmestdagh.expenses.entities.Account;

/**
 * Created by benjamin on 30/07/13.
 */
public class DatabaseAccountsManager {

    private static DatabaseAccountsManager dbmanager = null;
    private SQLiteOpenHelper databaseHelper;
    private Context context;

    private DatabaseAccountsManager(Context context) {
        databaseHelper = new ExpensesDbHelper(context);
    }

    public static DatabaseAccountsManager getInstance(Context context) {
        if (dbmanager == null) {
            dbmanager = new DatabaseAccountsManager(context);
        }

        return dbmanager;
    }

    public void deleteAccount(Account account) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(db != null) {
            String[] selectionArgs = { String.valueOf(account.getId()) };

            // Delete all of the account's expenses
            String selection = ExpensesTableContract.ExpenseEntry.COLUMN_NAME_ACCOUNT + " LIKE ?";
            db.delete(ExpensesTableContract.ExpenseEntry.TABLE_NAME, selection, selectionArgs);

            // Delete the account
            selection = ExpensesTableContract.AccountEntry._ID + " LIKE ?";
            db.delete(ExpensesTableContract.AccountEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    public void insertAccount(Account account) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(db != null) {
            ContentValues values = new ContentValues();

            values.put(ExpensesTableContract.AccountEntry.COLUMN_NAME_NAME, account.getName());
            values.put(ExpensesTableContract.AccountEntry.COLUMN_NAME_CURRENCY, account.getCurrency_id());
            values.put(ExpensesTableContract.AccountEntry.COLUMN_NAME_DATE, account.getDate());

            long newRowId;
            newRowId = db.insert(ExpensesTableContract.AccountEntry.TABLE_NAME, null, values);

            account.setId(newRowId);
        }
    }

    public List<Account> getAccounts() {
        List<Account> result = new ArrayList<Account>();

        String[] projection = {
                ExpensesTableContract.AccountEntry._ID,
                ExpensesTableContract.AccountEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.AccountEntry.COLUMN_NAME_CURRENCY,
                ExpensesTableContract.AccountEntry.COLUMN_NAME_DATE
        };

        String sortOrder = ExpensesTableContract.AccountEntry.COLUMN_NAME_NAME;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.AccountEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {

                Account ac;

                do {
                    c.moveToNext();

                    long currency_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.AccountEntry.COLUMN_NAME_CURRENCY));

                    ac = new Account(
                            c.getLong(c.getColumnIndex(ExpensesTableContract.AccountEntry._ID)),
                            c.getString(c.getColumnIndex(ExpensesTableContract.AccountEntry.COLUMN_NAME_NAME)),
                            DatabaseOtherManager.getInstance(context).getCurrencyById(currency_id),
                            c.getLong(c.getColumnIndex(ExpensesTableContract.AccountEntry.COLUMN_NAME_DATE))
                    );
                    result.add(ac);
                } while(!c.isLast());
            }
        }

        return Collections.unmodifiableList(result);
    }

    public void updateAccount(Account account) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesTableContract.AccountEntry.COLUMN_NAME_NAME, account.getName());
        values.put(ExpensesTableContract.AccountEntry.COLUMN_NAME_CURRENCY, account.getCurrency_id());

        String selection = ExpensesTableContract.AccountEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(account.getId()) };

        if(db != null) {
        db.update(
                ExpensesTableContract.AccountEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        }
    }
}
