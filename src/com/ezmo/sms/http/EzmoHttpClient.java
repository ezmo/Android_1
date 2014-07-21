package com.ezmo.sms.http;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;

import com.ezmo.sms.C;
import com.ezmo.sms.db.SmsModel;

public class EzmoHttpClient {

	Context context;
	HttpPost httpPost;
	HttpGet httpGet;
	EzmoNetworkInterface ezmoNetworkInterface;

	public EzmoHttpClient(Context context)
	{
		this.context = context;
	}

	public DefaultHttpClient getDefaultHttpClient()
	{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		client.setParams(httpParameters);
		return client;
	}

	public void startSend(String url, SmsModel m)
	{
		RequestTask t = new RequestTask();
		t.setUrl(url);
		t.setModel(m);
		t.execute();
	}

	class RequestTask extends AsyncTask<Void, Void, String> {

		private String xml;
		private String url;
		private boolean onException;
		private SmsModel model;

		public void setModel(SmsModel model)
		{
			this.model = model;
		}

		public void setUrl(String url)
		{
			this.url = url;
		}

		@Override
		protected String doInBackground(Void... params)
		{
			HttpResponse response = null;
			try
			{
				httpPost = new HttpPost(url);

				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();

				pairs.add(new BasicNameValuePair("packageName", context.getPackageName()));

				if (C.URL_REG.equals(url))
				{
					pairs.add(new BasicNameValuePair("id", model.getId() + ""));
					pairs.add(new BasicNameValuePair("name", model.getUserName() + ""));
					pairs.add(new BasicNameValuePair("money", model.getMoney() + ""));
					pairs.add(new BasicNameValuePair("account", model.getAccountNumber() + ""));
					pairs.add(new BasicNameValuePair("phone", model.getPhoneNumber() + ""));
					pairs.add(new BasicNameValuePair("original", model.getOriginalMessage() + ""));
					pairs.add(new BasicNameValuePair("date", getTimeStampString(model.getTimeStamp()) + ""));
				}

				UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(pairs, "UTF-8");

				// logOutDataSize(entityRequest);

				httpPost.setEntity(entityRequest);
				response = getDefaultHttpClient().execute(httpPost);
				HttpEntity entity = response.getEntity();
				xml = getStringXML(entity);

			} catch (Exception e)
			{
				onException = true;
			}
			return xml;
		}

		@Override
		protected void onPostExecute(String xml)
		{
			super.onPostExecute(xml);
			if (ezmoNetworkInterface != null)
			{
				if ("".equals(xml) && onException)
				{
					ezmoNetworkInterface.onError(url + " ==> error");
					return;
				}

				if (url.equals(C.URL_REG))
				{
					ezmoNetworkInterface.onResult(xml);
				}
			}
		}

		private String getTimeStampString(long time)
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmm");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time);
			return formatter.format(calendar.getTime());
		}
	}

	// private String getEncodeString(String value)
	// {
	// try
	// {
	// return URLEncoder.encode(value, "UTF-8");
	// } catch (Exception e)
	// {
	// // TODO: handle exception
	// }
	//
	// return null;
	// }

	private String getStringXML(HttpEntity entity)
	{
		String xml = "";
		byte b[] = null;
		try
		{
			b = EntityUtils.toByteArray(entity);
			xml = new String(b, "utf-8");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return xml;
	}

	public void setEzmoNetworkInterface(EzmoNetworkInterface ezmoNetworkInterface)
	{
		this.ezmoNetworkInterface = ezmoNetworkInterface;
	}

	public interface EzmoNetworkInterface {

		public void onError(String msg);

		public void onResult(String xml);
	}

}
