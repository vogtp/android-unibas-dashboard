package ch.unibas.urz.android.dashboard.helper;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Settings {

	public final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private static final String UPDATE_TIME = "updateTime";

	public static final int APP_LIST_LIST = 1;
	public static final int APP_LIST_GRID = 2;

	public static final int APP_APPEARIANCE_UNIBAS_TURQUISE = 1;
	public static final int APP_APPEARIANCE_ANDROID = 2;

	private static final long MINUTE_IN_MILLIES = 60000;
	private static final long HOUR_IN_MILLIES = 60 * MINUTE_IN_MILLIES;
	private static final long DAY_IN_MILLIES = 24 * HOUR_IN_MILLIES;
	private static final long WEEK_IN_MILLIES = 7 * DAY_IN_MILLIES;
	private static Settings instance;
	private final Context context;

	public static void initInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx);
		}
	}

	public static Settings getInstance() {
		return instance;
	}

	protected Settings(Context ctx) {
		super();
		context = ctx;
	}

	protected SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public long getUpdateTime() {
		return getPreferences().getLong(UPDATE_TIME, -1);
	}

	public void setUpdateTimeNow() {
		Editor editor = getPreferences().edit();
		editor.putLong(UPDATE_TIME, System.currentTimeMillis());
		editor.commit();
	}

	public boolean isUpdateNeeded() {
		long delta = System.currentTimeMillis() - getUpdateTime();
		return delta > getUpdateFrequencyInMillis();
	}

	private long getUpdateFrequencyInMillis() {
		switch (getUpdateFrequency()) {
		case 1:
			return MINUTE_IN_MILLIES;
		case 2:
			return HOUR_IN_MILLIES;
		case 3:
			return DAY_IN_MILLIES;
		case 4:
			return WEEK_IN_MILLIES;
		case 5:
			return Long.MAX_VALUE;
		default:
			return DAY_IN_MILLIES;
		}
	}

	private int getUpdateFrequency() {
		try {
			return Integer.parseInt(getPreferences().getString("prefKeyUpdateFrequency", "3"));
		} catch (NumberFormatException e) {
			return 3;
		}
	}

	public int getAppListStyle() {
		try {
			return Integer.parseInt(getPreferences().getString("prefKeyAppListStyle", "1"));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	public int getAppAppearance() {
		try {
			return Integer.parseInt(getPreferences().getString("prefKeyAppAppearance", "1"));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}
