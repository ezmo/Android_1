package com.ezmo.sms.task;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.ezmo.sms.db.SmsDBHelper;
import com.ezmo.sms.db.SmsModel;
import com.ezmo.sms.model.DataModel;

public class EzmoDatabaseClient {

	private Context context;
	private EzmoDatabaseInterface ezmoDatabaseInterface;

	public static final String SELECT_ALL = "select_all";

	public EzmoDatabaseClient(Context context, EzmoDatabaseInterface ezmoDatabaseInterface)
	{
		this.context = context;
		this.ezmoDatabaseInterface = ezmoDatabaseInterface;
	}

	public void start(String workName)
	{
		DbTask task = null;
		if (SELECT_ALL.equals(workName))
		{
			task = new DbTask();
			task.execute();
		}
	}

	public class DbTask extends AsyncTask<Void, Integer, DataModel> {

		private SmsDBHelper helper;
		private boolean onError;

		@Override
		protected DataModel doInBackground(Void... params)
		{
			try
			{
				helper = new SmsDBHelper(context);
				DataModel dm = new DataModel();
				if (helper != null)
				{
					ArrayList<SmsModel> list = helper.getDAO().selectAll();
					ArrayList<ArrayList<SmsModel>> children = new ArrayList<ArrayList<SmsModel>>();
					if (list != null)
					{
						for (int i = 0; i < list.size(); i++)
						{
							ArrayList<SmsModel> chRoot = new ArrayList<SmsModel>();
							chRoot.add(list.get(i));
							children.add(chRoot);
						}
					}
					if (list != null && children != null)
					{
						dm.setSmsList(list);
						dm.setChildren(children);
						Thread.sleep(700);
						return dm;

					}
				}
			} catch (Exception e)
			{
				onError = true;
			} finally
			{
				databaseClose();
			}

			return null;
		}

		public void databaseClose()
		{
			if (helper != null)
			{
				helper.dbClose();
				helper.helperClose();
				helper = null;
			}
		}

		@Override
		protected void onPostExecute(DataModel result)
		{
			super.onPostExecute(result);

			if (!onError)
			{
				if (ezmoDatabaseInterface != null)
				{
					ezmoDatabaseInterface.onResult(result);
				}
			} else
			{
				if (ezmoDatabaseInterface != null)
				{
					ezmoDatabaseInterface.onError();
				}
			}

		}
	}

	public interface EzmoDatabaseInterface {

		public void onResult(DataModel result);

		public void onError();
	}

}
