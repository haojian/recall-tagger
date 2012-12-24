package emory.recall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Recall_taggerActivity extends Activity{
	private static final String logFileName = "erowserHistory.txt";
	private String userID = "error";
	private String task_uid; 
	private int index_unviewedAbstracts, index_viewedAbstracts,
			index_unviewedPages, index_viewedPages;
	private ParsedResultCollection resultCollection;
	private WebView mWebView;
	private RatingBar rating;
	private ProgressBar Pbar;
	private TextView hint_text;
	private Button skip_btn;
    Random generator;
    
	private ArrayList<HistoryEntry> currentTaskTestSet;
	private HistoryEntry currentEntry;
	private int taskIndex;
	
	private String[] hintSet = {" Not exist", " Bad", " Fair", " Good", " Excellent", " Perfect"};
	
	private ArrayList<String> taskDescList;
	private String taskDesc0 = "Warm-up Task:  Assuming that you are about to exchange for a different rental car, and are trying to find the closet Enterprise rental car location to make the exchange. Also, you would like to find the phone number to the location to check the car availability.";
	private String taskDesc1 = "Current Task 1:  What was the best selling book (title and author) of 2011 in us? how many copies of the book were sold in 2011 in us?";
	private String taskDesc2 = "Current Task 2:  Find three vegetarian restaurants near Lenox Square.";
	private String taskDesc3 = "Current Task 3:  What is the average temperature in Dallas, SD for winter? Summer?";
	private String taskDesc4 = "Current Task 4:  How many pixels must be dead on a iPad 3 before Apple will replace it? Assume the tablet is still under warranty.";
	private String taskDesc5 = "Current Task 5:  In what year did the USA experience its worst drought? What was the average precipitation in the country that year?";
	private String taskDesc6 = "Current Task 6:  Is the band State Radio coming to Atlanta, GA within the next year? If not, when and where will they be playing closest?";
	private String taskDesc7 = "Current Task 7:  Find the hours of the Target stores nearest to the Emory University.";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Pbar = (ProgressBar)findViewById(R.id.webviewer_progressbar);
		hint_text = (TextView)findViewById(R.id.hint_text);
		//skip_btn = (Button)findViewById(R.id.btn_skip);
		/*
		 * 
    <Button
        android:id="@+id/btn_skip"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Skip" 
        android:onClick="skip_btn_onClick"/>
		 * */
		currentTaskTestSet = new ArrayList<HistoryEntry>();
		hint_text.setText("");
		generator = new Random();
		setUpWebView();
		EventMonitor.getInstance().StartRun();
		taskDescList = new ArrayList<String>();
		taskDescList.add(taskDesc0);
		taskDescList.add(taskDesc1);
		taskDescList.add(taskDesc2);
		taskDescList.add(taskDesc3);
		taskDescList.add(taskDesc4);
		taskDescList.add(taskDesc5);
		taskDescList.add(taskDesc6);
		taskDescList.add(taskDesc7);
		
		task_uid = (String) this.getIntent().getCharSequenceExtra("task_uid");
		if(task_uid != null){
			String delimiter = "\\|";
			String[] temp = task_uid.split(delimiter);
			userID = temp[0];
			taskIndex = Integer.parseInt(task_uid.split(delimiter)[1]);
			Log.v("userid", userID);
			Log.v("taskIndex", String.valueOf(taskIndex));
			startParsing();
			startNewRatingTask();
		}
		else{
			new AlertDialog.Builder(this)
			.setTitle("No userid available!")
			// .setMessage("Thanks!")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							//
							
						}
					}).show();
		}
	}
	
	private void startNewRatingTask() {
		new AlertDialog.Builder(this)
				.setTitle("New Rating Task Starts!")
				// .setMessage("Thanks!")
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								//
								
							}
						}).show();

		while (!GoogleResultParser.getInstance().checkIfProcessed()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<HistoryEntry> tmpViewedSet, tmpUnviewedSet;
		tmpViewedSet = GoogleResultParser.getInstance().extractViewedPages();
		tmpUnviewedSet = GoogleResultParser.getInstance()
				.extractUnviewedPages();
		if (tmpViewedSet != null || tmpUnviewedSet != null) {
			currentTaskTestSet.addAll(tmpViewedSet);
			currentTaskTestSet.addAll(tmpUnviewedSet);
		}
		Log.v("TEST", String.valueOf(currentTaskTestSet.size()));
		Log.v("TEST", String.valueOf(tmpViewedSet.size()));
		for(int i=0; i<tmpViewedSet.size(); i++ ){
			Log.v("Final viewed set: ", tmpViewedSet.get(i).urlStr);
		}
		displayResultRandomly();
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
	}
	
	private void displayResultRandomly(){
		if(currentTaskTestSet.size() <=0)
		{
			new AlertDialog.Builder(this)
			.setTitle("Finished!")
			.setMessage("Would go back to Brower to start new task! You need to choose new task option to start a new task! ")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface arg0, int arg1) {
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					}).show();
		}
		else{
			int currentIndex = generator.nextInt(currentTaskTestSet.size());
			currentEntry = currentTaskTestSet.get(currentIndex);
			loadHistoryEntry(currentTaskTestSet.get(currentIndex));
			currentTaskTestSet.remove(currentIndex);
			currentTaskTestSet.trimToSize();
		}
	}
	
	private void startParsing() {
		// extract url arraylist
		ArrayList<HistoryEntry> urlList = SDCardOperator.extractURLEntryFromSDByTaskID(
				logFileName, "", userID, task_uid);
		// parse the url to get result collection
		
		GoogleResultParser.getInstance().startParsing(urlList);
	}
	
	private void removeDuplicate(){
		
	}

	private void setUserID() {
		AlertDialog.Builder login = new AlertDialog.Builder(this);
		login.setTitle("Hello!");
		login.setMessage("Input your login id");

		final EditText input = new EditText(this);
		login.setView(input);

		login.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				userID = input.getText().toString();
				if (userID == "")
					userID = "error";
				startParsing();
				startNewRatingTask();
			}
		});
		login.show();
	}

	private void loadHistoryEntry(HistoryEntry hEntry) {
		mWebView.loadUrl(hEntry.urlStr);
	}
	
	public void notviewed_btn_onClick(View source){
		if(currentEntry.getRelevanceRate() == -1)
		{
			new AlertDialog.Builder(this)
			.setTitle("Evaluate first!")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface arg0, int arg1) {
							// EventMonitor.getInstance()
						}
					}).show();
		}
		else{
			// Send log request
			currentEntry.setJudgeResult(0);
			EventMonitor.getInstance().sendLogJudgement(currentEntry);
			displayResultRandomly();
		}
	}
	
	public void viewed_btn_onClick(View source){
		if(currentEntry.getRelevanceRate() == -1)
		{
			new AlertDialog.Builder(this)
			.setTitle("Evaluate first!")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface arg0, int arg1) {
							// EventMonitor.getInstance()
						}
					}).show();
		}
		else{
			// Send log request
			currentEntry.setJudgeResult(1);
			EventMonitor.getInstance().sendLogJudgement(currentEntry);
			displayResultRandomly();
		}		
	}
	
