package it.sudchiamanord.quizontheroad.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import it.sudchiamanord.quizontheroad.provider.StageContract.StageEntry;

public class StageProvider extends ContentProvider
{
    private static final String TAG = StageProvider.class.getSimpleName();

    /**
     * Use VideoDatabaseHelper to manage database creation and version management.
     */
    private DatabaseHelper mOpenHelper;

    /**
     * The code that is returned when a URI for 1 item is matched against the given components.
     * Must be positive.
     */
    private static final int STAGE = 100;

    /**
     * The code that is returned when a URI for more than 1 items is matched against the given
     * components.  Must be positive.
     */
    private static final int STAGES = 101;

    /**
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Helper method to match each URI to the VIDEO integers constant defined above.
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher()
    {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found. The code passed into the constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is created.
        matcher.addURI (StageContract.CONTENT_AUTHORITY, StageContract.PATH_STAGE + "/#", STAGE);
        matcher.addURI (StageContract.CONTENT_AUTHORITY, StageContract.PATH_STAGE, STAGES);
        return matcher;
    }


    @Override
    public boolean onCreate()
    {
        mOpenHelper = new DatabaseHelper (getContext());

        return true;
    }

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder)
    {
        Cursor retCursor;

        switch (sUriMatcher.match (uri)) {
            case STAGES:
                retCursor = mOpenHelper.getReadableDatabase().query (StageEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case STAGE:
                final String rowId = StageEntry._ID + " = '" + ContentUris.parseId (uri) + "'";
                String uSelection = (selection == null ? rowId : selection + " AND " + rowId);
                retCursor = mOpenHelper.getReadableDatabase().query (StageEntry.TABLE_NAME,
                        projection, uSelection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri (getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType (Uri uri)
    {
        // Match the id returned by UriMatcher to return appropriate MIME_TYPE
        switch (sUriMatcher.match (uri)) {
            case STAGE:
                return StageEntry.CONTENT_ITEM_TYPE;
            case STAGES:
                return StageEntry.CONTENT_ITEMS_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert (Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        switch (sUriMatcher.match (uri)) {
            case STAGES:
                long id = db.insert (StageEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = StageEntry.buildVideoUri (id);
                }
                else {
                    throw new android.database.SQLException ("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange (uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert (Uri uri, ContentValues[] contentValues)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match (uri)) {
            case STAGES:
                db.beginTransaction();
                int returnCount = 0;

                try {
                    for (ContentValues cv : contentValues) {
                        if (cv == null) {
                            continue;
                        }
                        long id = db.insert (StageEntry.TABLE_NAME, null, cv);
                        if (id > 0) {
                            returnCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange (uri, null);

                return returnCount;

            default:
                return super.bulkInsert (uri, contentValues);
        }
    }

    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs)
    {
//        throw new UnsupportedOperationException ("Treasure hunt stages cannot be deleted at this point");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (sUriMatcher.match (uri)) {
            case STAGES:
                rowsDeleted = db.delete (StageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if ((selection == null) || (rowsDeleted != 0)) {
            getContext().getContentResolver().notifyChange (uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update (Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsUpdated;

        switch (sUriMatcher.match (uri)) {
            case STAGE:
                rowsUpdated = db.update (StageEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange (uri, null);
        }

        return rowsUpdated;
    }
}
