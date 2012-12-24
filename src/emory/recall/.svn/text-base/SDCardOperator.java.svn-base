package emory.recall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

public class SDCardOperator {
	
	//append writing
	static void writeToSDcardFile(String fileName, String dir, String content, boolean isAppend){
		String sDStateString = android.os.Environment.getExternalStorageState();
		File myFile = null;
		if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			try{
				File SDFile = android.os.Environment.getExternalStorageDirectory();
				File destDir = new File(SDFile.getAbsolutePath() + dir);
				if (!destDir.exists())
					destDir.mkdir();
				myFile = new File(destDir + File.separator + fileName);
				if (!myFile.exists()) {
					myFile.createNewFile();
					}
				FileOutputStream outputStream = new FileOutputStream(myFile, isAppend);
				outputStream.write(content.getBytes());
				outputStream.close();
				}
		catch(Exception e){
			e.printStackTrace();
			}
		}
	}
	
	static String getFilePath(String fileName, String dir){
		String res = null;
		File SDFile = android.os.Environment.getExternalStorageDirectory();
		res = SDFile.getAbsolutePath() + dir + File.separator + fileName;
		return res;
	}
	
	//only for limited size
	static String readFromSDcardFile(String fileName, String dir){
		String res = null;
		File SDFile = android.os.Environment.getExternalStorageDirectory();
		File myFile = new File(SDFile.getAbsolutePath() + dir + File.separator + fileName);
		if(myFile.exists())
		{
			try{
				FileInputStream inputStream = new FileInputStream(myFile);
				byte[] buffer = new byte[1024];
				inputStream.read(buffer);
				inputStream.close();
				res=new String(buffer);
			}catch (Exception e){
				
			}
		}
		return res;
	}
	

	static ArrayList<HistoryEntry> extractURLEntryFromSDByTaskID(String fileName, String dir,String uid, String taskID){
		ArrayList<HistoryEntry> res = new ArrayList<HistoryEntry>();
		File SDFile = android.os.Environment.getExternalStorageDirectory();
		File myFile = new File(SDFile.getAbsolutePath() + dir + File.separator + fileName);
		String test = "";
		if(myFile.exists())
		{
			try{
				FileInputStream inputStream = new FileInputStream(myFile);
				BufferedReader buReader=new BufferedReader(new FileReader(myFile));
				for(String s = buReader.readLine(); s != null; s = buReader.readLine())
				{
					if(s.startsWith("&[s]=&uid=" + uid) && s.contains(taskID))
					{
						String time = StrUtil.extractTime(s);
						String url = StrUtil.extractUrl(s);
						String taskid = StrUtil.extractTaskID(s);
						
						if(time != null && url != null && time != "" && url != ""){
							if(url.equals("StartNewTask") || url.equals("EndCurrentTask"))
								res.add(new HistoryEntry(time, false, url, taskid));
							else{
								String serpIndex = StrUtil.extractSERPIndex(s);
								res.add(new HistoryEntry(time, false, url, taskid, serpIndex));
							}
						}
					}
				}
			}catch (Exception e){
				
			}
		}
		return res;
	}
	
	//Obsolete implementation, which should be abandoned.
	static ArrayList<HistoryEntry> extractURLEntryFromSD(String fileName, String dir, String uid){
		ArrayList<HistoryEntry> res = new ArrayList<HistoryEntry>();
		File SDFile = android.os.Environment.getExternalStorageDirectory();
		File myFile = new File(SDFile.getAbsolutePath() + dir + File.separator + fileName);
		String test = "";
		if(myFile.exists())
		{
			try{
				FileInputStream inputStream = new FileInputStream(myFile);
				BufferedReader buReader=new BufferedReader(new FileReader(myFile));
				for(String s = buReader.readLine(); s != null; s = buReader.readLine())
				{
					if(s.startsWith("&[s]=&uid=" + uid))
					{
						String time = StrUtil.extractTime(s);
						String url = StrUtil.extractUrl(s);
						//if(time != null && url != null && time != "" && url != "")
							//res.add(new HistoryEntry(time, false, url, taskid));
					}
				}
			}catch (Exception e){
				
			}
		}
		return res;
	}
}
