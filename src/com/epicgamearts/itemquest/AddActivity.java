package com.epicgamearts.itemquest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AddActivity extends ActionBarActivity implements OnClickListener {

	private Button scanBtn, scanBtn1;
	private TextView scoreTxt, scannedTxt, itemTxt;
	private String toFind, barcode, currentItem;
	private String filename = "data.txt";
	private String scanContent;
    File file = new File(filename);
   
    private final String USER_AGENT = "Mozilla/5.0";
    
       @Override
       protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_add);
           Intent intent = getIntent();
           scanBtn = (Button)findViewById(R.id.add_button);
           scanBtn1 = (Button)findViewById(R.id.add_data_button);
		   StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		   StrictMode.setThreadPolicy(policy);
		   scanBtn.setOnClickListener(this);
		   scanBtn1.setOnClickListener(this);
	    
       }
		
       /** Called when the user clicks the Send button */
       public void addToDatabase(View view) {
    	   Intent intent = new Intent(this, AddActivity.class);
			  startActivity(intent);

    	
       }
       
       private void sendPost(String name){
    	   
    	   try {
    	        URL url;
    	        URLConnection urlConnection;
    	        DataOutputStream outStream;
    	        DataInputStream inStream;
    	 
    	        // Build request body
    	        String body =
    	        "name=" + URLEncoder.encode(name, "UTF-8") +
    	        "&barcode=" + URLEncoder.encode(scanContent, "UTF-8");
    	 
    	        // Create connection
    	        url = new URL("http://192.168.1.68/test/POST/post.php");
    	        urlConnection = url.openConnection();
    	        ((HttpURLConnection)urlConnection).setRequestMethod("POST");
    	        urlConnection.setDoInput(true);
    	        urlConnection.setDoOutput(true);
    	        urlConnection.setUseCaches(false);
    	        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    	        urlConnection.setRequestProperty("Content-Length", ""+ body.length());
    	 
    	        // Create I/O streams
    	        outStream = new DataOutputStream(urlConnection.getOutputStream());
    	        inStream = new DataInputStream(urlConnection.getInputStream());
    	 
    	        // Send request
    	        outStream.writeBytes(body);
    	        outStream.flush();
    	        outStream.close();
    	 
    	        // Get Response
    	        // - For debugging purposes only!
    	        String buffer;
    	        while((buffer = inStream.readLine()) != null) {
    	            System.out.println(buffer);
    	        }
    	 
    	        // Close I/O streams
    	        inStream.close();
    	        outStream.close();
    	    }
    	    catch(Exception ex) {
    	        System.out.println("Exception cought:\n"+ ex.toString());
    	    }
    	    
    
   	}
		
		/** Called when the user clicks the Start Quest button */
		public void addItem(View view) {
			  Intent intent = new Intent(this, AddActivity.class);
			  startActivityForResult(intent, 1);
			}
		
		public void onClick(View v){
			//respond to clicks
			if(v.getId()==R.id.add_button){
				//scan
				IntentIntegrator scanIntegrator = new IntentIntegrator(this);
				scanIntegrator.initiateScan();
				}
			
			if (v.getId()==R.id.add_data_button) {
				  EditText editText = (EditText) findViewById(R.id.edit_name);
		    	   String name = editText.getText().toString();
		    	   
		    	   if (scanContent == null) {
		    		   Toast toast = Toast.makeText(getApplicationContext(), 
		    				   "No scan data received!", Toast.LENGTH_SHORT);
		    		   toast.show();
		    		   return;
		    	   }
		    	   try {
					sendPost(name);
				} catch (Exception e) {
					Toast toast = Toast.makeText(getApplicationContext(), 
			 				   "Computer says no", Toast.LENGTH_SHORT);
			 		   toast.show();
				}
					
				
			  }
			}
		
		
		public boolean present (String content) {
		
			  URL url = null;
		      BufferedReader in = null;
		      String inputLine;
		    
		  

		      try {
		          url = new URL("http://epicgamearts.com/itemquest/api.php");
		      } catch (MalformedURLException e) {
		          // TODO Auto-generated catch block
		          e.printStackTrace();
		      }

		      try {
		          in = new BufferedReader(
		          new InputStreamReader(url.openStream()));
		      } catch (IOException e) {
		          // TODO Auto-generated catch block
		          e.printStackTrace();
		      }


		      try {
		          while ((inputLine = in.readLine()) != null) {
		              if ((inputLine.split(",")[1]).equals(scanContent)) {
		            	  return true;
		              }
		              
		          }
		      } catch (IOException e) {
		          // TODO Auto-generated catch block
		          e.printStackTrace();
		      }

		      try {
		          in.close();
		      } catch (IOException e) {
		          // TODO Auto-generated catch block
		          e.printStackTrace();
		      }
		    
		    
			return false;
		}
		
		public void onActivityResult(int requestCode, int resultCode, Intent intent) {
			//retrieve scan result
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if (scanningResult != null) {
				//we have a result
				scanContent = scanningResult.getContents();
				if(present(scanContent)) {
					Toast toast = Toast.makeText(getApplicationContext(), 
							   "Already in database", Toast.LENGTH_SHORT);
					toast.show();
				}
				
		
		    }else{
				   Toast toast = Toast.makeText(getApplicationContext(), 
				   "No scan data received!", Toast.LENGTH_SHORT);
				   toast.show();
		    }
			
			
		}
   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
