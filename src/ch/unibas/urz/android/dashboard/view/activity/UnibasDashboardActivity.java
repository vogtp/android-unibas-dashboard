package ch.unibas.urz.android.dashboard.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import ch.unibas.urz.android.dashboard.R;
import ch.unibas.urz.android.dashboard.helper.AsyncDataLoader;
import ch.unibas.urz.android.dashboard.helper.AsyncDataLoader.LoaderCallback;
import ch.unibas.urz.android.dashboard.helper.ImageCachedLoader;
import ch.unibas.urz.android.dashboard.helper.Logger;
import ch.unibas.urz.android.dashboard.helper.Settings;
import ch.unibas.urz.android.dashboard.model.AppModel;
import ch.unibas.urz.android.dashboard.provider.db.DB;
import ch.unibas.urz.android.dashboard.provider.db.DB.DashboardApp;
import ch.unibas.urz.android.dashboard.view.preferences.DashboardPreferenceActivity;

public class UnibasDashboardActivity extends Activity implements LoaderCallback {
	private AbsListView appsList;
	private Cursor appsCursor;
	private boolean showHidden = false;
	private int currentAppListStyle = -1;
	private int currentAppAppearance = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Settings.getInstance().isUpdateNeeded()) {
			AsyncDataLoader.loadData(this);
		}

		initList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initList();
	}

	private void initList() {
		int appListStyle = Settings.getInstance().getAppListStyle();
		int appAppearance = Settings.getInstance().getAppAppearance();
		if (currentAppListStyle == appListStyle && currentAppAppearance == appAppearance) {
			return;
		}
		if (appAppearance == Settings.APP_APPEARIANCE_ANDROID) {
			setTheme(android.R.style.Theme_Black);
			setContentView(R.layout.main_android);
		} else {
			setTheme(android.R.style.Theme_NoTitleBar);
			setContentView(R.layout.main_unibas);
		}
		LinearLayout llAppsAncor = (LinearLayout) findViewById(R.id.llAppsAncor);
		ListView lvApps = (ListView) findViewById(R.id.lvApps);
		GridView gvApps = (GridView) findViewById(R.id.gvApps);
		if (appsCursor != null && !appsCursor.isClosed()) {
			appsCursor.close();
		}
		if (appListStyle == Settings.APP_LIST_GRID) {
			llAppsAncor.removeView(lvApps);
			appsList = gvApps;
		} else {
			llAppsAncor.removeView(gvApps);
			appsList = lvApps;
		}
		currentAppListStyle = appListStyle;
		currentAppAppearance = appAppearance;
		queryDatabase();
	}

	private void queryDatabase() {
		String selection = null;
		if (!showHidden) {
			selection = DB.DashboardApp.NAME_HIDE + "=0";
		}
		appsCursor = managedQuery(DB.DashboardApp.CONTENT_URI, DB.DashboardApp.PROJECTION_DEFAULT, selection, null, DB.DashboardApp.SORTORDER_DEFAULT);

		String[] from = new String[] { DB.DashboardApp.NAME_APPNAME, DB.DashboardApp.NAME_ICON };
		int[] to = new int[] { R.id.tvAppName, R.id.ivAppIcon };
		int res = R.layout.app_grid;
		if (currentAppListStyle == Settings.APP_LIST_LIST) {
			res = R.layout.app_list;
			from = new String[] { DB.DashboardApp.NAME_APPNAME, DB.DashboardApp.NAME_ICON, DB.DashboardApp.NAME_DESCRIPTION };
			to = new int[] { R.id.tvAppName, R.id.ivAppIcon, R.id.tvDescription };

		}
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, res, appsCursor, from, to);
		appsList.setAdapter(adapter);
		appsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppModel am = getAppModelFromId(id);
				if (am != null) {
					startApp(am);
				}
			}
		});

		appsList.setOnCreateContextMenuListener(this);

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				final AppModel am = new AppModel(cursor);

				view.setContentDescription(am.getDescription());

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
				if (DB.DashboardApp.INDEX_DESCRIPTION == columnIndex) {
					((TextView) view).setText(am.getDescription());
				}

				return true;
			}
		});
	}

	private void startApp(final AppModel appModel) {
		if (!startNativeApp(appModel)) {
			if (!startWebApp(appModel)) {
				Toast.makeText(this, R.string.msg_unable_to_start, Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean startWebApp(AppModel appModel) {
		if (!appModel.hasUrl()) {
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
		if (!appModel.hasPackage()) {
			return false;
		}
		Intent intent = getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
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
			return true;
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

	@Override
	public void loadingFinished() {
		appsCursor.requery();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.general_option_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int res = R.string.itemHideOn;
		if (showHidden) {
			res = R.string.itemHideOff;
		}
		menu.findItem(R.id.itemToggleHide).setTitle(res);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPreferences:
			startActivity(new Intent(this, DashboardPreferenceActivity.class));
			return true;
		case R.id.itemToggleHide:
			showHidden = !showHidden;
			queryDatabase();
			return true;
		default:
			return false;
		}
	}

	@Override
	public Context getContext() {
		return this;
	}

	private AppModel getAppModelFromId(long id) {
		Cursor c = managedQuery(DB.DashboardApp.CONTENT_URI, DashboardApp.PROJECTION_DEFAULT, DB.SELECTION_BY_ID, new String[] { "" + id }, DashboardApp.SORTORDER_DEFAULT);
		if (c.moveToFirst()) {
			return new AppModel(c);
		}
		return null;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item instanceof MenuItem) {
			AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			AppModel am = getAppModelFromId(menuInfo.id);
			if (am == null) {
				return false;
			}
			switch (item.getItemId()) {
			case R.id.itemLaunchApp:
				startNativeApp(am);
				break;
			case R.id.itemGotoWeb:
				startWebApp(am);
				break;
			case R.id.itemHideApp:
				hideApp(am);
				break;
			}
			return true;
		}
		return false;

	}

	private void hideApp(AppModel am) {
		am.setHide(!am.isHide());
		getContentResolver().update(DB.DashboardApp.CONTENT_URI, am.getValues(), DB.SELECTION_BY_ID, new String[] { "" + am.getDbid() });
		// appsCursor.requery();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.app_context, menu);
		AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;
		AppModel am = getAppModelFromId(mi.id);
		menu.getItem(0).setVisible(am.hasPackage());
		menu.getItem(1).setVisible(am.hasUrl());
		int res = R.string.hide;
		if (am.isHide()) {
			res = R.string.show;
		}
		menu.getItem(2).setTitle(res);
	}

}