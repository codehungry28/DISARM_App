package com.example.testapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomMessage extends Activity {
	DataBase dbase=new DataBase(this); //for setting different menu items depending on disaster type
	Spinner require,oxy,med;
	EditText nrofdoc,nrofam,othermed;
	TextView docreq,amreq,oxyreq,medreq;
	String requirement=null;
	Button ok;
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_message);
		require=(Spinner)findViewById(R.id.require_menu);
		oxy=(Spinner)findViewById(R.id.oxygen_require_menu);
		med=(Spinner)findViewById(R.id.medicine_require_menu);
		addItemsOnSpinnerRequirement();
		addItemsOnSpinnerOxygen();
		addItemsOnSpinnerMedicine();
		
		nrofdoc=(EditText)findViewById(R.id.doctor_number_text);
		nrofam=(EditText)findViewById(R.id.ambulance_number_text);
		othermed=(EditText)findViewById(R.id.medicine_other_text);
		
		docreq=(TextView)findViewById(R.id.doctor_require_text);
		amreq=(TextView)findViewById(R.id.ambulance_require_text);
		oxyreq=(TextView)findViewById(R.id.oxygen_require_text);
		medreq=(TextView)findViewById(R.id.medicine_require_text);
		
		ok=(Button)findViewById(R.id.advance_ok);
		
		oxy.setVisibility(View.GONE);
		med.setVisibility(View.GONE);
		
		//nrofdoc.setVisibility(View.GONE);
		nrofam.setVisibility(View.GONE);
		othermed.setVisibility(View.GONE);
		
		//docreq.setVisibility(View.GONE);
		amreq.setVisibility(View.GONE);
		oxyreq.setVisibility(View.GONE);
		medreq.setVisibility(View.GONE);
		
		require.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                    		if(require.getSelectedItem().toString().equals("Doctor"))
                    		{
                    			nrofdoc.setVisibility(View.VISIBLE);
                    			docreq.setVisibility(View.VISIBLE);
                    			
                    			oxy.setVisibility(View.GONE);
                    			med.setVisibility(View.GONE);
                    			
                    			nrofam.setVisibility(View.GONE);
                    			othermed.setVisibility(View.GONE);
                    			
                    			amreq.setVisibility(View.GONE);
                    			oxyreq.setVisibility(View.GONE);
                    			medreq.setVisibility(View.GONE);
                    		}
                    		else if(require.getSelectedItem().toString().equals("Ambulance"))
                    		{
                    			nrofam.setVisibility(View.VISIBLE);
                    			amreq.setVisibility(View.VISIBLE);
                    			oxy.setVisibility(View.VISIBLE);
                    			oxyreq.setVisibility(View.VISIBLE);
                    			
                    			med.setVisibility(View.GONE);
                    			
                    			nrofdoc.setVisibility(View.GONE);
                    			othermed.setVisibility(View.GONE);
                    			
                    			docreq.setVisibility(View.GONE);
                    			medreq.setVisibility(View.GONE);
                    		}
                    		else if(require.getSelectedItem().toString().equals("Medicine"))
                    		{
                    			med.setVisibility(View.VISIBLE);
                    			medreq.setVisibility(View.VISIBLE);
                    			
                    			oxy.setVisibility(View.GONE);
                    			
                    			nrofdoc.setVisibility(View.GONE);
                    			nrofam.setVisibility(View.GONE);
                    			othermed.setVisibility(View.GONE);
                    			
                    			docreq.setVisibility(View.GONE);
                    			amreq.setVisibility(View.GONE);
                    			oxyreq.setVisibility(View.GONE);
                    		}
                    		else if(require.getSelectedItem().toString().equals("Food"))
                    		{
                    			oxy.setVisibility(View.GONE);
                    			med.setVisibility(View.GONE);
                    			
                    			nrofdoc.setVisibility(View.GONE);
                    			nrofam.setVisibility(View.GONE);
                    			othermed.setVisibility(View.GONE);
                    			
                    			docreq.setVisibility(View.GONE);
                    			amreq.setVisibility(View.GONE);
                    			oxyreq.setVisibility(View.GONE);
                    			medreq.setVisibility(View.GONE);
                    		}
                    		else if(require.getSelectedItem().toString().equals("Clothes"))
                    		{
                    			oxy.setVisibility(View.GONE);
                    			med.setVisibility(View.GONE);
                    			
                    			nrofdoc.setVisibility(View.GONE);
                    			nrofam.setVisibility(View.GONE);
                    			othermed.setVisibility(View.GONE);
                    			
                    			docreq.setVisibility(View.GONE);
                    			amreq.setVisibility(View.GONE);
                    			oxyreq.setVisibility(View.GONE);
                    			medreq.setVisibility(View.GONE);
                    		}
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        
                    }
                });
		
		med.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                    		if(med.getSelectedItem().toString().equals("Others"))
                    		{
                    			othermed.setVisibility(View.VISIBLE);
                    		}
                    		else
                    		{
                    			othermed.setVisibility(View.GONE);
                    		}
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        
                    }
                });
		 ok.setOnClickListener(new OnClickListener(){
			   public void onClick(View v)
			   {
				   //Toast.makeText(getApplicationContext(), text,
						   //Toast.LENGTH_LONG).show();
				   //System.out.println(text);
				   //setSenderLocation();
				   setCustomMessage();
			   }
		   });
                    
	}
	public void addItemsOnSpinnerRequirement() 
	{
		List<String> requirement_list = new ArrayList<String>();
		requirement_list.add("Doctor");
		requirement_list.add("Ambulance");
		requirement_list.add("Medicine");
		requirement_list.add("Food");
		requirement_list.add("Clothes");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item,requirement_list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		require.setAdapter(dataAdapter);
	}
	public void addItemsOnSpinnerOxygen() 
	{
		List<String> oxy_requirement_list = new ArrayList<String>();
		oxy_requirement_list.add("Yes");
		oxy_requirement_list.add("No");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item,oxy_requirement_list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		oxy.setAdapter(dataAdapter);
	}
	public void addItemsOnSpinnerMedicine() 
	{
		List<String> med_requirement_list = new ArrayList<String>();
		med_requirement_list.add("First Aid");
		med_requirement_list.add("Food Poisoning");
		med_requirement_list.add("Others");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item,med_requirement_list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		med.setAdapter(dataAdapter);
	}
	private void setCustomMessage() 
	{
		// TODO Auto-generated method stub
		if(require.getSelectedItem().toString().equals("Doctor"))
		{
			requirement="Requirement: "+require.getSelectedItem().toString()+"\nNo. of doctors: "+nrofdoc.getText().toString()+"\n";
		}
		else if(require.getSelectedItem().toString().equals("Ambulance"))
		{
			if(oxy.getSelectedItem().toString().equals("No"))
			{
				requirement="Requirement: "+require.getSelectedItem().toString()+"\nNo. of ambulance: "+nrofam.getText().toString()+"\n";
			}
			else
			{
				requirement="Requirement: "+require.getSelectedItem().toString()+"\nNo. of ambulance: "+nrofam.getText().toString()+"\nWith oxygen\n";
			}
		}
		else if(require.getSelectedItem().toString().equals("Medicine"))
		{
			if(med.getSelectedItem().toString().equals("Others"))
			{
				requirement="Requirement: Medicine\nMedicine for: "+othermed.getText().toString()+"\n";
			}
			else
			{
				requirement="Requirement: "+require.getSelectedItem().toString()+"\nMedicine for: "+med.getSelectedItem().toString()+"\n";
			}
		}
		Intent intent = new Intent();
		intent.putExtra("edittextvalue",requirement);
		setResult(RESULT_OK, intent);        
		finish();
	}
	public void onBackPressed() {
		setCustomMessage() ;
	    
	}

}
