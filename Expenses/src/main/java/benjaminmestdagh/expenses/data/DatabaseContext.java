package benjaminmestdagh.expenses.data;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Class needed to store the database on the external storage.
 * Created by benjamin on 07/08/13.
 */
public class DatabaseContext extends ContextWrapper {

    private Context context;

    public DatabaseContext(Context base) {
        super(base);
        this.context = base;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        return openOrCreateDatabase(name, mode, factory, null);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name).getAbsolutePath(),null,errorHandler);

        return result;
    }

    @Override
    public File getDatabasePath(String name)
    {
        File directory = new File("/mnt/sdcard/Android/data/benjaminmestdagh.expenses/files");//context.getExternalFilesDir(null);
        if(directory == null) directory = new File("/data/data/benjaminmestdagh.expenses/files");

        String dbfile = directory.getAbsolutePath() + File.separator + "databases" + File.separator + name;

        if (!dbfile.endsWith(".db"))
        {
            dbfile += ".db" ;
        }

        File result = new File(dbfile);

        if (!result.getParentFile().exists())
        {
            result.getParentFile().mkdirs();
        }

        return result;
    }
}