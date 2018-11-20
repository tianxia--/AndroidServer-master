package com.cpf.testclient;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class MyApplication extends android.app.Application {

	private final String TAG = this.getClass().toString();

	public static String mIp = "192.168.0.104";



	@Override
	public void onCreate() {
		super.onCreate();
		initializeOkHttp();
	}


	/**
	 * 初始化okttp请求网络框架
	 */
	private void initializeOkHttp(){
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(new LoggerInterceptor("TAG"))
				.connectTimeout( 10000L, TimeUnit.MILLISECONDS )
				.readTimeout( 10000L, TimeUnit.MILLISECONDS )
				//其他配置
				.build();
		OkHttpUtils.initClient( okHttpClient );
	}
}
