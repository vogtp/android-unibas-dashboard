package ch.unibas.urz.android.dashboard.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import ch.unibas.urz.android.dashboard.R;
import ch.unibas.urz.android.dashboard.helper.AsyncDataLoader;
import ch.unibas.urz.android.dashboard.helper.ImageCachedLoader;
import ch.unibas.urz.android.dashboard.helper.Logger;
import ch.unibas.urz.android.dashboard.model.AppModel;
import ch.unibas.urz.android.dashboard.provider.db.DB;

public class UnibasDashboardActivity extends Activity {
	private GridView gvApps;
	// private ProgressDialog progressDialog;
	private Cursor appsCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initaliseData();
		setTheme(android.R.style.Theme_NoTitleBar);
		setContentView(R.layout.main);

		gvApps = (GridView) findViewById(R.id.gvApps);

		appsCursor = managedQuery(DB.DashboardApp.CONTENT_URI, DB.DashboardApp.PROJECTION_DEFAULT, null, null, DB.DashboardApp.SORTORDER_DEFAULT);

		String[] from = new String[] { DB.DashboardApp.NAME_APPNAME, DB.DashboardApp.NAME_ICON };
		int[] to = new int[] { R.id.tvAppName, R.id.ivAppIcon };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.app, appsCursor, from, to);
		gvApps.setAdapter(adapter);
		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				final AppModel am = new AppModel(cursor);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startApp(am);
					}
				});

				if (DB.DashboardApp.INDEX_ICON == columnIndex) {
					ImageView image = (ImageView) view;
					// if (am.getName().toLowerCase().contains("perssearch")) {
					// image.setImageResource(R.drawable.perssearch2);
					// } else if
					// (am.getName().toLowerCase().contains("flexiform")) {
					// image.setImageResource(R.drawable.flexiform2);
					// } else {
					Bitmap bitmap = ImageCachedLoader.getImageBitmapFromCache(UnibasDashboardActivity.this, am.getIcon());
					if (bitmap != null) {
						image.setImageBitmap(bitmap);
					} else {
						image.setImageResource(R.drawable.unibasel_with_bg);
					}
					// }
				} else if (DB.DashboardApp.INDEX_APPNAME == columnIndex) {
					((TextView) view).setText(am.getName());
				}

				return true;
			}
		});
	}

	private void initaliseData() {
		// FIXME check for last check
		AsyncDataLoader adl = new AsyncDataLoader(this);
		adl.execute(this);
	}

	private void startApp(final AppModel appModel) {
		if (!startNativeApp(appModel)) {
			if (!startWebApp(appModel)) {
				Toast.makeText(this, R.string.msg_unable_to_start, Toast.LENGTH_LONG).show();
			}
		}

	}

	private boolean startWebApp(AppModel appModel) {
		if (appModel.hasUrl()) {
			return false;
		}
		Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(appModel.getUrl()));
		if (intent != null) {
			try {
				startActivity(intent);
				return true;
			} catch (Exception e) {
				Logger.e("Cannot launch webpage for " + appModel.getName(), e);
			}
		}
		return false;
	}

	private boolean startNativeApp(final AppModel appModel) {
		Intent intent = null;
		if (!appModel.hasPackage()) {
			return false;
		}
		intent = getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
		if (intent == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.title_install_native_app);
			builder.setMessage(R.string.msg_install_or_use_web);
			builder.setPositiveButton("Install app", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					maketInstall(appModel);
				}
			});
			builder.setNegativeButton("Goto website", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startWebApp(appModel);

				}
			});
			builder.create().show();
		}
		if (intent != null) {
			try {
				startActivity(intent);
				return true;
			} catch (Exception e) {
				Logger.e("Cannot start  native app " + appModel.getName(), e);
			}
		}
		return false;
	}

	private boolean maketInstall(AppModel appModel) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:" + appModel.getPackageName()));
		try {
			startActivity(intent);
			return true;
		} catch (Exception e) {
			Logger.e("Cannot start  native app " + appModel.getName(), e);
		}
		return false;
	}

	public void loadingFinished() {
		appsCursor.requery();
	}
}