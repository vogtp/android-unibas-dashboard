package ch.unibas.urz.android.dashboard.view.preferences;


import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import ch.unibas.urz.android.dashboard.R;
import ch.unibas.urz.android.dashboard.helper.AsyncDataLoader;
import ch.unibas.urz.android.dashboard.helper.AsyncDataLoader.LoaderCallback;
import ch.unibas.urz.android.dashboard.helper.Settings;

public class DashboardPreferenceActivity extends PreferenceActivity implements LoaderCallback {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.dashboard_perferences);
		displayUpdateTimestamp();
		findPreference("prefkeyUpdateNow").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				findPreference("prefKeyLastUpdate").setSummary(R.string.msg_updating);
				AsyncDataLoader.loadData(DashboardPreferenceActivity.this);
				return true;
			}
		});
		
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void loadingFinished() {
		displayUpdateTimestamp();
	}

	private void displayUpdateTimestamp() {
		long updateTime = Settings.getInstance().getUpdateTime();
		if (updateTime > 0) {
			String date = Settings.dateTimeFormat.format(updateTime);
			findPreference("prefKeyLastUpdate").setSummary(date);
		}
	}

}
