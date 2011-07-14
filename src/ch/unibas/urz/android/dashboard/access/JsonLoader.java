package ch.unibas.urz.android.dashboard.access;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import ch.unibas.urz.android.dashboard.helper.Logger;
import ch.unibas.urz.android.dashboard.model.AppModel;
import ch.unibas.urz.android.dashboard.provider.db.DB;

public class JsonLoader {
	private static final String APP_JSON_URL = "http://nikt.unibas.ch/mobileapp/json.cfm";

	// private static final String APP_JSON_URL =
	// "http://urz-cfaa.urz.unibas.ch/muriel/MobileApps/json.cfm";

	public static void loadApps(Context ctx) {
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(loadData());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);
				AppModel am = new AppModel(object);
				insertOrUpdate(ctx, am);
			}
		} catch (Exception e) {
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
				am.setHide(c.getInt(DB.DashboardApp.INDEX_HIDE));
			contentResolver.update(DB.DashboardApp.CONTENT_URI, am.getValues(), DB.DashboardApp.SELECTION_BY_APPID, selectionArgs);
		} else {
			Logger.d("insert " + am.getName());
			contentResolver.insert(DB.DashboardApp.CONTENT_URI, am.getValues());
		}
		} catch (Throwable t) {
			Logger.e("Fail to insert ", t);
		}
	}

	private static String loadData() throws Exception {
		URL aUrl = new URL(APP_JSON_URL);
		URLConnection conn = aUrl.openConnection();
		conn.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

}
