package com.ezmo.sms.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.ezmo.free.libs.commonUtils.CommonUtil;
import com.ezmo.sms.C;
import com.ezmo.sms.R;

public class SettingActivity extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.setting_activity, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{

		super.onActivityCreated(savedInstanceState);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		// {
		// setTheme(android.R.style.Theme_Holo_NoActionBar);
		// } else
		// {
		// setTheme(android.R.style.Theme_NoTitleBar);
		// }
		// setContentView(R.layout.setting_activity);
		initWidgets();
	}

	Button setNick;
	CheckBox bgmCheckBox;
	LayoutInflater inflater;
	TextView tv;

	private void initWidgets()
	{
		inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// setNick = (Button) getView().findViewById(R.id.setNickNameBtn);
		// setNick.setOnClickListener(click);
		// tv = (TextView) getView().findViewById(R.id.nickNameTv);
		// setNickName();
		bgmCheckBox = (CheckBox) getView().findViewById(R.id.bgmCheckBox);
		bgmCheckBox.setOnCheckedChangeListener(change);
		if (CommonUtil.getCommonPrefBoolean(getActivity(), C.KEY_AUTO_SEND))
		{
			bgmCheckBox.setChecked(true);
		}
	}

	private OnCheckedChangeListener change = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			CommonUtil.setCommonPrefBoolean(getActivity(), C.KEY_AUTO_SEND, isChecked);
		}
	};

	private void setNickName()
	{
		// String name = CommonUtil.getCommonPref(getActivity(),
		// CommonValues.NICK_NAME_KEY);
		// if ("".equals(name))
		// {
		// tv.setText("사용중인 별명 : 설정된 별명이 없습니다.");
		// } else
		// {
		// tv.setText("사용중인 별명 : " + name);
		// }
	}

	private View.OnClickListener click = new View.OnClickListener() {

		@Override
		public void onClick(View v)
		{
			// switch (v.getId())
			// {
			// case R.id.setNickNameBtn:
			// showSetNickNameDialog();
			// break;
			//
			// default:
			// break;
			// }

		}
	};

	// CustomDialogEdittext customDialogEdittext;
	//
	// private void showSetNickNameDialog()
	// {
	// customDialogEdittext = new CustomDialogEdittext(getActivity());
	// //
	// customDialogEdittext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	// // Dialog 밖을 터치 했을 경우 Dialog 사라지게 하기
	// // mDialog.setCanceledOnTouchOutside(true);
	// // Dialog 밖의 View를 터치할 수 있게 하기 (다른 View를 터치시 Dialog Dismiss)
	// //
	// customDialogEdittext.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
	// // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
	// // Dialog 자체 배경을 투명하게 하기
	// // mDialog.getWindow().setBackgroundDrawable
	// // (new ColorDrawable(android.graphics.Color.TRANSPARENT));
	//
	// customDialogEdittext.show();
	// customDialogEdittext.setOnDismissListener(new OnDismissListener() {
	//
	// @Override
	// public void onDismiss(DialogInterface dialog)
	// {
	// setNickName();
	// }
	// });
	// }

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		// if (customDialogEdittext != null)
		// {
		// customDialogEdittext.dismiss();
		// customDialogEdittext = null;
		// }
	}

}
