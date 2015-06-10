package com.example.testapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	Spinner dis_type;
	Spinner w_type;
	Button btn_reg;
	EditText other;
	DataBase dbase=new DataBase(this);
	String dt=null,wt=null;
	public static Activity ac;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ac=this;
		if(dbase.ifExists())
		{
			ac.finish();
			Intent i=new Intent(MainActivity.this,Message.class);
			startActivity(i);
		}
		else
		{
			btn_reg = (Button)findViewById(R.id.register);
			dis_type = (Spinner) findViewById(R.id.dis_menu);
			w_type=(Spinner) findViewById(R.id.worker_menu);
			other=(EditText) findViewById(R.id.other_worker);
			other.setVisibility(View.GONE);
			addItemsOnSpinnerWorker();
			
			//String msg="Disaster is "+dt+" Worker type is "+wt;
			OnClickListener oclBtnReg = new OnClickListener() {
			       @Override
			       public void onClick(View v) {
			         // change text of the TextView (tvOut)  
			    	 //Intent i=new Intent(MainActivity.this,Message.class);
			    	 //startActivity(i);
			    	   dt=dis_type.getSelectedItem().toString();
					   wt=w_type.getSelectedItem().toString();
						
			    	   if(wt.equals("Others"))
			    	   {
			    		   wt=other.getText().toString();
			    	   }
			    	   if(wt.equals("Mention here")||wt.equals(""))
			    	   {
			    		   Toast.makeText(getApplicationContext(), "Please mention worker type",
			    				   Toast.LENGTH_LONG).show();
			    	   }
			    	   else
			    	   {
				    	   try
				    	   {
				    		   
					    	   dbase.addDetails(dt, wt);
					    	   Toast.makeText(getApplicationContext(), "Data inserted/table created",
				    				   Toast.LENGTH_LONG).show();
					    	   MainActivity.ac.finish();
					    	   Intent i=new Intent(MainActivity.this,Message.class);
					    	   startActivity(i);
				    	   }
				    	   catch(SQLiteException e)
				    	   {
				    		   Toast.makeText(getApplicationContext(), "Data not inserted/table not created",
				    				   Toast.LENGTH_LONG).show();
				    	   }
			    	   }
			       }
			     };
			btn_reg.setOnClickListener(oclBtnReg);
			
			w_type.setOnItemSelectedListener(
	                new AdapterView.OnItemSelectedListener() {
	                    public void onItemSelected(
	                            AdapterView<?> parent, View view, int position, long id) {
	                    		if(w_type.getSelectedItem().toString().equals("Others"))
	                    		{
	                    			other.setVisibility(View.VISIBLE);
	                    		}
	                    		else
	                    		{
	                    			other.setVisibility(View.GONE);
	                    		}
	                    		
	                    }

	                    public void onNothingSelected(AdapterView<?> parent) {
	                        
	                    }
	                });
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	public void addItemsOnSpinnerWorker() 
	{
		List<String> list = new ArrayList<String>();
		list.add("Medical");
		list.add("Police");
		list.add("Rescue");
		list.add("Others");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		w_type.setAdapter(dataAdapter);
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	 @Override
	    public void onStart(){
	        super.onStart();
	        Log.d("TAG", "onStart");
	        }
	    
	
	 @Override
	 public void onPause(){
		 super.onStart();
	     Log.d("TAG", "onResume");
	 }
	 protected void onStop() {
	        // TODO Auto-generated method stub
	        super.onStop();
	 }
	/* public void onDestroy()
	 {
		 unbindDrawables(findViewById(R.id.root_view));
		 System.gc();
	 }
	 private void unbindDrawables(View view) {
	        if (view.getBackground() != null) {
	        view.getBackground().setCallback(null);
	        }
	        if (view instanceof ViewGroup) {
	            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            unbindDrawables(((ViewGroup) view).getChildAt(i));
	            }
	        ((ViewGroup) view).removeAllViews();
	        }
	    }*/
}
