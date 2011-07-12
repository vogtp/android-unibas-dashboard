package ch.unibas.urz.android.dashboard.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import ch.unibas.urz.android.dashboard.helper.Logger;
import ch.unibas.urz.android.dashboard.provider.db.DB;

public class AppModel {
	// {
	// "apphtmlurl":"http:\/\/perssearch.unibas.ch",
	// "nappexist":1, -> exisiert native app (auch iPhone)
	// "appdesc":"Personensuche der Universität Basel",
	// "appid":3,
	// "applabel":"PersSearch"
	// "appname":"PersSearch",
	// "appicon":"perssearch.png",
	// "NativeApp":
	// {
	// "nappdesc":"Personensuche für Android",
	// "nappname":"Perssearch",
	// "napploc":"weiss nicht",
	// "nappid":1,
	// "appid":3,
	// "nappsystem": "Android",
	// "napplabel":"Android"
	// },
	// }


	public static final String NOT_FOUND_STR = "na";
	public static final int NOT_FOUND_INT = -1;

	private long dbid = -1;
	private String appname;
	private int appid;
	private String label;
	private String description;
	private String url;
	private String packageName;
	private String icon;

	public AppModel() {
		super();
	}

	public AppModel(Cursor c) {
		super();
		dbid = c.getLong(DB.INDEX_ID);
		appname = c.getString(DB.DashboardApp.INDEX_APPNAME);
		appid = c.getInt(DB.DashboardApp.INDEX_APPID);
		label = c.getString(DB.DashboardApp.INDEX_LABEL);
		description = c.getString(DB.DashboardApp.INDEX_DESCRIPTION);
		url = c.getString(DB.DashboardApp.INDEX_URL);
		packageName = c.getString(DB.DashboardApp.INDEX_PACKAGE);
		icon = c.getString(DB.DashboardApp.INDEX_ICON);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (dbid > -1) {
			values.put(DB.NAME_ID, dbid);
		}
		values.put(DB.DashboardApp.NAME_APPNAME, appname);
		values.put(DB.DashboardApp.NAME_APPID, appid);
		values.put(DB.DashboardApp.NAME_LABEL, label);
		values.put(DB.DashboardApp.NAME_DESCRIPTION, description);
		values.put(DB.DashboardApp.NAME_URL, url);
		values.put(DB.DashboardApp.NAME_PACKAGE, packageName);
		values.put(DB.DashboardApp.NAME_ICON, icon);
		return values;
	}

	// {
	// "apphtmlurl":"http:\/\/perssearch.unibas.ch",
	// "nappexist":1, -> exisiert native app (auch iPhone)
	// "appdesc":"Personensuche der Universität Basel",
	// "appid":3,
	// "applabel":"PersSearch"
	// "appname":"PersSearch",
	// "appicon":"perssearch.png",
	// "NativeApp":
	// {
	// "nappdesc":"Personensuche für Android",
	// "nappname":"Perssearch",
	// "napploc":"weiss nicht",
	// "nappid":1,
	// "appid":3,
	// "nappsystem": "Android",
	// "napplabel":"Android"
	// },
	// }

	public AppModel(JSONObject json) {
		super();
		try {
			appname = json.getString(DB.DashboardApp.NAME_APPNAME);
		} catch (JSONException e) {
			Logger.w("Cannot read " + DB.DashboardApp.NAME_APPNAME, e);
			appname = NOT_FOUND_STR;
		}
		try {
			appid = json.getInt(DB.DashboardApp.NAME_APPID);
		} catch (JSONException e) {
			Logger.w("Cannot read " + DB.DashboardApp.NAME_APPID, e);
			appid = NOT_FOUND_INT;
		}
		try {
			label = json.getString(DB.DashboardApp.NAME_LABEL);
		} catch (JSONException e) {
			Logger.w("Cannot read " + DB.DashboardApp.NAME_LABEL, e);
			label = NOT_FOUND_STR;
		}
		try {
			description = json.getString(DB.DashboardApp.NAME_DESCRIPTION);
		} catch (JSONException e) {
			Logger.w("Cannot read " + DB.DashboardApp.NAME_DESCRIPTION, e);
			description = NOT_FOUND_STR;
		}
		try {
			url = json.getString(DB.DashboardApp.NAME_URL);
		} catch (JSONException e) {
			Logger.w("Cannot read " + DB.DashboardApp.NAME_URL, e);
			url = NOT_FOUND_STR;
		}
		try {
			icon = json.getString(DB.DashboardApp.NAME_ICON);
		} catch (JSONException e) {
			Logger.w("Cannot read ", e);
			icon = NOT_FOUND_STR;
		}
		try {
			JSONObject nativ = json.getJSONObject("NativeApp");
			packageName = nativ.getString(DB.DashboardApp.NAME_PACKAGE);
		} catch (JSONException e) {
			Logger.w("Cannot read " + DB.DashboardApp.NAME_PACKAGE, e);
			packageName = NOT_FOUND_STR;
		}
	}

	public String getName() {
		return appname;
	}

	public void setName(String name) {
		this.appname = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPackageName() {
		if (packageName == null) {
			return "";
		}
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean hasPackage() {
		if (packageName == null) {
			return false;
		}
		return !"".equals(packageName.trim());
	}

	public boolean hasUrl() {
		if (url == null) {
			return false;
		}
		return !"".equals(url.trim());
	}

	public long getDbid() {
		return dbid;
	}

	public void setDbid(long dbid) {
		this.dbid = dbid;
	}

	public int getAppId() {
		return appid;
	}

	public void setAppId(int id) {
		this.appid = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
