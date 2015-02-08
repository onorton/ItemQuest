package com.epicgamearts.itemquest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends Activity implements OnClickListener {
	
	

	private Button scanBtn;
	private TextView scoreTxt, scannedTxt, itemTxt;
	private String toFind, barcode, currentItem;
	private int itemsScanned = 0;
	private int score;
	private String filename = "data.txt";
    File file = new File(filename);
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		Intent intent = getIntent();
		scanBtn = (Button)findViewById(R.id.scan_button);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
	    
		
		String text = readFromFile();
		try {
	    itemsScanned = Integer.parseInt(text.split(";")[0]);
	    score =Integer.parseInt(text.split(";")[1]);
		currentItem = text.split(";")[2];
		toFind = text.split(";")[3];
		
		
		} catch (Exception e) {
		
			
		}
		
		setScore();
		setItemsScanned();
		setItemName();
		setImage();
		
		scanBtn.setOnClickListener(this);
		
	}
	
   private void checkMatch(String testCode) {
	   if (testCode != null) {
	   if (testCode.equals(toFind)) {
		   score++;
		   itemsScanned++;
		   setScore();
		   setItemsScanned();
		   currentItem = null;
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
      currentItem = text.split(",")[0];
      setImage();
	 return currentItem;
	
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
		
		saveStuff();
		super.onBackPressed();
	
	}
	

	public void saveStuff() {
		String textToSave = score + ";" + itemsScanned + ";" + currentItem + ";" + toFind;
		 writeToFile(textToSave);
	}
	
	@Override
	public void onStop() {
		saveStuff();
		super.onStop();
	}
	
	private void writeToFile(String data) { 
	        
	         try {

	              // open myfilename.txt for writing
	              OutputStreamWriter out=new OutputStreamWriter(openFileOutput("data.txt",MODE_APPEND));
	              // write the contents to the file

	              
	            
	               out.write(data);
	               out.write('\n');
 	              // close the file

	              out.close();

	            //  Toast.makeText(this,"Text Saved !",Toast.LENGTH_LONG).show();
	            } catch (java.io.IOException e) {

	                //do something if an IOException occurs.
	  Toast.makeText(this,"Sorry Text could't be added",Toast.LENGTH_LONG).show();


	              }
    }
	
	
	private void setImage() {
		       
		ImageView image = (ImageView) findViewById(R.id.item_image);
		 Drawable drawable = LoadImageFromWebOperations("http://epicgamearts.com/itemquest/images/" + toFind + ".png");

		image.setImageDrawable(drawable);
		  

		
	}
		    
 private Drawable LoadImageFromWebOperations(String url)
  {
	try{
		    InputStream is = (InputStream) new URL(url).getContent();
		    Drawable d = Drawable.createFromStream(is, "src name");
		    return d;
	}catch (Exception e) {
		Toast.makeText(this,"Sorry, could't find image",Toast.LENGTH_LONG).show();
		 return null;
	}
		 
	}

 
    private String readFromFile() {
    	 StringBuilder text = new StringBuilder();
    	    
    	    
         try {
                // open the file for reading we have to surround it with a try
            
                InputStream instream = openFileInput("data.txt");//open the text file for reading
                
                // if file the available for reading
                if (instream != null) {                
                    
                  // prepare the file for reading
                  InputStreamReader inputreader = new InputStreamReader(instream);
                  BufferedReader buffreader = new BufferedReader(inputreader);
                  String line=null;
                  //We initialize a string "line" 
                //  Toast.makeText(this,"Awwshiit",Toast.LENGTH_LONG).show();
        
                    //buffered reader reads only one line at a time, hence we give a while loop to read all till the text is null
                  String lastLine = "";

                  while ((line= buffreader.readLine()) != null) 
                  {
                    
                      lastLine = line;
                  }
    
                		 text.append(lastLine);  
                     
                              
                      
                  }}
                    
                 //now we have to surround it with a catch statement for exceptions
                catch (IOException e) {
                    e.printStackTrace();
                }
        
                 return text.toString();
            }
}