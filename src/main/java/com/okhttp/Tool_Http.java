package com.okhttp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Tool_Http {
	
	public static final int timeout = 30;

	public static final MediaType JSONMediaType = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType FORMMEDIATYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
	
	private OkHttpClient client = new OkHttpClient();
	
	public Tool_Http() {
		client.setConnectTimeout(timeout, TimeUnit.SECONDS);
	}

	public String do_get(String url) throws IOException {
		return do_get(url, null);
	}
	
	public String do_get(String url, Map<String, String> headers) throws IOException {
		Builder builder = new Request.Builder().url(url);
		if (headers != null && headers.size() > 0) {
			headers.forEach((name, value) -> {
				builder.addHeader(name, value);
			});
		}
		Request request = builder.url(url).build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new IOException("服务器端错误: " + response);
		}

		return response.body().string();
	}
	
	public String do_post(String url, Map<String, String> params) throws IOException {
		return do_post(url, params, null);
	}
	
	public String do_post(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
		FormEncodingBuilder formBuilder = new FormEncodingBuilder();
		if (params != null && params.size() > 0) {
			params.forEach((name, value) -> {
				formBuilder.add(name, value);
			});
		}
		
		Builder builder = new Request.Builder().url(url);
		if (headers != null && headers.size() > 0) {
			headers.forEach((name, value) -> {
				builder.addHeader(name, value);
			});
		}
		
		RequestBody formBody = formBuilder.build();
		Request request = builder.url(url).post(formBody).build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new IOException("服务器端错误: " + response);
		}

		return response.body().string();
	}
	
	public static void main(String[] args) throws Exception {
		Tool_Http http = new Tool_Http();
		//System.out.println(http.do_get("http://www.open-open.com/lib/view/open1434375729020.html"));
		System.out.println(http.do_post("https://www.baidu.com", null));
	}
	
}
