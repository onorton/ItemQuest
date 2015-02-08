package com.epicgamearts.itemquest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends Activity implements OnClickListener {

	private Button scanBtn;
	private TextView contentTxt, scoreTxt, scannedTxt, itemTxt;
	private String toFind, barcode, currentItem;
	private int itemsScanned = 0;
	private int score;
	
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		Intent intent = getIntent();
	    currentItem = intent.getStringExtra("CURRENTITEM");
		scanBtn = (Button)findViewById(R.id.scan_button);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		setScore();
		setItemsScanned();
		setItemName();
		
		scanBtn.setOnClickListener(this);
		
	}
	
   private void checkMatch(String testCode) {
	   if (testCode != null) {
	   if (testCode.equals(toFind)) {
		   score++;
		   itemsScanned++;
		   setScore();
		   setItemsScanned();
		   setItemName();
	   }
	   }
	   	   
   }

   private void setScore() {
	   scoreTxt= (TextView)findViewById(R.id.score_text);
	    scoreTxt.setText(String.valueOf(score));
   }
   
   private void setItemName()  {
	    itemTxt= (TextView)findViewById(R.id.item_name);
	    if (currentItem == null)
	    	itemTxt.setText(getRandItem());	
	    else
	        itemTxt.setText((CharSequence) currentItem);
	    
   }
   
   private String getRandItem() {
	  List<String> items = new ArrayList<String>();
	  URL url = null;
      BufferedReader in = null;
      String inputLine;
      Random r = new Random();

      String text;

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
              items.add(inputLine);
              
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
      
      int i = r.nextInt(items.size());
      text = items.get(i);
      toFind = text.split(",")[1];
	 return text.split(",")[0];
	
   }

   
   private void setItemsScanned() {
	    scannedTxt= (TextView)findViewById(R.id.items_scanned);
	    scannedTxt.setText(String.valueOf(score));
   }
	
	
	/** Called when the user clicks the Start Quest button */
	public void scanItem(View view) {
		  Intent intent = new Intent(this, ScanActivity.class);
		  startActivityForResult(intent, 1);
		}
	
	public void onClick(View v){
		//respond to clicks
		if(v.getId()==R.id.scan_button){
			//scan
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			}
		}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();
			checkMatch(scanContent);
	
	    }else{
			   Toast toast = Toast.makeText(getApplicationContext(), 
			   "No scan data received!", Toast.LENGTH_SHORT);
			   toast.show();
	    }
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
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
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("CURRENTITEM",currentItem);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	
	}
	


	
	
}
