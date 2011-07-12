package ch.unibas.urz.android.dashboard.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;

public class ImageCachedLoader {

	private static final String BASE_URL_ICON = "http://nikt.unibas.ch/mobileapp/resources/images/";
	private static final Config BITMAP_CONFIG = Bitmap.Config.RGB_565;
	private static final int MIN_IMAGE_DIM = 20;

	private static String buildFilePath(Context context, String iconName) {
		String extStorage = context.getCacheDir().getAbsolutePath();
		String path = extStorage + "/icons/" + iconName;
		Logger.v("Got image path " + path);
		return path;
	}

	private static String buildImageUrl(String iconName) throws MalformedURLException {
		String url = BASE_URL_ICON + iconName;
		Logger.d("Got image url " + url);
		return url;
	}

	/**
	 * 
	 * @param context
	 * @param iconName
	 * @return the bitmap of the image or null if not found
	 */
	public static Bitmap getImageBitmapFromCache(Context context, String iconName) {
		String imagePath = buildFilePath(context, iconName);
		Bitmap bm = loadFromFile(imagePath);
		int width = -1;
		int height = -1;
		if (bm != null) {
			width = bm.getWidth();
			height = bm.getHeight();
			Logger.v("Got bitmap " + imagePath + " with " + width + " height " + height);
		}
		if (width < MIN_IMAGE_DIM && height < MIN_IMAGE_DIM) {
			bm = null;
		}
		return bm;
	}

	public static Bitmap getImageBitmapFromNetwork(Context context, String iconName) {
		String imagePath = buildFilePath(context, iconName);
		Bitmap bm = loadFromFile(imagePath);
		int width = -1;
		int height = -1;
		if (bm != null) {
			width = bm.getWidth();
			height = bm.getHeight();
			Logger.v("Got bitmap " + imagePath + " with " + width + " height " + height);
		}
		if (width < MIN_IMAGE_DIM && height < MIN_IMAGE_DIM) {
			try {
				bm = loadImage(buildImageUrl(iconName));
			} catch (MalformedURLException e) {
				Logger.e("Cannot parse url for rainspot", e);
			}
			if (bm != null) {
				width = bm.getWidth();
				height = bm.getHeight();
				if (height > 1 && width > 1) {
					writeBitmapToFile(imagePath, bm);
				}
			}
		}
		return bm;
	}

	private static Bitmap loadFromFile(String imagePath) {
		try {
			return loadFromFile(new FileInputStream(imagePath));
		} catch (FileNotFoundException e) {
			Logger.d("Cache file not found, going to download");
			return null;
		}
	}

	private static Bitmap loadFromFile(InputStream is) {
		Bitmap bm = null;
		try {
			// FileInputStream fis = new FileInputStream(imagePath);
			bm = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			Logger.d("Error loading image from cache", e);
		}
		return bm;
	}

	private static void writeBitmapToFile(String imagePath, Bitmap bm) {
		if (bm == null) {
			return;
		}
		BufferedOutputStream bos = null;
		File path = new File(imagePath);
		if (!checkOrCreateDirectory(path.getParentFile())) {
			return;
		}
		try {
			bos = new BufferedOutputStream(new FileOutputStream(imagePath));
			bm.compress(CompressFormat.PNG, 0, bos);

		} catch (IOException e) {
			Logger.e("Error writing bitmap to file", e);
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					Logger.e("Error closing output stream", e);
				}
			}
		}

	}

	private static boolean checkOrCreateDirectory(File parentFile) {
		if (parentFile == null) {
			return false;
		}
		if (parentFile.isDirectory()) {
			return true;
		}
		if (!parentFile.mkdir()) {
			if (checkOrCreateDirectory(parentFile.getParentFile())) {
				return parentFile.mkdir();
			}
		}
		return false;
	}

	private static Bitmap loadImage(String imageUrl) {
		Bitmap bm = null;
		try {
			URL aUrl = new URL(imageUrl);
			URLConnection conn = aUrl.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (Exception e) {
			Logger.e("Error getting bitmap", e);
		}
		if (bm == null) {
			bm = createEmptyBitmap();
		}
		return bm;
	}

	private static Bitmap createEmptyBitmap() {
		int[] colors = new int[1];
		colors[0] = Color.BLACK;
		return Bitmap.createBitmap(colors, 1, 1, BITMAP_CONFIG);
	}


	private static Bitmap scaleImage(Activity a, Bitmap bm) {
		DisplayMetrics metrics = new DisplayMetrics();
		a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float w = bm.getScaledWidth(metrics) * metrics.density;
		float h = bm.getScaledHeight(metrics) * metrics.density;
		return Bitmap.createScaledBitmap(bm, (int) w, (int) h, false);
	}
}
