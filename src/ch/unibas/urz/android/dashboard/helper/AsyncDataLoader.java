package ch.unibas.urz.android.dashboard.helper;

import android.database.Cursor;
import android.os.AsyncTask;
import ch.unibas.urz.android.dashboard.access.JsonLoader;
import ch.unibas.urz.android.dashboard.provider.db.DB;
import ch.unibas.urz.android.dashboard.view.activity.UnibasDashboardActivity;

public class AsyncDataLoader extends AsyncTask<Object, Object, Object> {

	private final UnibasDashboardActivity dashboardActivity;

	public AsyncDataLoader(UnibasDashboardActivity dashboardActivity) {
		super();
		this.dashboardActivity = dashboardActivity;
	}

	@Override
	protected Object doInBackground(Object... params) {

		JsonLoader.loadApps(dashboardActivity);
		Cursor c = null;
		try {
			c = dashboardActivity.managedQuery(DB.DashboardApp.CONTENT_URI, DB.DashboardApp.PROJECTION_DEFAULT, null, null, DB.DashboardApp.SORTORDER_DEFAULT);
			while (c.moveToNext()) {
				ImageCachedLoader.getImageBitmapFromNetwork(dashboardActivity, c.getString(DB.DashboardApp.INDEX_ICON));

			}
		} finally {
			if (c != null && !c.isClosed()) {
				try {
					c.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		dashboardActivity.loadingFinished();
		super.onPostExecute(result);
	}

}
