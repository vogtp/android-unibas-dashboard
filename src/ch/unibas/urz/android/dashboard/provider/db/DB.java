package ch.unibas.urz.android.dashboard.provider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import ch.unibas.urz.android.dashboard.helper.Logger;
import ch.unibas.urz.android.dashboard.provider.DashboardAppProvider;

public interface DB {

	public static final String DATABASE_NAME = "unibasDashboard";

	public static final String NAME_ID = "_id";
	public static final int INDEX_ID = 0;

	public static final String SELECTION_BY_ID = NAME_ID + "=?";

	public class OpenHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 2;

		private static final String CREATE_APPS_TABLE = "create table if not exists " + DashboardApp.TABLE_NAME + " (" + DB.NAME_ID + " integer primary key, "
				+ DB.DashboardApp.NAME_APPNAME + " text, " + DB.DashboardApp.NAME_APPID + " int," + DB.DashboardApp.NAME_LABEL + " text," + DB.DashboardApp.NAME_DESCRIPTION + " text,"
 + DB.DashboardApp.NAME_URL + " text," + DB.DashboardApp.NAME_ICON + " text," + DB.DashboardApp.NAME_PACKAGE + " text, " + DB.DashboardApp.NAME_HIDE
				+ " int default 0)";


		public OpenHelper(Context context) {
			super(context, DB.DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_APPS_TABLE);
			// db.execSQL("create index idx_app_id on " +
			// DashboardApp.TABLE_NAME + " (" + DashboardApp.NAME_APPID +
			// "); ");
			Logger.i("Created tables ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			switch (oldVersion) {
			case 1:
				Logger.w("Upgrading to DB Version 2...");
				db.execSQL("alter table " + DashboardApp.TABLE_NAME + " add column " + DashboardApp.NAME_HIDE + " int  default 0;");
				// nobreak


			default:
				Logger.w("Finished DB upgrading!");
				break;
			}
		}
	}

	public interface DashboardApp {

		public static final String TABLE_NAME = "unibasDashboard";

		public static final String CONTENT_ITEM_NAME = "unibasDashboard";
		public static String CONTENT_URI_STRING = "content://" + DashboardAppProvider.AUTHORITY + "/" + CONTENT_ITEM_NAME;
		public static Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

		static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + DashboardAppProvider.AUTHORITY + "." + CONTENT_ITEM_NAME;

		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + DashboardAppProvider.AUTHORITY + "." + CONTENT_ITEM_NAME;


		public static final String NAME_APPNAME = "appname";
		public static final String NAME_APPID = "appid";
		public static final String NAME_LABEL = "applabel";
		public static final String NAME_DESCRIPTION = "appdesc";
		public static final String NAME_URL = "apphtmlurl";
		public static final String NAME_ICON = "appicon";
		public static final String NAME_PACKAGE = "napploc";
		public static final String NAME_HIDE = "hideApp";

		public static final int INDEX_APPNAME = 1;
		public static final int INDEX_APPID = 2;
		public static final int INDEX_LABEL = 3;
		public static final int INDEX_DESCRIPTION = 4;
		public static final int INDEX_URL = 5;
		public static final int INDEX_ICON = 6;
		public static final int INDEX_PACKAGE = 7;
		public static final int INDEX_HIDE = 8;

		public static final String[] colNames = new String[] { NAME_ID, NAME_APPNAME, NAME_APPID, NAME_LABEL, NAME_DESCRIPTION, NAME_URL, NAME_ICON, NAME_PACKAGE, NAME_HIDE };

		public static final String[] PROJECTION_DEFAULT = colNames;

		public static final String SORTORDER_DEFAULT = NAME_APPID + " DESC";

		static final String SORTORDER_REVERSE = NAME_APPID + " ASC";

		public static final String SELECTION_BY_APPID = NAME_APPID + "=?";


	}
}