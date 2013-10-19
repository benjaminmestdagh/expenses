package benjaminmestdagh.expenses.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import benjaminmestdagh.expenses.entities.PaymentMethod;
import benjaminmestdagh.expenses.entities.PaymentMethodType;

/**
 * Created by benjamin on 10/08/13.
 */
public class DatabasePaymentMethodsManager {

    private static DatabasePaymentMethodsManager dbmanager = null;
    private SQLiteOpenHelper databaseHelper;
    private Context context;

    private DatabasePaymentMethodsManager(Context context) {
        databaseHelper = new ExpensesDbHelper(context);
        this.context = context;
    }

    public static DatabasePaymentMethodsManager getInstance(Context context) {
        if (dbmanager == null) {
            dbmanager = new DatabasePaymentMethodsManager(context);
        }

        return dbmanager;
    }

    public List<PaymentMethod> getPaymentMethods() {
        List<PaymentMethod> result = new ArrayList<PaymentMethod>();

        String[] projection = {
                ExpensesTableContract.PaymentMethodEntry._ID,
                ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE
        };

        String sortOrder = ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.PaymentMethodEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {

                PaymentMethod paymentMethod;

                do {
                    c.moveToNext();

                    long type_id = c.getLong(c.getColumnIndex(
                            ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE));

                    paymentMethod = new PaymentMethod(
                            c.getLong(c.getColumnIndex(
                                    ExpensesTableContract.PaymentMethodEntry._ID)),
                            c.getString(c.getColumnIndex(
                                    ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME)),
                            getPaymentMethodType(type_id)
                    );
                    result.add(paymentMethod);
                } while(!c.isLast());
            }
        }

        return Collections.unmodifiableList(result);
    }

    public PaymentMethod getPaymentMethodById(long paymentMethod_id) {
        String[] projection = {
                ExpensesTableContract.PaymentMethodEntry._ID,
                ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE
        };

        String selection = ExpensesTableContract.PaymentMethodEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(paymentMethod_id) };
        String sortOrder = ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.PaymentMethodEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {
                c.moveToNext();

                long type_id = c.getLong(c.getColumnIndex(
                        ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE));

                return new PaymentMethod(
                        c.getLong(c.getColumnIndex(ExpensesTableContract.PaymentMethodEntry._ID)),
                        c.getString(c.getColumnIndex(
                                ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME)),
                        getPaymentMethodType(type_id)
                );
            }
        }

        return null;
    }

    public void insertPaymentMethod(PaymentMethod paymentMethod) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(db != null) {
            ContentValues values = new ContentValues();

            values.put(ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME, paymentMethod.getName());
            values.put(ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE, paymentMethod.getType_id());

            long newRowId;
            newRowId = db.insert(ExpensesTableContract.PaymentMethodEntry.TABLE_NAME, null, values);

            paymentMethod.setId(newRowId);
        }
    }

    public void updatePaymentMethod(PaymentMethod paymentMethod) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_NAME, paymentMethod.getName());
        values.put(ExpensesTableContract.PaymentMethodEntry.COLUMN_NAME_TYPE, paymentMethod.getType_id());

        String selection = ExpensesTableContract.PaymentMethodEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(paymentMethod.getId()) };

        if(db != null) {
            db.update(
                    ExpensesTableContract.PaymentMethodEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    public void deletePaymentMethod(PaymentMethod paymentMethod) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(db != null) {
            String[] selectionArgs = { String.valueOf(paymentMethod.getId()) };
            String selection = ExpensesTableContract.PaymentMethodEntry._ID + " LIKE ?";
            db.delete(ExpensesTableContract.PaymentMethodEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    public List<PaymentMethodType> getPaymentMethodTypes() {
        List<PaymentMethodType> result = new ArrayList<PaymentMethodType>();

        String[] projection = {
                ExpensesTableContract.PaymentMethodTypesEntry._ID,
                ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_ICON
        };

        String sortOrder = ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.PaymentMethodTypesEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {
                do {
                    c.moveToNext();
                    result.add(new PaymentMethodType(
                            c.getLong(c.getColumnIndex(
                                    ExpensesTableContract.PaymentMethodTypesEntry._ID)),
                            c.getString(c.getColumnIndex(
                                    ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME)),
                            c.getString(c.getColumnIndex(
                                    ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_ICON))
                    ));
                } while(!c.isLast());
            }
        }

        return Collections.unmodifiableList(result);
    }

    private PaymentMethodType getPaymentMethodType(long type_id) {
        String[] projection = {
                ExpensesTableContract.PaymentMethodTypesEntry._ID,
                ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME,
                ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_ICON
        };

        String selection = ExpensesTableContract.PaymentMethodTypesEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(type_id) };
        String sortOrder = ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if(db != null) {
            Cursor c = db.query(
                    ExpensesTableContract.PaymentMethodTypesEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if(c.getCount() > 0) {
                c.moveToNext();
                return new PaymentMethodType(
                        c.getLong(c.getColumnIndex(
                                ExpensesTableContract.PaymentMethodTypesEntry._ID)),
                        c.getString(c.getColumnIndex(
                                ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_NAME)),
                        c.getString(c.getColumnIndex(
                                ExpensesTableContract.PaymentMethodTypesEntry.COLUMN_NAME_ICON))
                );
            }
        }

        return null;
    }
}