public void pop_Relevance_Report(final String url){
		final String[] txt_list = new String[]{"Perfect -- the page completely satisfied my information need.", 
					"Excellent", "Good", "Fair", "Bad -- the page did not satisfy my information need in any way.", "This page does not exist, is not viewable, or is not written in English."};
		final int[] realValue = new int[]{6, 5, 4, 3, 2, 1, 0};
		final Dialog reporter = new Dialog(this);
		reporter.setTitle("Please evaluate how well the page you just visited:");
		ScrollView container = new ScrollView(this);
		TableLayout layout = new TableLayout(this);
		TableRow rowUrl = new TableRow(this);  
		TableRow rowTaskDescription = new TableRow(this);  
		TableRow rowRadBtn = new TableRow(this); 
		TableRow rowBtn = new TableRow(this); 
		
		final TextView txt_url = new TextView(this);
		txt_url.setText(url);
		txt_url.setGravity(1);
		if(getWindowManager().getDefaultDisplay().getOrientation() == 0){
			txt_url.setMaxWidth(400);
		}else{
			txt_url.setMaxWidth(750);
		}
		rowUrl.addView(txt_url);
		
		final TextView txt_taskdesc = new TextView(this);
		txt_taskdesc.setText(taskDescList.get(taskIndex));
		
		txt_taskdesc.setGravity(1);
		if(getWindowManager().getDefaultDisplay().getOrientation() == 0){
			txt_taskdesc.setMaxWidth(400);
		}else{
			txt_taskdesc.setMaxWidth(750);
		}
		rowTaskDescription.addView(txt_taskdesc);
		
		final RadioGroup rGroup = new RadioGroup(this);
		final RadioButton button1 = new RadioButton(this);
		button1.setText(txt_list[0]);
		rGroup.addView(button1);
		final RadioButton button2 = new RadioButton(this);
		button2.setText(txt_list[1]);
		rGroup.addView(button2);
		final RadioButton button3 = new RadioButton(this);
		button3.setText(txt_list[2]);
		rGroup.addView(button3);
		final RadioButton button4 = new RadioButton(this);
		button4.setText(txt_list[3]);
		rGroup.addView(button4);
		final RadioButton button5 = new RadioButton(this);
		button5.setText(txt_list[4]);
		rGroup.addView(button5);
		final RadioButton button6 = new RadioButton(this);
		button6.setText(txt_list[5]);
		rGroup.addView(button6);
		rowRadBtn.addView(rGroup);
		
		Button ok_btn = new Button(this); 
		ok_btn.setWidth(60);
		ok_btn.setText("OK"); 
		ok_btn.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	int radioBtnChecked = rGroup.getCheckedRadioButtonId();      
		    	
				if(radioBtnChecked < 0){
					
				}else{
					int index = -1;
					if(radioBtnChecked == button1.getId())
						index = 6;
					else if(radioBtnChecked == button2.getId())
						index = 5;
					else if(radioBtnChecked == button3.getId())
						index = 4;
					else if(radioBtnChecked == button4.getId())
						index = 3;
					else if(radioBtnChecked == button5.getId())
						index = 2;
					else if(radioBtnChecked == button6.getId())
						index = 1;
					else
						index =-1;
					currentEntry.setRelevanceRate(index);
					reporter.dismiss();
				}
		    }
		});	
		rowBtn.addView(ok_btn);
		
		layout.addView(rowUrl);
		layout.addView(rowTaskDescription);
		layout.addView(rowRadBtn);
		layout.addView(rowBtn);
		container.addView(layout);
		reporter.setContentView(container);
		reporter.show();
	}
	
	public void evaluate_btn_onClick(View source){
		if(currentEntry != null)
			pop_Relevance_Report(currentEntry.urlStr);
	}
	
	public void skip_btn_onClick(View source){
		new AlertDialog.Builder(this)
		.setTitle("Finished!")
		.setMessage("Would go back to Brower to start new task! You need to choose new task option to start a new task! ")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface arg0, int arg1) {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}).show();
	}

	private void setUpWebView() {
		WebViewClient linksdisabledClient = new WebViewClient() {
			// Override page so it's load on my view only
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		};
		// Get Web view
		mWebView = (WebView) findViewById(R.id.webViewer); // This is the id you
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setWebViewClient(linksdisabledClient);
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100 && Pbar.getVisibility() == ProgressBar.GONE) {
					Pbar.setVisibility(ProgressBar.VISIBLE);
					hint_text.setText("Loading. . .");
				}
				Pbar.setProgress(progress);
				if (progress == 100) {
					Pbar.setVisibility(ProgressBar.GONE);
					hint_text.setText("Loaded");
				}
			}
		});
	}
}
