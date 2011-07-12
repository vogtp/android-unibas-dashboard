package ch.unibas.urz.android.dashboard.view.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ch.unibas.urz.android.dashboard.R;
import ch.unibas.urz.android.dashboard.model.AppModel;

public class AppWidget extends ViewGroup {

	private AppModel appModel;
	private final ImageView image;

	public AppWidget(Context context) {
		super(context);
		int p = getResources().getDimensionPixelSize(R.dimen.dim_image_button_padding);
		setPadding(p, p, p, p);
		image = new ImageView(context);
		image.setMaxHeight(android.R.dimen.thumbnail_height);
		image.setMaxWidth(android.R.dimen.thumbnail_width);
	}

	public AppWidget(Context context, AppModel appModel) {
		this(context);
		this.appModel = appModel;
		image.setImageDrawable(getResources().getDrawable(R.drawable.unibasel_with_bg));
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startApp();
			}

		});
		addView(image);
	}

	private void startApp() {
		Intent intent = null;
		if (appModel.hasPackage()) {
			intent = getContext().getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
		}
		if (intent == null && appModel.hasUrl()) {
			// launch url
			intent = new Intent("android.intent.action.VIEW", Uri.parse(appModel.getUrl()));
		}
		if (intent != null) {
			getContext().startActivity(intent);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cc = getChildCount();
		for (int i = 0; i < cc; i++) {
			getChildAt(i).layout(l, t, r, b);
		}
	}

}
