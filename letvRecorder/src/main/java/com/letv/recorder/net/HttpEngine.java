package com.letv.recorder.net;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class HttpEngine {
	private String TAG = "HttpEngine";
	private Context mContext;

	private static HttpEngine sInstance;
	private static RequestQueue sRequestQueue;

	private HttpEngine(Context context) {
		mContext = context;
		sRequestQueue = Volley.newRequestQueue(mContext);
	}

	public static HttpEngine getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new HttpEngine(context);
		}
		return sInstance;
	}

	public RequestQueue getRequestQueue() {
		return sRequestQueue;
	}

	public String getCacheData(String url) {
		try {
			Cache cache = sRequestQueue.getCache();
			if (cache != null) {
				Entry entry = cache.get(url);
				if (entry != null) {
					return new String(entry.data, "UTF-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addRequest(Request<?> request) {
		if (request != null) {
			request.setTag(mContext);
			sRequestQueue.add(request);
		}
	}

	public void cancelAll() {
		sRequestQueue.cancelAll(mContext);
	}

	public ResponseData<JSONObject> sendJsonRequest(int method, String requestUrl, RequestParam param) {
		try {
			JSONObject jsonObj = null;
			if (method == Request.Method.GET) {
				requestUrl = requestUrl + param.getQueryStringParameter();
			} else {
				Gson gson = new Gson();
				String json = gson.toJson(param.getPostBodyParameter());
				jsonObj = new JSONObject(json);
			}
			return sendJsonRequest(method, requestUrl, jsonObj, param.getHeaders());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResponseData<JSONObject> sendJsonRequest(int method, String url, JSONObject jsonObj, Map<String, String> headers)
			throws InterruptedException, ExecutionException {
		SynchronizationRequestFuture<JSONObject> future = SynchronizationRequestFuture.newFuture();
		CBJsonobjectRequest request = new CBJsonobjectRequest(method, url, jsonObj, future, future);
		request.setHeaders(headers);
		addRequest(request);
		ResponseData<JSONObject> response = future.get();
		return response;
	}

	public void sendAsynJsonRequest(int method, String requestUrl, RequestParam params, final HttpEngineCallback cb) {
		try {
			JSONObject jsonObj = null;
			if (method == Request.Method.GET) {
				requestUrl = requestUrl + params.getQueryStringParameter();
			} else {
				Gson gson = new Gson();
				String json = gson.toJson(params.getPostBodyParameter());
				jsonObj = new JSONObject(json);
			}
			sendAsynJsonRequest(method, requestUrl, jsonObj, params.getHeaders(), cb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAsynJsonRequest(int method, String url, JSONObject jsonObj, Map<String, String> headers, final HttpEngineCallback cb) {
		CBJsonobjectRequest request = new CBJsonobjectRequest(method, url, jsonObj, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				if (cb != null) {
					ResponseData<JSONObject> data = new ResponseData<JSONObject>();
					data.data = json;
					data.httpCode = 200;
					cb.onSuccess(data);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				if (cb != null) {
					ResponseData<JSONObject> data = new ResponseData<JSONObject>();
					if (volleyError.networkResponse != null) {
						data.httpCode = volleyError.networkResponse.statusCode;
					}
					data.volleyError = volleyError;
					cb.onFailed(data);
				}
			}
		});
		request.setHeaders(headers);
		addRequest(request);
	}

	public ResponseData<String> sendStringRequest(int method, String requestUrl, RequestParam param) {
		try {
			
			if (method == Request.Method.GET && param != null) {
				requestUrl = requestUrl + param.getQueryStringParameter();
			}
			Log.i(TAG, "reqeustUrl:" + requestUrl);
			Map<String, String> postParam = null;
			Map<String, String> header = null;
			if (param != null) {
				postParam = param.getPostBodyParameter();
				header = param.getHeaders();
//				Logger.e(TAG, "sendStringRequest:" + headers);
			}
//			for(int i=0;i<header.size();i++){
//			}
			
			return sendStringRequest(method, requestUrl, postParam, header);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResponseData<String> sendStringRequest(int method, String url, Map<String, String> postParams, Map<String, String> headers)
			throws InterruptedException, ExecutionException {
		SynchronizationRequestFuture<String> future = SynchronizationRequestFuture.newFuture();
		CBStringRequest request = new CBStringRequest(method, url, future, future);
		request.setHeaders(headers);
//		Logger.e(TAG, "setHeaders:" + headers);
		request.setPostParams(postParams);
		addRequest(request);
		ResponseData<String> response = future.get();
		return response;
	}

	public void sendAsynStringRequest(int method, String requestUrl, RequestParam params, final HttpEngineCallback cb) {
		try {
			// JSONObject jsonObj = null;
			if (method == Request.Method.GET && params != null) {
				requestUrl = requestUrl + params.getQueryStringParameter();
			}
			Map<String, String> postParam = null;
			Map<String, String> header = null;
			if (params != null) {
				postParam = params.getPostBodyParameter();
				header = params.getHeaders();
			}
			sendAsynStringRequest(method, requestUrl, postParam, header, cb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAsynStringRequest(int method, String url, Map<String, String> postParams, Map<String, String> headers, final HttpEngineCallback cb) {
		CBStringRequest request = new CBStringRequest(method, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if (cb != null) {
					ResponseData<String> data = new ResponseData<String>();
					data.data = response;
					data.httpCode = 200;
					cb.onSuccess(data);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				if (cb != null) {
					ResponseData<String> data = new ResponseData<String>();
					if (volleyError.networkResponse != null) {
						data.httpCode = volleyError.networkResponse.statusCode;
					}
					data.volleyError = volleyError;
					cb.onFailed(data);
				}
			}
		});
		request.setHeaders(headers);
		request.setPostParams(postParams);
		addRequest(request);
	}

	public class CBJsonobjectRequest extends JsonObjectRequest {
		private Map<String, String> mHeaders;

		public CBJsonobjectRequest(int method, String url, JSONObject jsonRequest, Listener<JSONObject> listener, ErrorListener errorListener) {
			super(method, url, jsonRequest, listener, errorListener);
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			if (mHeaders != null) {
				return mHeaders;
			}
			return super.getHeaders();
		}

		public void setHeaders(Map<String, String> headers) {
			mHeaders = headers;
		}
	}

	public class CBStringRequest extends StringRequest {
		private Map<String, String> mHeaders;
		private Map<String, String> mParams;

		public CBStringRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
			super(method, url, listener, errorListener);
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
//			Logger.e(TAG, "getHeaders:" + mHeaders);
			if (mHeaders != null) {
				return mHeaders;
			}
			return super.getHeaders();
		}

		public void setHeaders(Map<String, String> headers) {
			mHeaders = headers;
		}

		public void setPostParams(Map<String, String> params) {
			mParams = params;
		}

		@Override
		protected Map<String, String> getParams() throws AuthFailureError {
			if (mParams != null) {
				return mParams;
			}
			return super.getParams();
		}
		
	}

	public interface HttpEngineCallback<T> {
		public void onSuccess(ResponseData<T> data);

		public void onFailed(ResponseData<T> data);
	}

}
