package ch.unibas.urz.android.dashboard.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import ch.unibas.urz.android.dashboard.access.JsonLoader;
import ch.unibas.urz.android.dashboard.provider.db.DB;

public class AsyncDataLoader extends AsyncTask<Object, Object, Object> {

	private final LoaderCallback loaderCallback;

	public interface LoaderCallback {
		public Context getContext();

		public void loadingFinished();
	}

	public AsyncDataLoader(LoaderCallback loaderCallback) {
		super();
		this.loaderCallback = loaderCallback;
	}

	@Override
	protected Object doInBackground(Object... params) {
		Context ctx = loaderCallback.getContext();
		JsonLoader.loadApps(ctx);
		Cursor c = null;
		try {
			c = ctx.getContentResolver().query(DB.DashboardApp.CONTENT_URI, DB.DashboardApp.PROJECTION_DEFAULT, null, null, DB.DashboardApp.SORTORDER_DEFAULT);
			while (c.moveToNext()) {
				ImageCachedLoader.getImageBitmapFromNetwork(ctx, c.getString(DB.DashboardApp.INDEX_ICON));
			}
		} finally {
			if (c != null && !c.isClosed()) {
				try {
					c.close();
				} catch (Exception e) {
				}
			}
			Settings.getInstance().setUpdateTimeNow();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		loaderCallback.loadingFinished();
		super.onPostExecute(result);
	}

	public static void loadData(LoaderCallback cb) {
		AsyncDataLoader adl = new AsyncDataLoader(cb);
		adl.execute(cb);
	}

}
