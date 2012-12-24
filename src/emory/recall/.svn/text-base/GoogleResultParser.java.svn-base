package emory.recall;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.os.Process;
import android.util.Log;

public class GoogleResultParser {
	private static GoogleResultParser sSingleton;
	private ArrayList<HistoryEntry> unviewedAbstractsList, viewedAbstractsList, unviewedPagesList, viewedPagesList;
	private ArrayList<HistoryEntry> urlList;
	private ArrayList<String> wholeurlset;
	private ArrayList<String> processed_serpfile_set;
	private ResultProcessor googleProcessor;
	private int urlList_tracker = 0;
	
	private boolean bIfProcessed = false;
	private int totalPageNum = 0;
	
	static GoogleResultParser getInstance() {
		if (sSingleton == null) {
			sSingleton = new GoogleResultParser();
		}
		return sSingleton;
	}
	
	public void startParsing(ArrayList<HistoryEntry> urlHistoryList){
		this.urlList = urlHistoryList;
		bIfProcessed = false;
		urlList_tracker = 0;
		unviewedPagesList = new ArrayList<HistoryEntry>();
		viewedPagesList = new ArrayList<HistoryEntry>();
		processed_serpfile_set = new ArrayList<String>();
		googleProcessor = new ResultProcessor();
		wholeurlset = new ArrayList<String>();
		new Thread(googleProcessor).start();
	}
	
	public class ResultProcessor implements Runnable{
		public void run(){
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			while(urlList_tracker < urlList.size()){
				processHistoryEntry(urlList.get(urlList_tracker));
				urlList_tracker++;	
			}
			bIfProcessed = true;
			totalPageNum = unviewedPagesList.size() + viewedPagesList.size();
		}
	}
	private int num =0;
	private void processHistoryEntry(HistoryEntry entry) {
		if(entry.urlStr.equals("StartNewTask")){
			return;
		}
		if(entry.urlStr.equals("EndCurrentTask")){
			return;
		}
		if(entry.urlStr.equals("http://www.google.com/m?client=ms-android-verizon&source=android-home"))
			return;
		else {
			if(StrUtil.bIsGoogleResultURL(entry.urlStr)){
				String delimiter = "\\|";
				String[] temp = entry.task_id.split(delimiter);
				String userID = temp[0];
				
				String path = "SERPHTML_" + temp[0] + "_"+ temp[1] + "_"+ String.valueOf(entry.fromSERPindex)  + ".txt";
				if(processed_serpfile_set.contains(path)){
					return;
				}
				File input = new File(SDCardOperator.getFilePath(path, ""));
				try {
					Document doc = Jsoup.parse(input, "UTF-8", "");
					Element content = doc.getElementById("rso");
					Elements lis = content.getElementsByTag("li");
					Log.v("test", "size: " + lis.size());
					for (Element li : lis) {
						// to remove the news box, image box, google map results in serp.
						if(li.parent() != content){
							continue;
						}
						String id = li.attr("id");
						Log.v("id", id);
						if(id == "newsbox" || id == "imagebox_bigimages"){
							Log.v("Result list", "one missed, " + li.getElementsByTag("a").get(0).attr("href"));
							continue;
						}
							
						Elements links = li.getElementsByTag("a");
						String linkHref = links.get(0).attr("href");
						if(linkHref.startsWith("/")){
							Log.v("Result list", "one missed, " + li.getElementsByTag("a").get(0).attr("href"));
							continue;
						}
						Log.v("whole set",linkHref);
						HistoryEntry tmpEntry = new HistoryEntry(entry.timeStr, false, linkHref, entry.task_id);
						insertList(unviewedPagesList, tmpEntry);
						wholeurlset.add(linkHref);
 					}
					Log.v("Number of unviewed", String.valueOf(unviewedPagesList.size()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				processed_serpfile_set.add(path);
			}
			 else {
				removeEntry(unviewedPagesList, entry);
				entry.setbIfViewed(true);
				/*
				for(int i =0; i<wholeurlset.size(); i++){
					if(StrUtil.urlMatch(wholeurlset.get(i), entry.urlStr))
						return;
				}
				*/
				insertList(viewedPagesList, entry);
				num++;
				Log.v("Number of viewed", String.valueOf(num));
				Log.v("view set", entry.urlStr);
			}
		}
	}
	
	public ArrayList<HistoryEntry> extractUnviewedPages(){
		return unviewedPagesList;
	}
	
	public ArrayList<HistoryEntry> extractViewedPages(){
		return viewedPagesList;
	}
	
	public boolean checkIfProcessed(){
		return bIfProcessed;
	}
	
	public int getTotalPageNum(){
		return totalPageNum;
	}
	
	private boolean removeEntry(ArrayList<HistoryEntry> list, HistoryEntry entry){
		for(int i=0; i<list.size(); i++){
			if(StrUtil.urlMatch(list.get(i).urlStr, entry.urlStr))
			{
				Log.v("removed set", entry.urlStr + "|"+ String.valueOf(i));
				list.remove(i);
				Log.v("Number of viewed", String.valueOf(list.size()));
				return true;
			}
		}
		Log.v("test", "remove error");
		return false;
	}
	
	//special insert method which deals with duplicate pages.
	private void insertList(ArrayList<HistoryEntry> list, HistoryEntry entry){
		for(int i =0; i<list.size(); i++)
		{
			if(StrUtil.urlMatch(list.get(i).urlStr, entry.urlStr))
				return;
		}
		list.add(entry);
		return;
	}
	
}