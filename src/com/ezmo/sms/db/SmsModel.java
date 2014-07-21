package com.ezmo.sms.db;

public class SmsModel {

	private long id;
	private String date;
	private String time;
	private String accountNumber;
	private String userName;
	private int money;
	private long timeStamp;
	private boolean sendStatus;
	private String phoneNumber;

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	private String originalMessage;

	public String getOriginalMessage()
	{
		return originalMessage;
	}

	public void setOriginalMessage(String originalMessage)
	{
		this.originalMessage = originalMessage;
	}

	public boolean isSendStatus()
	{
		return sendStatus;
	}

	public void setSendStatus(boolean sendStatus)
	{
		this.sendStatus = sendStatus;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public int getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

}
