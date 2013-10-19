package benjaminmestdagh.expenses;

import android.content.Context;
import android.widget.ListView;

/**
 * Created by benjamin on 06/08/13.
 */
public class DatabaseThread extends Thread {
    protected Context context;
    protected ListView listView;

    public DatabaseThread(Context context, ListView listview) {
        this.context = context;
        this.listView = listview;
    }

    public DatabaseThread(Context context) {
        this(context, null);
    }
}
