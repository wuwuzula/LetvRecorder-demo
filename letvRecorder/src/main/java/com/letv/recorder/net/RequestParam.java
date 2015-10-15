package com.letv.recorder.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class RequestParam {

	private String charset = HTTP.UTF_8;

	private Map<String, String> headers;
	private List<NameValuePair> queryStringParams;
	private Map<String, String> bodyParams;

	public void addHeader(String name, String value) {
		if (this.headers == null) {
			this.headers = new HashMap<String, String>();
		}
		this.headers.put(name, value);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void addQueryStringParameter(String name, String value) {
		if (queryStringParams == null) {
			queryStringParams = new ArrayList<NameValuePair>();
		}
		queryStringParams.add(new BasicNameValuePair(name, value));
	}

	public void addBodyParameter(String name, String value) {
		if (bodyParams == null) {
			bodyParams = new HashMap<String, String>();
		}
		bodyParams.put(name, value);
	}

	public String getQueryStringParameter() {
		if (queryStringParams == null || queryStringParams.size() == 0) {
			return "";
		}
		final StringBuilder result = new StringBuilder();
		for (final NameValuePair parameter : queryStringParams) {
			final String encodedName = parameter.getName();
			final String encodedValue = parameter.getValue();
			if (result.length() > 0) {
				result.append("&");
			}
			result.append(encodedName);
			if (encodedValue != null) {
				result.append("=");
				result.append(encodedValue);
			}
		}
		return "?" + result.toString();
	}

	public Map<String, String> getPostBodyParameter() {
		return bodyParams;
	}
}
