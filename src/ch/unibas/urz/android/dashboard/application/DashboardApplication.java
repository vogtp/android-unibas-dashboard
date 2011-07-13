package ch.unibas.urz.android.dashboard.application;

import android.app.Application;
import ch.unibas.urz.android.dashboard.helper.Settings;

public class DashboardApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Settings.initInstance(getApplicationContext());
	}

}
