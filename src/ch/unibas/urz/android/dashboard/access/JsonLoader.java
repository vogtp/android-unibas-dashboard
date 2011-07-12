package ch.unibas.urz.android.dashboard.access;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import ch.unibas.urz.android.dashboard.helper.Debugger;
import ch.unibas.urz.android.dashboard.helper.Logger;
import ch.unibas.urz.android.dashboard.model.AppModel;
import ch.unibas.urz.android.dashboard.provider.db.DB;

public class JsonLoader {

	public static void loadApps(Context ctx) {
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(loadData());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);
				AppModel am = new AppModel(object);
				insertOrUpdate(ctx, am);
			}
		} catch (JSONException e) {
			Logger.e("Cannot get applist from the network", e);
		}

	}

	private static void insertOrUpdate(Context ctx, AppModel am) {
		ContentResolver contentResolver = ctx.getContentResolver();
		String[] selectionArgs = new String[] { Integer.toString(am.getAppId()) };
		Cursor c = contentResolver.query(DB.DashboardApp.CONTENT_URI, DB.DashboardApp.PROJECTION_DEFAULT, DB.DashboardApp.SELECTION_BY_APPID, selectionArgs,
				DB.DashboardApp.SORTORDER_DEFAULT);
		try {
		if (c.moveToFirst()) {
			Logger.d("update " + am.getName());
			am.setDbid(c.getLong(DB.INDEX_ID));
			contentResolver.update(DB.DashboardApp.CONTENT_URI, am.getValues(), DB.DashboardApp.SELECTION_BY_APPID, selectionArgs);
		} else {
			Logger.d("insert " + am.getName());
			contentResolver.insert(DB.DashboardApp.CONTENT_URI, am.getValues());
		}
		} catch (Throwable t) {
			Logger.e("Fail to insert ", t);
		}
	}

	private static String loadData() {
		return Debugger.jsonString;
	}

}
