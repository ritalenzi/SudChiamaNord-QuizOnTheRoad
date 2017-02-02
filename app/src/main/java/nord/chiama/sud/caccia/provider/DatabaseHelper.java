package nord.chiama.sud.caccia.provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import nord.chiama.sud.caccia.provider.StageContract.StageEntry;

import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper
{
    /**
     * If the database schema is changed, the database version must be incremented.
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Database name.
     */
    public static final String DATABASE_NAME = "treasureHunt.db";


    public DatabaseHelper (Context context)
    {
        super (context, context.getCacheDir() + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db)
    {
        db.execSQL (
                "CREATE TABLE "
                + StageEntry.TABLE_NAME + " ("
                + StageEntry._ID + "INTEGER PRIMARY KEY, "
                + StageEntry.COLUMN_NUMBER + " INTEGER NOT NULL, "
//                + StageEntry.COLUMN_LOCATION + " TEXT, "
                + StageEntry.COLUMN_STATUS + " INTEGER NOT NULL, "
                + StageEntry.COLUMN_CLUE + " TEXT NOT NULL, "
                + StageEntry.COLUMN_TEST + " TEXT NOT NULL"
//                + StageEntry.COLUMN_TEST + " TEXT NOT NULL, "
//                + StageEntry.COLUMN_CORRECT_POSITION + " INTEGER NOT NULL"  // no boolean type in sqlite, using 1 for true and 0 for false
                + ");");
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This database is only a cache for online data, so its upgrade policy is simply to discard
        // the data and start over. This method only fires if you change the version number for your
        // database. It does NOT depend on the version number for your application. If the schema is
        // updated without wiping data, commenting out the next 2 lines should be the top priority
        // before modifying this method
        db.execSQL ("DROP TABLE IF EXISTS " + StageEntry.TABLE_NAME);
        onCreate (db);
    }
}
