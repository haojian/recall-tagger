package emory.recall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.conn.ssl.*;

public class Recall_Httpclient {
	private static HttpClient recallHttpClient;
    
    public Recall_Httpclient() {
    }
    
    public static HttpClient getHttpClient() {
        if(null== recallHttpClient) {
        	HttpParams params = new BasicHttpParams();
        	HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        	HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        	HttpProtocolParams.setUseExpectContinue(params, true);
        	
        	ConnManagerParams.setTimeout(params, 1000);
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            HttpConnectionParams.setSoTimeout(params, 4000);
            
            SchemeRegistry schReg =new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory(), 443));
            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(
                    params, schReg);
            
            recallHttpClient =new DefaultHttpClient(conMgr, params);
        }
        return recallHttpClient;
    }
    
    public static String resultContentDownload(String url) {
		String html = "";
		String contentStart = "<li class=\"g\"><h3 class=\"r\">";
		String contentEnd = "</li></ol>";
		boolean isContent = false;
		try {
			//HttpClient client = new DefaultHttpClient();
			HttpGet pageGet = new HttpGet(url);
			HttpResponse responseGet;
			responseGet = getHttpClient().execute(pageGet);
			if(responseGet.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new RuntimeException("Httpget failed");
			HttpEntity resEntityGet = responseGet.getEntity();
			
			
			if (resEntityGet != null) {
				final InputStream is = resEntityGet.getContent();
				BufferedReader buReader = new BufferedReader(
						new InputStreamReader(is), 8192);
				for (String s = buReader.readLine(); s != null; s = buReader
						.readLine()) {
					html += s;
					if (isContent == false && html.contains(contentStart)) {
						isContent = true;
					} else if (isContent == true && html.contains(contentEnd)) {
						// find the end
						isContent = false;
						is.close();
						buReader.close();
						return html;
					} else if (isContent == false) {
						// chunk the obsolete string.
						if (html.length() > contentStart.length())
							html = html.substring(html.length()
									- contentStart.length());
					}
				}
				is.close();
			}
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(isContent)
			return null;
		else
			return html;
	}
}
