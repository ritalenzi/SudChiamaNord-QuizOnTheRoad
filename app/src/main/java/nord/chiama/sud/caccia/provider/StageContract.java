package nord.chiama.sud.caccia.provider;


import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import nord.chiama.sud.caccia.R;

public class StageContract
{
    /**
     * The "Content authority" is a name for the entire content provider, similar to the relationship
     * between a domain name and its website.  A convenient string to use for the content authority
     * is the package name for the app, which must be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "nord.chiama.sud.caccia.provider.stageprovider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's that apps will use to contact the
     * content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse ("content://" + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible URI's), e.g.,
     * content://nord.chiama.sud.caccia/stage/ is a valid path for stage data. However,
     * content://nord.chiama.sud.caccia/givemeroot/ will fail since the ContentProvider hasn't been
     * given any information on what to do with "givemeroot".
     */
    public static final String PATH_STAGE = StageEntry.TABLE_NAME;

    /**
     * Inner class that defines the contents of the Stage table.
     */
    public static final class StageEntry implements BaseColumns
    {
        /**
         * Use BASE_CONTENT_URI to create the unique URI for Stage Table that apps will use to
         * contact the content provider.
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath (PATH_STAGE).build();

        /**
         * When the Cursor returned for a given URI by the ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_STAGE;

        /**
         * When the Cursor returned for a given URI by the ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_STAGE;

        /**
         * Name of the database table.
         */
        public static final String TABLE_NAME = "stage_table";

        /**
         * Columns to store data of each stage of the treasure hunt
         */
        public static final String COLUMN_NUMBER = "number";
//        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_CLUE = "clue";
        public static final String COLUMN_TEST = "test";
//        public static final String COLUMN_CORRECT_POSITION = "correct_position";

        /**
         * Return a Uri that points to the row containing a given id
         * @param id row id
         * @return Uri uri corresponding to the given id
         */
        public static Uri buildVideoUri (Long id)
        {
            return ContentUris.withAppendedId (CONTENT_URI, id);
        }
    }
}
