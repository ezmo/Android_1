package com.ezmo.sms.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.ezmo.sms.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class EzmoBaseSlidingFragmentActivity extends SlidingFragmentActivity {

	// protected ListFragment mFrag;
	protected SettingActivity mFrag;

	// public EzmoBaseSlidingFragmentActivity(int titleRes)
	// {
	// // TODO Auto-generated constructor stub
	// }

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null)
		{
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			// mFrag = new SampleListFragment();
			mFrag = new SettingActivity();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else
		{
			mFrag = (SettingActivity) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.xml.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
