package com.ezmo.sms.db;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import com.ezmo.sms.util.L;

public class DBUtil {

	public static boolean existDBFile(Context context)
	{
		boolean result = false;

		String filePath = "/data/data/" + context.getPackageName() + "/databases/" + SmsDBHelper.DB_NAME;
		File file = new File(filePath);
		if (file.exists())
		{
			return true;
		}

		return result;
	}

	public static void copySQLiteDB(Context context)
	{
		AssetManager manager = context.getAssets();
		String folderPath = "/data/data/" + context.getPackageName() + "/databases/";
		String filePath = "/data/data/" + context.getPackageName() + "/databases/" + SmsDBHelper.DB_NAME;
		File folder = new File(folderPath);
		File file = new File(filePath);

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try
		{
			InputStream is = manager.open(SmsDBHelper.DB_NAME);
			BufferedInputStream bis = new BufferedInputStream(is);

			if (!folder.exists())
			{
				folder.mkdir();
			}
			if (file.exists())
			{

				file.delete();
				file.createNewFile();
			}

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);

			int read = -1;
			byte[] buffer = new byte[1024];
			while ((read = bis.read(buffer, 0, 1024)) != -1)
			{
				bos.write(buffer, 0, read);
			}
			bos.flush();

			bos.close();
			fos.close();
			bis.close();
			is.close();
			L.d("DB 복사 완료");

		} catch (IOException e)
		{
			L.d("DB복사중 오류:" + e.getMessage());
		}
	}

}
