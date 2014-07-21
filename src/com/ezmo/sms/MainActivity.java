package com.ezmo.sms;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.ezmo.sms.adapter.Adapter;
import com.ezmo.sms.db.SmsDBHelper;
import com.ezmo.sms.db.SmsModel;
import com.ezmo.sms.fragment.EzmoBaseSlidingFragmentActivity;
import com.ezmo.sms.model.DataModel;
import com.ezmo.sms.task.EzmoDatabaseClient;
import com.ezmo.sms.util.L;
import com.ezmo.sms.util.Util;

@SuppressLint("ValidFragment")
public class MainActivity extends EzmoBaseSlidingFragmentActivity {

	public PlaceholderFragment placeholderFragment;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null)
		{
			placeholderFragment = new PlaceholderFragment(this);
			getFragmentManager().beginTransaction().add(R.id.container, placeholderFragment).commit();
		}

		registSMSBroadcastReceiver();
	}

	SmsReceiver smsAddReceiver;

	private void registSMSBroadcastReceiver()
	{
		if (smsAddReceiver == null)
		{
			smsAddReceiver = new SmsReceiver();
			IntentFilter theFilter = new IntentFilter();
			theFilter.addAction(C.ACTION_ADD_SMS_ROW);
			registerReceiver(smsAddReceiver, theFilter);
		}

	}

	class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(C.ACTION_ADD_SMS_ROW))
			{
				placeholderFragment.refresh();
			}

		}

	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			toggle();
			return true;
		} else if (id == R.id.action_refresh)
		{
			placeholderFragment.refresh();
			return true;
		} else if (id == R.id.action_import)
		{
			SMSList();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	ProgressDialog pd;

	public void showProgressDialog(String title, String msg)
	{
		hideProgressDialog();
		pd = new ProgressDialog(this);
		pd.setTitle(title);
		pd.setMessage(msg);
		pd.show();
	}

	public void hideProgressDialog()
	{
		if (pd != null)
		{
			pd.dismiss();
			pd = null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		MainActivity activity;

		@SuppressLint("ValidFragment")
		public PlaceholderFragment(Context context)
		{
			this.activity = (MainActivity) context;
		}

		public void refresh()
		{
			if (lv != null)
			{

			}
			init("REFRESH");

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			initWidgets(rootView);
			// setBroadCastReceiver();
			init("ONCREATE");
			return rootView;
		}

		private ExpandableListView lv;

		private void initWidgets(View rootView)
		{
			lv = (ExpandableListView) rootView.findViewById(R.id.expandableListView1);
			expListIndicatorAttach(lv);
		}

		// private int previousGroup = -1;

		private void init(String initType)
		{
			activity.showProgressDialog("SMS", "SMS를 가져옵니다.");
			EzmoDatabaseClient c = new EzmoDatabaseClient(getActivity(), ezmoDatabaseInterface);
			c.start(EzmoDatabaseClient.SELECT_ALL);

		}

		private EzmoDatabaseClient.EzmoDatabaseInterface ezmoDatabaseInterface = new EzmoDatabaseClient.EzmoDatabaseInterface() {

			@Override
			public void onResult(DataModel result)
			{
				activity.hideProgressDialog();
				if (result != null)
				{
					setAdapter(result);
				}

			}

			@Override
			public void onError()
			{
				activity.hideProgressDialog();
				Toast.makeText(getActivity(), "가져오기 실패", Toast.LENGTH_LONG).show();
			}
		};

		private void setAdapter(DataModel result)
		{
			if (result != null)
			{
				if (result.getSmsList() == null || result.getSmsList().size() == 0)
				{
					Toast.makeText(getActivity(), "결과 없음", Toast.LENGTH_LONG).show();
				}
			}

			Adapter a = new Adapter(getActivity(), result.getSmsList(), result.getChildren());
			lv.setAdapter(a);
			lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
				int previousGroup = -1;

				@Override
				public void onGroupExpand(int groupPosition)
				{
					if (groupPosition != previousGroup)
						lv.collapseGroup(previousGroup);
					previousGroup = groupPosition;
				}
			});

		}

		private void expListIndicatorAttach(final ExpandableListView expList)
		{
			ViewTreeObserver vto = expList.getViewTreeObserver();

			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout()
				{

					if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
					{
						expList.setIndicatorBounds(expList.getRight() - 100, expList.getRight() - 50);
					} else
					{
						expList.setIndicatorBoundsRelative(expList.getRight() - 100, expList.getRight() - 50);
					}
					// L.e("right" + expList.getRight());
					// expList.setIndicatorBounds(expList.getRight() - 100,
					// expList.getRight() - 50);
				}
			});
		}
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(smsAddReceiver);
		super.onDestroy();
	}

	public static final String MESSAGE_TYPE_INBOX = "1";
	public static final String MESSAGE_TYPE_SENT = "2";
	public static final String MESSAGE_TYPE_CONVERSATIONS = "3";
	public static final String MESSAGE_TYPE_NEW = "new";

	public void SMSList()
	{
		try
		{
			// Retrieve All SMS
			/*
			 * Inbox = "content://sms/inbox" Failed = "content://sms/failed"
			 * Queued = "content://sms/queued" Sent = "content://sms/sent" Draft
			 * = "content://sms/draft" Outbox = "content://sms/outbox"
			 * Undelivered = "content://sms/undelivered" All =
			 * "content://sms/all" Conversations = "content://sms/conversations"
			 * 
			 * addressCol= mCurSms.getColumnIndex("address"); personCol=
			 * mCurSms.getColumnIndex("person"); dateCol =
			 * mCurSms.getColumnIndex("date"); protocolCol=
			 * mCurSms.getColumnIndex("protocol"); readCol =
			 * mCurSms.getColumnIndex("read"); statusCol =
			 * mCurSms.getColumnIndex("status"); typeCol =
			 * mCurSms.getColumnIndex("type"); subjectCol =
			 * mCurSms.getColumnIndex("subject"); bodyCol =
			 * mCurSms.getColumnIndex("body");
			 */
			Uri allMessage = Uri.parse("content://sms/inbox");
			Cursor cur = this.getContentResolver().query(allMessage, null, null, null, null);
			if (cur != null)
			{
				int count = cur.getCount();

				L.e("SMS count = " + count);
				String row = "";
				String msg = "";
				String date = "";
				String protocol = "";

				cur.moveToFirst();
				ArrayList<String> list = new ArrayList<String>();
				ArrayList<SmsModel> sList = new ArrayList<SmsModel>();
				for (int i = 0; i < count; i++)
				{
					row = cur.getString(cur.getColumnIndex("address"));
					msg = cur.getString(cur.getColumnIndex("body"));
					date = cur.getString(cur.getColumnIndex("date"));
					protocol = cur.getString(cur.getColumnIndex("protocol"));
					// Logger.d( TAG , "SMS PROTOCOL = " + protocol);

					String type = "";
					if (protocol == MESSAGE_TYPE_SENT)
					{
						type = "sent";
					} else if (protocol == MESSAGE_TYPE_INBOX)
					{
						type = "receive";
					} else if (protocol == MESSAGE_TYPE_CONVERSATIONS)
					{
						type = "conversations";
					} else if (protocol == null)
					{
						type = "send";
					}

					// L.e("SMS Phone: " + row + " / Mesg: " + msg + " / Type: "
					// + type + " / Date: " + date);

					// list.add(msg);

					String phoneNumber = row;
					String message[] = msg.split("\n");

					// KB 문자인지 확인
					String confirmKBString = message[0];
					if (confirmKBString.indexOf("KB") >= 0)
					{
						try
						{
							// 날짜와 시간을 가져온다.
							String dateTime[] = message[0].split(" ");
							String datee = dateTime[0];
							datee = datee.replace("[KB]", "");
							String time = dateTime[1];

							// 계좌번호
							String accountNumber = message[1];

							// 보낸사람 이름
							String userName = message[2];

							// 금액
							int money = Integer.parseInt(Util.removeCommaString(message[4]));

							// SmsDBHelper h = new SmsDBHelper(this);

							SmsModel m = new SmsModel();
							m.setPhoneNumber(phoneNumber);
							m.setDate(datee);
							m.setTime(time);
							m.setAccountNumber(accountNumber);
							m.setUserName(userName);
							m.setMoney(money);
							m.setTimeStamp(System.currentTimeMillis());
							m.setOriginalMessage(msg);

							list.add(msg);
							sList.add(m);
							// h.getDAO().insert(m);
							// h.dbClose();
							// h.helperClose();
						} catch (Exception e)
						{
							L.e("문자 파싱 오류");
						}
					}
					cur.moveToNext();
				}
				showImportSMSDialog(list, sList);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void showImportSMSDialog(ArrayList<String> list, final ArrayList<SmsModel> sList)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("SMS 선택");

		ListView lv = new ListView(this);
		// String[] stringArray = new String[] { "Bright Mode", "Normal Mode" };
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
		lv.setAdapter(modeAdapter);
		builder.setView(lv);
		final Dialog dialog = builder.create();
		dialog.show();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> lv, View view, int position, long id)
			{
				SmsDBHelper h = new SmsDBHelper(MainActivity.this);

				h.getDAO().insert(sList.get(position));
				if (h != null)
				{
					h.dbClose();
					h.helperClose();
					h = null;
				}
				dialog.dismiss();
				placeholderFragment.refresh();
			}
		});

	}
}
