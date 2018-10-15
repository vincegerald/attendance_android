package com.example.attendance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	AlertDialog.Builder builder;
	ListView lv;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> stat = new ArrayList<String>();
	ArrayList<String> names = new ArrayList<String>();
	ArrayList<String> numberr = new ArrayList<String>();
	ArrayList<String> idnumber = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	EditText studentName, idNumber, txtSearch;
	private String id;
	private String name;
	private AdapterContextMenuInfo info;
	boolean editFlag;
	boolean presentFlag;
	boolean absentFlag;
	private String pid;
	private String primaryId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.lv = (ListView) this.findViewById(R.id.listView1);
		this.txtSearch = (EditText) this.findViewById(R.id.editText1);
		this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		this.lv.setAdapter(adapter);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.txtSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				adapter.getFilter().filter(arg0);
			}
		});
		this.registerForContextMenu(lv);
		this.Students();
        
    }

    
	public boolean onContextItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	switch(id){
    		case R.id.DELETE:
    			String num = numberr.get(this.info.position);
    			Toast.makeText(this, num, Toast.LENGTH_SHORT).show();
    			this.deleteStudent(num);
    			break;
    		case R.id.EDIT:
    			this.editFlag = true;
    			AlertDialog.Builder alert = new AlertDialog.Builder(this);
    			alert.setTitle(names.get(this.info.position));
    			LinearLayout layout = new LinearLayout(this);
    			layout.setOrientation(LinearLayout.VERTICAL);
    			this.primaryId = numberr.get(this.info.position);
    			this.idNumber = new EditText(this);
    			this.idNumber.setText(idnumber.get(this.info.position));
    			layout.addView(this.idNumber);
    			this.studentName = new EditText(this);
    			this.studentName.setText(names.get(this.info.position));
    			layout.addView(this.studentName);
    			alert.setView(layout);
    			alert.setPositiveButton("UPDATE", this);
    			alert.setNegativeButton("CANCEL", this);
    			alert.show();
    			break;
    		case R.id.PRESENT:
    			this.presentFlag = true;
    			this.absentFlag = false;
    			AlertDialog.Builder alertt = new AlertDialog.Builder(this);
    			this.primaryId = numberr.get(this.info.position);
    			LinearLayout layoutt = new LinearLayout(this);
    			layoutt.setOrientation(LinearLayout.VERTICAL);
    			alertt.setTitle(names.get(this.info.position));
    			alertt.setMessage("Is this student present?");
    			alertt.setPositiveButton("YES", this);
    			alertt.setNegativeButton("NO", this);
    			alertt.setView(layoutt);
    			alertt.show();
    			break;
    		case R.id.ABSENT:
    			this.presentFlag = false;
    			this.absentFlag = true;
    			AlertDialog.Builder alerttt = new AlertDialog.Builder(this);
    			this.primaryId = numberr.get(this.info.position);
    			LinearLayout layouttt = new LinearLayout(this);
    			layouttt.setOrientation(LinearLayout.VERTICAL);
    			alerttt.setTitle(names.get(this.info.position));
    			alerttt.setMessage("Is this student absent?");
    			alerttt.setPositiveButton("YES", this);
    			alerttt.setNegativeButton("NO", this);
    			alerttt.setView(layouttt);
    			alerttt.show();
    			break;
    			
    	}
		return super.onContextItemSelected(item);
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.context, menu);
        this.info = (AdapterContextMenuInfo) menuInfo;
        String test = stat.get(this.info.position);
        menu.setHeaderTitle(names.get(this.info.position));
        
        if(stat.get(this.info.position).equals("ABSENT")){
        	menu.findItem(R.id.ABSENT).setVisible(false);
        }
        else{
        	menu.findItem(R.id.PRESENT).setVisible(false);
        }
        Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }	
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		this.editFlag = false;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Add Student");
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		this.idNumber = new EditText(this);
		this.idNumber.setHint("Enter ID Number");
		layout.addView(this.idNumber);
		this.studentName = new EditText(this);
		this.studentName.setHint("Enter Student Name");
		layout.addView(this.studentName);
		alert.setView(layout);
		alert.setPositiveButton("ADD", this);
		alert.setNegativeButton("CANCEL", this);
		alert.show();
		return super.onOptionsItemSelected(item);
	}
	
	private void deleteStudent(String num){
		String url = "http://192.168.0.57/attendance_server/deleteStudent.php";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost= new HttpPost(url);
		List<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>(1);
		namevaluepairs.add(new BasicNameValuePair("id", num));
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if(resEntity != null){
				String responseStr = EntityUtils.toString(resEntity).trim();
				Log.v("data", "Response: " +  responseStr);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
		this.adapter.notifyDataSetChanged();
		this.Students();
	}
	
	private void Students(){
		try {
			names.clear();
			numberr.clear();
			list.clear();
			stat.clear();
			URL url = new URL("http://192.168.0.57/attendance_server/students.php");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String s = br.readLine();
			Log.d("data", s);
			is.close();
			conn.disconnect();
			JSONObject json = new JSONObject(s);
			JSONArray array = json.getJSONArray("students");
			for(int i = 0; i < array.length(); i++){
				JSONObject item = array.getJSONObject(i);
				String name = item.getString("name");
				String status = item.getString("status");
				String idNum = item.getString("idnumber");
				String number = item.getString("id");
				list.add(name + ":" + idNum);
				names.add(name);
				numberr.add(number);
				idnumber.add(idNum);
				stat.add(status);
				
				
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.adapter.notifyDataSetChanged();
		
		
	}
	

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		switch(arg1){
			case AlertDialog.BUTTON_POSITIVE:
				if(this.editFlag){
					this.id = this.idNumber.getText().toString();
					this.name = this.studentName.getText().toString();
					this.pid = this.primaryId;
					String url = "http://192.168.0.57/attendance_server/editStudent.php";
					Toast.makeText(this, this.name, Toast.LENGTH_SHORT).show();
					Toast.makeText(this, this.id, Toast.LENGTH_SHORT).show();
					Toast.makeText(this, this.pid, Toast.LENGTH_SHORT).show();
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost= new HttpPost(url);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("name", this.name));
					nameValuePairs.add(new BasicNameValuePair("idnumber", this.id));
					nameValuePairs.add(new BasicNameValuePair("id", this.pid));
					
					try {
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity resEntity = response.getEntity();
						if(resEntity != null){
							String responseStr = EntityUtils.toString(resEntity).trim();
							Log.v("data", "Response: " +  responseStr);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.Students();
					break;
				}
				else if(this.presentFlag){
					this.pid = this.primaryId;
					String url = "http://192.168.0.57/attendance_server/studentPresent.php";
					Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost= new HttpPost(url);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("id", this.pid));
					
					try {
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity resEntity = response.getEntity();
						if(resEntity != null){
							String responseStr = EntityUtils.toString(resEntity).trim();
							Log.v("data", "Response: " +  responseStr);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.Students();
					break;
				}
				else if(this.absentFlag){
					this.pid = this.primaryId;
					String url = "http://192.168.0.57/attendance_server/studentAbsent.php";
					Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost= new HttpPost(url);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("id", this.pid));
					
					try {
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity resEntity = response.getEntity();
						if(resEntity != null){
							String responseStr = EntityUtils.toString(resEntity).trim();
							Log.v("data", "Response: " +  responseStr);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.Students();
					break;
				}
				else{
					this.id = this.idNumber.getText().toString();
					this.name = this.studentName.getText().toString();
					if(!this.id.isEmpty() && !this.name.isEmpty()){
						Log.d("name", this.name);
						Log.d("id", this.id);
						String url = "http://192.168.0.57/attendance_server/addStudent.php";
						Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
						HttpClient httpClient = new DefaultHttpClient();
						HttpPost httpPost= new HttpPost(url);
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("name", this.name));
						nameValuePairs.add(new BasicNameValuePair("idnumber", this.id));
						
						try {
							httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							HttpResponse response = httpClient.execute(httpPost);
							HttpEntity resEntity = response.getEntity();
							if(resEntity != null){
								String responseStr = EntityUtils.toString(resEntity).trim();
								Log.v("data", "Response: " +  responseStr);
								this.idNumber.setText("");
								this.studentName.setText("");
							}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				
					else{
						Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
					}
					this.Students();
					break;
				}
				
			case AlertDialog.BUTTON_NEGATIVE:
				arg0.dismiss();
				break;
		}
	}
}
		


	
    


