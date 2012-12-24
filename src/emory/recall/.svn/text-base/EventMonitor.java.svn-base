package emory.recall;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import android.os.Process;
import android.util.Log;


public class EventMonitor {
	private static EventMonitor sSingleton;
	private static final String TAG = "Recall_EventMonitor";
	//private static final String EVENT_URL = "http://192.168.1.100/cgi-bin/recalltag_recorder.pl?";
	//private static final String EVENT_URL = "http://erowser.heliohost.org/recalltag_recorder.pl?";
	private static final String EVENT_URL = "http://ir-ub.mathcs.emory.edu/saveRecallTagger.cgi?";
	
	private EventsConsumer mEventsConsumer;
	private LinkedList<String> mQueue;
	private final Semaphore events_in_queue = new Semaphore(0, true);// how many events are in the queue.


	
	static EventMonitor getInstance() {
		if (sSingleton == null) {
			sSingleton = new EventMonitor();
		}
		return sSingleton;
	}
	
	
	public EventMonitor() {
		mQueue = new LinkedList<String>();
		mEventsConsumer = new EventsConsumer();
	}
	
	// Program is about to run in foreground
	public void sendResume() {
		sendString("ev=Resume");
	}

	//ViewedTime(in the versionmark) | visittime | taskid | relevance | ifviewed | judgement | url.
	public void sendLogJudgement(HistoryEntry loggedEntry){
		if (loggedEntry.timeStr.equals("") || loggedEntry.task_id.equals("")
				|| loggedEntry.urlStr.equals("")
				|| loggedEntry.getRelevanceRate() == -1
				|| loggedEntry.getJudgeResult() == -1) {
			sendString("error data");
		}
		else{
		sendString("ev=Judge&EntryViewedTime=" + loggedEntry.timeStr
				+ "&EntryTaskUid=" + loggedEntry.task_id + "&EntryRelevanceRate="
				+ loggedEntry.getRelevanceRate() + "&ifviewed="
				+ loggedEntry.getIfViewed() + "&judgement="
				+ loggedEntry.getJudgeResult() + "&EntryURL="
				+ loggedEntry.urlStr);
		}
	}
	
	// Start to run the monitor.
	public void StartRun() {
		new Thread(mEventsConsumer).start();
	}
	
	// return a string that every event request needs.
	public String versionTimeMark() {
		return "&v=2.1&time=" + String.valueOf(new Date().getTime()) + "&source=recalltag&";
	}
	
	// add a header to this string and then send it to the buffer.
	void sendString(String surfix) {
		sendString(surfix, false);
	}

	// add a header to this string and then send it to the buffer.
	void sendString(String surfix, boolean force) {
		String tsurfix = versionTimeMark() + surfix + "&[E]=";// "&[E]=" is used to mark the end of events.
		Log.w(TAG, tsurfix);
		queueMessage(tsurfix, force);
	}
	
	// simple encoding
	String Encode(String before) {
		String after = null;
		try {
			after = URLEncoder.encode(before, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return after;
	}
	
	// handle events in queue and send them to the server through "GET" http requests.
	public class EventsConsumer implements Runnable {
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			while (true) {
				String urlStr = getFirstMessage();
				getUrlData(urlStr, null);
			}
		}
	}
	
	private String initPrefix() {
		return EVENT_URL;
	}
	
	public void queueMessage(String tsurfix, boolean force) {
		mQueue.add(initPrefix() + tsurfix);
		if(!mQueue.isEmpty())
			events_in_queue.release();
	}
	
	
	// get first message from the head of the queue.
	public String getFirstMessage() {
		String urlStr = null;
		try {
			events_in_queue.acquire();// will be blocked here if no message in it
			urlStr = mQueue.getFirst();
			mQueue.removeFirst();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return urlStr;
	}
	
	// Can do "Get" and "Post" methods. if "data"==null, do "Get"; else do "Post"
	public String getUrlData(String urlStr, String data) {
		String DataStr = "";
		if (urlStr != null) {
			try {
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				// Send data
				if ((data != null) && (data.length() > 0)) {
					// post data
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();
				}
				// Get the response
				String strLine;
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((strLine = rd.readLine()) != null) { // Process, read feedback
					DataStr += strLine;
				}
				rd.close();
			} catch (Exception e) {
			}
		}
		return DataStr;
	}
	
}
