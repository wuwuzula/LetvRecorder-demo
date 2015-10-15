package com.letv.recorder.net;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class SynchronizationRequestFuture<T> implements Future, Response.Listener<T>, Response.ErrorListener {

	private Request<?> mRequest;
	private boolean mResultReceived = false;
	private T mResult;
	private VolleyError mException;
	private ResponseData<T> mResponseData;

	public static <E> SynchronizationRequestFuture<E> newFuture() {
		return new SynchronizationRequestFuture<E>();
	}

	private SynchronizationRequestFuture() {
		mResponseData = new ResponseData<T>();
	}

	public void setRequest(Request<?> request) {
		mRequest = request;
	}

	@Override
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {
		if (mRequest == null) {
			return false;
		}

		if (!isDone()) {
			mRequest.cancel();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ResponseData<T> get() throws InterruptedException, ExecutionException {
		try {
			return doGet(null);
		} catch (TimeoutException e) {
			
			throw new AssertionError(e);
		}
	}

	@Override
	public ResponseData<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return doGet(TimeUnit.MILLISECONDS.convert(timeout, unit));
	}

	private synchronized ResponseData<T> doGet(Long timeoutMs) throws InterruptedException, ExecutionException, TimeoutException {
		
		
		if (mException != null) {
			
			// throw new ExecutionException(mException);
			mResponseData.volleyError = mException;
			mResponseData.httpCode = mException.networkResponse.statusCode;
			return mResponseData;
		}

		if (mResultReceived) {
			mResponseData.httpCode = 200;
			mResponseData.data = mResult;
			return mResponseData;
		}
		if (timeoutMs == null) {
			wait(0);
		} else if (timeoutMs > 0) {
			wait(timeoutMs);
		}
		if (mException != null) {
			// throw new ExecutionException(mException);
			mResponseData.volleyError = mException;
			if(mException.networkResponse != null){
				mResponseData.httpCode = mException.networkResponse.statusCode;
			}else{
				mResponseData.httpCode = -1;
			}
			return mResponseData;
		}

		if (!mResultReceived) {
			throw new TimeoutException();
		}
		mResponseData.httpCode = 200;
		
		mResponseData.data = mResult;
		return mResponseData;
	}

	@Override
	public boolean isCancelled() {
		if (mRequest == null) {
			return false;
		}
		return mRequest.isCanceled();
	}

	@Override
	public synchronized boolean isDone() {
		return mResultReceived || mException != null || isCancelled();
	}

	@Override
	public synchronized void onResponse(T response) {
		mResultReceived = true;
		mResult = response;
		notifyAll();
	}

	@Override
	public synchronized void onErrorResponse(VolleyError error) {
		mException = error;
		notifyAll();
	}

}
