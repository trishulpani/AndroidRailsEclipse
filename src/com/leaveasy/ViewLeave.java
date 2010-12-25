package com.leaveasy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class ViewLeave extends Activity {
    /** Called when the activity is first created. */
	
	TextView tv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    
    public void viewLeaveDetailsHandler(View view){
    	
    	String jsonDetails = retrieveLeaveDetails();
    	tv = (TextView)findViewById(R.id.leaveDetails);
    	String leaveDetails = parseJSON(jsonDetails);
    	Log.d("LeavEasy", "Leave details :"+leaveDetails);
    	tv.setText(leaveDetails);
    }
    
    
    private String parseJSON(String json){
    	
    	Log.d("LeavEasy", "Going to parse :"+json);
    	StringBuilder leaveInfo = new StringBuilder();
    	try {
			JSONObject holder = new JSONObject(json);
			//should get user here
			JSONObject user = holder.getJSONObject("user");
			
			leaveInfo.append("Employee ID : "+user.getString("empno")).append("\n")
			.append("Name : "+user.getString("name")).append("\n")
			.append("Vacation Leave :"+user.getInt("l_vacation")).append("\n")
			.append("Sick Leave :"+user.getInt("l_sick")).append("\n")
			.append("Compensatory Leave :"+user.getInt("l_comp")).append("\n")
			.append("Personal Leave :"+user.getInt("l_personal"));
		
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("LeavEasy", "Leave details :"+leaveInfo);
		return leaveInfo.toString();
    	
    	
    }
    
    public void changeViewForLeaveApplication(View view){
    	
    	ViewSwitcher v_switcher = (ViewSwitcher)findViewById(R.id.ViewSwitcher01);
    	v_switcher.setDisplayedChild(1);
    	
    }
    
   public void changeViewForHome(View view){
    	
    	ViewSwitcher v_switcher = (ViewSwitcher)findViewById(R.id.ViewSwitcher01);
    	v_switcher.setDisplayedChild(0);
    	
    }
   
   
   public void submitApplication(View view){
	   
	   TextView fromDate = (TextView)findViewById(R.id.txtFromDate);
	   String leaveFromDate = fromDate.getText().toString();
	   
	   TextView toDate = (TextView)findViewById(R.id.txtToDate);
	   String leaveToDate = toDate.getText().toString();
	   
	   RadioButton rdbCasual = (RadioButton)findViewById(R.id.rbCasualLeave);
	   RadioButton rdbSick = (RadioButton)findViewById(R.id.rbSickLeave);
	   
	   boolean casualLeave = false;
	   boolean sickLeave = false;
	   String leaveType = "";
	   
	   if(rdbCasual.isChecked()){
		   leaveType = "Casual"; 
	   }
	   
	   if(rdbSick.isChecked()){
		   leaveType = "Sick";
	   }
	   
	   TextView contactDuringLeave = (TextView)findViewById(R.id.contactDuringLeave);
	   String leaveContact = contactDuringLeave.getText().toString();
	   
	   Log.d("LeavEasy::submitApplication => from date ", leaveFromDate);
	   Log.d("LeavEasy::submitApplication => to date ", leaveToDate);
	   Log.d("LeavEasy::submitApplication => Casual Leave ", String.valueOf(casualLeave));
	   Log.d("LeavEasy::submitApplication => Sick Leave ", String.valueOf(sickLeave));
	   Log.d("LeavEasy::submitApplication => Contact during Leave ", leaveContact);
	   
	   
	   //create, populate json and send to server for application
	   
	   try {
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost();
			request.setURI(new URI("http://192.168.1.21:3000/leave_details/create.json"));
			request.setHeader("Content-Type","application/json");
			
			
			StringBuilder jsonString = new StringBuilder("{\"contactinfo\":\"" +
					leaveContact+"\",\"empno\":\"223128\",\"fromdate\":\""+leaveFromDate+"\",\"leavetype\":\""
					+leaveType+"\",\"status\":\"submitted\",\"todate\":\""+leaveToDate+"\"}");
			
			
			JSONObject leaveDetailObject = new JSONObject(jsonString.toString());
			JSONObject holder = new JSONObject();
			
			holder.put("leave_detail", leaveDetailObject);
			
			Log.d("LeavEasy", "JSON :"+holder.toString());
			
			StringEntity stringEntity = new StringEntity(holder.toString());
			//stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			request.setEntity(stringEntity);
			
			HttpResponse response = httpClient.execute(request);
			
			String status = EntityUtils.toString(response.getEntity());
			if(status!=null){
				Toast.makeText(this, "Success !!", Toast.LENGTH_LONG).show();
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
	   
   }
    
    private String retrieveLeaveDetails() {
		
        String app_name = "ViewLeave";
    	
		HttpClient httpClient = new DefaultHttpClient();
		String msg = "";
		try{
			String url = "http://192.168.1.21:3000/users/1.json";
			Log.d(app_name, "About to GET :"+url);
			
			HttpGet method = new HttpGet(new URI(url));
			HttpResponse response = httpClient.execute(method);
			
			if(response != null)
			{
				msg = EntityUtils.toString(response.getEntity());
				Log.i(app_name, "received " + msg );
			}
			else
			{
				Log.i( app_name, "got a null response" );
			}
		} catch (IOException e) {
			Log.e( "ouch", "!!! IOException " + e.getMessage() );
		} catch (URISyntaxException e) {
			Log.e( "ouch", "!!! URISyntaxException " + e.getMessage() );
		}
		return msg;
	
	}

}