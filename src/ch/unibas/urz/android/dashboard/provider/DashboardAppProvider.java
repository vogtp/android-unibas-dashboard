package ch.unibas.urz.android.dashboard.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import ch.unibas.urz.android.dashboard.provider.db.DB;
import ch.unibas.urz.android.dashboard.provider.db.DB.OpenHelper;
import ch.unibas.urz.android.dashboard.provider.db.DBBackendDashboardApp;

public class DashboardAppProvider extends ContentProvider {

	public static final String AUTHORITY = "ch.unibas.urz.android.dashboard";

	private static final int DASHBOARD_APP = 1;

	private static final UriMatcher sUriMatcher;

	private OpenHelper openHelper;

	@Override
	public boolean onCreate() {
		openHelper = new OpenHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case DASHBOARD_APP:
			count = DBBackendDashboardApp.delete(openHelper, uri, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case DASHBOARD_APP:
			return DBBackendDashboardApp.getType(uri);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri ret;
		switch (sUriMatcher.match(uri)) {
		case DASHBOARD_APP:
			ret = DBBackendDashboardApp.insert(openHelper, uri, initialValues);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return ret;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor c;
		switch (sUriMatcher.match(uri)) {
		case DASHBOARD_APP:
			c = DBBackendDashboardApp.query(openHelper, uri, projection, selection, selectionArgs, sortOrder);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case DASHBOARD_APP:
			count = DBBackendDashboardApp.update(openHelper, uri, values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return count;
	}

	private void notifyChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, DB.DashboardApp.CONTENT_ITEM_NAME, DASHBOARD_APP);
		sUriMatcher.addURI(AUTHORITY, DB.DashboardApp.CONTENT_ITEM_NAME + "/#", DASHBOARD_APP);
	}


}
