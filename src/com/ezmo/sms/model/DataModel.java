package com.ezmo.sms.model;

import java.util.ArrayList;

import com.ezmo.sms.db.SmsModel;

public class DataModel {

	private ArrayList<SmsModel> smsList;
	private ArrayList<ArrayList<SmsModel>> children;

	public ArrayList<SmsModel> getSmsList()
	{
		return smsList;
	}

	public void setSmsList(ArrayList<SmsModel> smsList)
	{
		this.smsList = smsList;
	}

	public ArrayList<ArrayList<SmsModel>> getChildren()
	{
		return children;
	}

	public void setChildren(ArrayList<ArrayList<SmsModel>> children)
	{
		this.children = children;
	}

}
