package com.letv.recorder.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

/**
 * 图片缓存
 * @author wangqiangqiang
 *
 */
public class LruImageCache implements ImageCache {
	private static LruCache<String, Bitmap> mMemoryCache;
	private static LruImageCache lruImageCache;
	private static ImageLoader imageLoader;


	private LruImageCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	public static LruImageCache instance(Context context) {
		if (lruImageCache == null) {
			lruImageCache = new LruImageCache();
		}
		
		return lruImageCache;
	}
	public static ImageLoader getImageLoader(Context context){
		if(null == imageLoader ){
			RequestQueue queue = Volley.newRequestQueue(context);
			imageLoader = new ImageLoader(queue,instance(context));
		}
		return imageLoader;
	}
	
	@Override
	public Bitmap getBitmap(String arg0) {
		return mMemoryCache.get(arg0);
	}

	@Override
	public void putBitmap(String arg0, Bitmap arg1) {
		if (getBitmap(arg0) == null) {
			mMemoryCache.put(arg0, arg1);
		}
	}

}