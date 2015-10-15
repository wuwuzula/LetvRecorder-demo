package com.letv.recorder.net;
@Deprecated
public interface HttpRequestCallback {
	
	public void onSuccess(Object[] mOutputData);
	
	public void onFailed(int errorCode, String msg);
	
	 
}
