package com.ezmo.sms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.ezmo.free.libs.commonUtils.CommonUtil;
import com.ezmo.sms.C;
import com.ezmo.sms.db.SmsDBHelper;
import com.ezmo.sms.db.SmsModel;
import com.ezmo.sms.http.EzmoHttpClient;
import com.ezmo.sms.util.L;
import com.ezmo.sms.util.Util;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";
	private SmsDBHelper helper;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.context = context;
		if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
		{
			// Bundel 널 체크
			Bundle bundle = intent.getExtras();
			if (bundle == null)
			{
				return;
			}
			// pdu 객체 널 체크
			Object[] pdusObj = (Object[]) bundle.get("pdus");
			if (pdusObj == null)
			{
				return;
			}
			// message 처리
			SmsMessage[] smsMessages = new SmsMessage[pdusObj.length];
			for (int i = 0; i < pdusObj.length; i++)
			{
				smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				// Log.e(logTag, "NEW SMS " + i + "th");
				// Log.e(logTag, "DisplayOriginatingAddress : " +
				// smsMessages[i].getDisplayOriginatingAddress());
				// Log.e(logTag, "DisplayMessageBody : " +
				// smsMessages[i].getDisplayMessageBody());
				// Log.e(logTag, "EmailBody : " +
				// smsMessages[i].getEmailBody());
				// Log.e(logTag, "EmailFrom : " +
				// smsMessages[i].getEmailFrom());
				// --->Log.e(TAG, "OriginatingAddress : " +
				// smsMessages[i].getOriginatingAddress());
				// --->Log.e(TAG, "MessageBody : " +
				// smsMessages[i].getMessageBody());
				// Log.e(logTag, "ServiceCenterAddress : " +
				// smsMessages[i].getServiceCenterAddress());
				// --->Log.e(TAG, "TimestampMillis : " +
				// smsMessages[i].getTimestampMillis());

				String phoneNumber = smsMessages[i].getOriginatingAddress();
				String message[] = smsMessages[i].getMessageBody().split("\n");

				// KB 문자인지 확인
				String confirmKBString = message[0];
				if (confirmKBString.indexOf("KB") >= 0)
				{
					// 날짜와 시간을 가져온다.
					String dateTime[] = message[0].split(" ");
					String date = dateTime[0];
					date = date.replace("[KB]", "");
					String time = dateTime[1];

					// 계좌번호
					String accountNumber = message[1];

					// 보낸사람 이름
					String userName = message[2];

					// 금액
					int money = Integer.parseInt(Util.removeCommaString(message[4]));

					helper = new SmsDBHelper(context);

					SmsModel m = new SmsModel();
					m.setPhoneNumber(phoneNumber);
					m.setDate(date);
					m.setTime(time);
					m.setAccountNumber(accountNumber);
					m.setUserName(userName);
					m.setMoney(money);
					m.setTimeStamp(smsMessages[i].getTimestampMillis());
					m.setOriginalMessage(smsMessages[i].getMessageBody());

					long res = helper.getDAO().insert(m);
					m.setId(res);
					helper.dbClose();
					helper.helperClose();

					// Toast.makeText(context, smsMessages[i].getMessageBody(),
					// 0).show();

					sendBroadCast();
					// 자동 서버 등록
					if (CommonUtil.getCommonPrefBoolean(context, C.KEY_AUTO_SEND))
					{
						showToast("자동 등록을 시작합니다.");
						EzmoHttpClient c = new EzmoHttpClient(context);
						c.setEzmoNetworkInterface(ezmoNetworkInterface);
						c.startSend(C.URL_REG, m);
					}

				} else
				{
					L.d("KB문자가 아니어서 처리하지 않습니다.");
					return;
				}
				Log.e(TAG, "TimestampMillis : " + smsMessages[i].getTimestampMillis());
			}
		}
	}

	private void sendBroadCast()
	{
		Intent intent2 = new Intent();
		intent2.setAction(C.ACTION_ADD_SMS_ROW);
		context.sendBroadcast(intent2);
	}

	private EzmoHttpClient.EzmoNetworkInterface ezmoNetworkInterface = new EzmoHttpClient.EzmoNetworkInterface() {

		@Override
		public void onResult(String xml)
		{
			if (xml == null || "".equals(xml))
			{

				return;
			}
			xml = xml.trim();
			String res[] = xml.split("\\|");
			if (res != null)
			{
				if ("1".equals(res[0]))
				{
					helper = new SmsDBHelper(context);
					int result = helper.getDAO().updateContact(res[1]);
					helper.dbClose();
					helper.helperClose();
					helper = null;
					showToast("처리하였습니다.");
					sendBroadCast();
				}
			}
		}

		@Override
		public void onError(String msg)
		{
			showToast("처리 실패");
		}
	};

	private String beforeToastMsg;
	private Toast toast;

	private void showToast(String msg)
	{
		if (!msg.equals(toast))
		{
			toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		}
		toast.show();
		beforeToastMsg = msg;
	}

}
