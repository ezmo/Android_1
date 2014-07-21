package com.ezmo.sms.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezmo.sms.C;
import com.ezmo.sms.MainActivity;
import com.ezmo.sms.R;
import com.ezmo.sms.db.DBUtil;
import com.ezmo.sms.db.SmsDBHelper;
import com.ezmo.sms.db.SmsModel;
import com.ezmo.sms.http.EzmoHttpClient;
import com.ezmo.sms.util.L;
import com.ezmo.sms.util.Util;

public class Adapter extends BaseExpandableListAdapter {

	/**
	 * LayoutInflater를 가저오려면 컨텍스트를 넘겨받아야 한다.
	 */
	private Context context;
	/**
	 * 대 그룹에 보여줄 리스트
	 */
	private ArrayList<SmsModel> gList;
	/**
	 * 대 그룹을 눌렀을때 보여주는 자식 리스트
	 */
	private ArrayList<ArrayList<SmsModel>> children;
	/**
	 * xml으로 생성한 UI를 가저다 준다.
	 */
	private LayoutInflater inflater;

	private MainActivity activity;

	public Adapter(Context context, ArrayList<SmsModel> gropus, ArrayList<ArrayList<SmsModel>> children)
	{

		this.gList = gropus;
		this.children = children;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.activity = (MainActivity) context;
	}

	public ArrayList<ArrayList<SmsModel>> getAllList()
	{

		return this.children;
	}

	/*
	 * public void addItem(Model model){ if( !groups.contains(model.getGroup())
	 * ) { GM g = new GM(); g.setId(4); g.setName(model.getGroup());
	 * groups.add(g); }
	 * 
	 * int index = groups.indexOf(model.getGroup());
	 * 
	 * if(children.size() < index +1) { children.add(new ArrayList<Model>()); }
	 * 
	 * children.get(index).add(model); }
	 */

