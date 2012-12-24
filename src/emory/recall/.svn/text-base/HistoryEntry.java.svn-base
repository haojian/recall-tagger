package emory.recall;

public class HistoryEntry {
	public String timeStr;
	public String urlStr;
	public String task_id;
	
	private Boolean bIfViewed;
	public int fromSERPindex = -1;
	private int rankinSERP = -1;
	private int relevanceRate = -1;
	private int judgeResult = -1; // judgereuslt = 0 for unviewed and judgeresult =1 for viewed.
	
	
	public HistoryEntry(String time, Boolean ifViewed, String url, String task_id){
		this.timeStr = time;
		this.bIfViewed = ifViewed;
		this.urlStr = url;
		this.task_id = task_id;
	}
	
	public HistoryEntry(String time, Boolean ifViewed, String url, String task_id, String _fromSERPindex){
		this.timeStr = time;
		this.bIfViewed = ifViewed;
		this.urlStr = url;
		this.task_id = task_id;
		if(_fromSERPindex != "")
			this.fromSERPindex = Integer.valueOf(_fromSERPindex);
		else
			fromSERPindex = -1;
	}
	
	public int getrankinSERP(){
		return rankinSERP;
	}
	
	public void setrankinSERP(int value){
		if(value>= 0 && value <=9)
			rankinSERP = value;
	}
	
	public int getRelevanceRate(){
		return relevanceRate;
	}
	
	public void setRelevanceRate(int ratelvl){
		relevanceRate = ratelvl;
		return;
	}
	
	public int getJudgeResult(){
		return judgeResult;
	}
	
	public void setJudgeResult(int judge){
		judgeResult = judge;
		return;
	}
	
	public void setbIfViewed(Boolean ifViewed){
		bIfViewed = ifViewed;
	}
	
	public int getIfViewed(){
		if(bIfViewed)
			return 1;
		else
			return 0;
	}
	
	public void reset(){
		timeStr = "";
		bIfViewed = false;
		urlStr = "";
		task_id = "";
		relevanceRate = -1;
		judgeResult = -1;
	}
}
