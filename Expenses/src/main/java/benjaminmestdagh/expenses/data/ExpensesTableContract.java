package benjaminmestdagh.expenses.data;

import android.provider.BaseColumns;

/**
 * Created by benjamin on 30/07/13.
 */
public final class ExpensesTableContract {

    public ExpensesTableContract() {}

    public static abstract class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CURRENCY = "currency_id";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static abstract class ExpenseEntry implements BaseColumns {
        public static final String TABLE_NAME = "expenses";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_ACCOUNT = "account_id";
        public static final String COLUMN_NAME_CATEGORY = "category_id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_CURRENCY = "currency_id";
        public static final String COLUMN_NAME_PAYMENT_METHOD = "paymentMethod_id";
    }

    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static abstract class CurrencyEntry implements BaseColumns {
        public static final String TABLE_NAME = "currencies";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SHORT = "short";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    public static abstract class PaymentMethodEntry implements BaseColumns {
        public static final String TABLE_NAME = "payment_methods";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    public static abstract class PaymentMethodTypesEntry implements BaseColumns {
        public static final String TABLE_NAME = "payment_method_types";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ICON = "icon";
    }
}