	@Override
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	@Override
	public SmsModel getChild(int groupPosition, int childPosition)
	{

		return children.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{

		return children.get(groupPosition).get(childPosition).getId();
	}

	/**
	 * children을 보여준다. ArrayAdapter의 getView와 동일하게 처리하면 된다.
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{

		final SmsModel model = getChild(groupPosition, childPosition);
		final ViewHolder holder;

		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.child_row, null);
			holder.child = (TextView) convertView.findViewById(R.id.tv);
			holder.child.setSingleLine(false);
			holder.sendBtn = (Button) convertView.findViewById(R.id.sendBtn);
			holder.delBtn = (Button) convertView.findViewById(R.id.delBtn);
			holder.phone = (TextView) convertView.findViewById(R.id.pn);

			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if (model != null)
		{
			if (model.isSendStatus())
			{
				holder.sendBtn.setVisibility(View.GONE);
			} else
			{
				holder.sendBtn.setVisibility(View.VISIBLE);
			}

			String all = "고객명: " + model.getUserName() + "\n\n";
			// all += "전화번호:" + model.getPhoneNumber() + "\n\n";
			all += "충전금액:" + Util.convertCommaString(model.getMoney() + "") + "\n\n";
			all += "계좌번호:" + model.getAccountNumber() + "\n\n";
			all += "시간1:" + model.getDate() + " | " + model.getTime() + "\n\n";
			all += "시간2:" + getTimeStampString(model.getTimeStamp()) + "\n\n";
			all += "원본 메시지:" + "\n\n";
			all += model.getOriginalMessage();

			holder.sendBtn.setTag(model);
			holder.delBtn.setTag(model);

			holder.sendBtn.setOnClickListener(click);
			holder.delBtn.setOnClickListener(click);

			holder.child.setText(all);
			holder.phone.setText(Html.fromHtml("<u>" + model.getPhoneNumber() + "</u>"));
			// t.setText(Html.fromHtml("<u>" + sitename + "</u>"));

		}
		return convertView;
	}

	SmsDBHelper helper;
	private View.OnClickListener click = new View.OnClickListener() {

		@Override
		public void onClick(View v)
		{
			SmsModel m = (SmsModel) v.getTag();
			switch (v.getId())
			{
			case R.id.sendBtn:
				if (m != null)
				{
					sendServer(m);
				}
				break;

			case R.id.delBtn:
				if (m != null)
				{
					L.e(m.getId());
					showDeleteConfirmDialog(m);
				}
				break;

			default:
				break;
			}
		}
	};

	private void sendServer(SmsModel m)
	{
		showProgressDialog();

		EzmoHttpClient c = new EzmoHttpClient(context);
		c.setEzmoNetworkInterface(ezmoNetworkInterface);
		c.startSend(C.URL_REG, m);
	}

	ProgressDialog pd;

	private void showProgressDialog()
	{
		hideProgressDialog();
		pd = new ProgressDialog(context);
		pd.setTitle("서버전송");
		pd.setMessage("처리중입니다.");
		pd.show();
	}

	private void hideProgressDialog()
	{
		if (pd != null)
		{
			pd.dismiss();
			pd = null;
		}
	}

	private EzmoHttpClient.EzmoNetworkInterface ezmoNetworkInterface = new EzmoHttpClient.EzmoNetworkInterface() {

		@Override
		public void onResult(String xml)
		{
			hideProgressDialog();
			if (xml == null || "".equals(xml))
			{
				showFailDialog();
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
					activity.placeholderFragment.refresh();
					showToast("처리하였습니다.");
				} else
				{
					showToast("잘못된 응답을 받았습니다 잠시후 다시 시도하세요");
				}
			}
		}

		@Override
		public void onError(String msg)
		{
			hideProgressDialog();

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

	private void showFailDialog()
	{
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("실패");
		b.setMessage("처리되지 않았습니다. 잠시후 다시 시도 하세요");
		b.setPositiveButton("확인", null);
		b.show();
	}

	Dialog deleteConfirm;

	private void showDeleteConfirmDialog(final SmsModel m)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("삭제확인");
		b.setMessage("아래 내용을 삭제하시겠습니까?\n\n" + m.getOriginalMessage());
		b.setPositiveButton("확인", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				helper = new SmsDBHelper(context);
				helper.getDAO().delete(m.getId());
				helper.helperClose();
				helper.dbClose();
				activity.placeholderFragment.refresh();
			}
		});
		b.setNegativeButton("취소", null);
		deleteConfirm = b.create();
		deleteConfirm.show();
	}

	private String getTimeStampString(long time)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return formatter.format(calendar.getTime());
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{

		return children.get(groupPosition).size();
	}

	@Override
	public String getGroup(int groupPosition)
	{
		// TODO Auto-generated method stub
		return gList.get(groupPosition).getUserName();
	}

	@Override
	public int getGroupCount()
	{
		// TODO Auto-generated method stub
		return gList.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		// TODO Auto-generated method stub
		return groupPosition;
	}

	/**
	 * 대그룹을 보여준다. ArrayAdapter의 getView와 동일하게 처리하면 된다.
	 */
	@Override
	public View getGroupView(int pos, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (view == null)
		{
			view = inflater.inflate(R.layout.title_row, null);
		}

		SmsModel m = gList.get(pos);
		if (m != null)
		{

			LinearLayout root = (LinearLayout) view.findViewById(R.id.root);
			if (pos % 2 == 0)
			{
				root.setBackgroundResource(R.drawable.list_bg_01);
			} else
			{
				root.setBackgroundResource(R.drawable.list_bg_02);
			}
			// if (pos % 2 == 0)
			// {
			// root.setBackgroundResource(R.xml.row1);
			// } else
			// {
			// root.setBackgroundResource(R.xml.row2);
			// }

			ImageView status = (ImageView) view.findViewById(R.id.statusIcon);
			if (m.isSendStatus())
			{
				status.setBackgroundResource(R.drawable.ic_green);
			} else
			{
				status.setBackgroundResource(R.drawable.ic_red);
			}

			TextView tv = (TextView) view.findViewById(R.id.title);

			tv.setText("고객명:" + m.getUserName() + " 금액:" + Util.convertCommaString(m.getMoney() + "") + "원 " + "시간:" + m.getDate() + "/" + m.getTime());
			tv.setSelected(true);
		}
		return view;
	}

	@Override
	public boolean hasStableIds()
	{

		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onGroupExpanded(int groupPosition)
	{
		super.onGroupExpanded(groupPosition);
	}

}