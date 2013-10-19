package benjaminmestdagh.expenses;

import android.app.Application;
import android.content.Context;

/**
 * Created by benjamin on 19/10/13.
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}
