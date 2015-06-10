package com.example.testapp;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import nus.dtn.util.DtnMessage;
import nus.dtn.util.Descriptor;
import nus.dtn.api.fwdlayer.ForwardingLayerProxy;
import nus.dtn.api.fwdlayer.ForwardingLayerInterface;
import nus.dtn.api.fwdlayer.MessageListener;
import nus.dtn.middleware.api.DtnMiddlewareInterface;
import nus.dtn.middleware.api.DtnMiddlewareProxy;
import nus.dtn.middleware.api.MiddlewareEvent;
import nus.dtn.middleware.api.MiddlewareListener;

public class Message extends Activity implements OnClickListener,ConnectionInfoListener,Handler.Callback,MessageTarget{

	
	
	
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;

	IntentFilter mIntentFilter;
	
	private Thread discoveryThread;
	
	WifiP2pInfo go;
	private WifiP2pDevice device;
	    
	
	EditText editText1;
	TextView tv1;
    ImageView imageAmbulance, imageFood, imageCloth, imageMedicine, imageWater;
    Button send,advance,msg;
    DataBase dbase=new DataBase(this);
    //String sender;
    public static int PORT = 4545;
	private static boolean server_running = false;
	
	public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    public static final String TAG = "wifidirectdemo";
    private Handler handler = new Handler(this);
    private ChatManager chatManager;
    
    public static boolean isConn=false;
    
    private List<String> Messages;
    private List<String> tempMessages;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_home);
		
		editText1 = (EditText) findViewById(R.id.message_text);
        imageAmbulance = (ImageView) findViewById(R.id.imageAmbulance);
        imageFood = (ImageView) findViewById(R.id.imageFood);
        imageCloth = (ImageView) findViewById(R.id.imageCloth);
        imageMedicine = (ImageView) findViewById(R.id.imageMedicine);
        imageWater = (ImageView) findViewById(R.id.imageWater);
        send=(Button) findViewById(R.id.send);
        tv1=(TextView)findViewById(R.id.show_message);
        advance=(Button)findViewById(R.id.anvanced);
        msg=(Button)findViewById(R.id.messages);
        //final String text = editText1.getText().toString();
        send.setOnClickListener(this);

        //Typeface font= Typeface.createFromAsset(getAssets(), "DejaVuSans.tff");
        //editText1.setTypeface(font); 
        tempMessages=new ArrayList<String>();
        Messages=new ArrayList<String>();
		//Turn on wifi not wifi direct
		WifiManager wifi = (WifiManager) getSystemService ( Context.WIFI_SERVICE );
        if ( ! wifi.isWifiEnabled() ) {
            wifi.setWifiEnabled ( true );
        }
		
        //Turn on wifi direct
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this,20000);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		   
		 // Create a discovery thread
	    discoveryThread = new Thread((Runnable) mReceiver);
	    discoveryThread.start();
        
        
        
        imageAmbulance.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	editText1.append("\uD83D\uDE91");
	            }
	    });
	    imageFood.setOnClickListener(new OnClickListener() {
	          @Override
	           public void onClick(View v) {
	        	  	editText1.append("\uD83C\uDF54");
	          }
	    });
	    imageCloth.setOnClickListener(new OnClickListener() {
	           @Override
	           public void onClick(View v) {
	        	   editText1.append("\uD83D\uDC55");
	          }
	   });
	   imageMedicine.setOnClickListener(new OnClickListener() {
	           @Override
	            public void onClick(View v) {
	        	   editText1.append("\uD83D\uDC8A");
	           }
	   });
	   imageWater.setOnClickListener(new OnClickListener() {
	           @Override
	           public void onClick(View v) {
	        	   editText1.append("\uD83D\uDEB0");
	           }
	   });
	   advance.setOnClickListener(new OnClickListener(){
		   public void onClick(View v)
		   {
			   //Toast.makeText(getApplicationContext(), text,
					   //Toast.LENGTH_LONG).show();
			   //System.out.println(text);
			   //setSenderLocation();
			   Intent i=new Intent(Message.this,CustomMessage.class);
			   startActivityForResult(i, 1);
		   }
	   });
	   msg.setOnClickListener(new OnClickListener(){
		   public void onClick(View v)
		   {
			   	tv1.setText("");
				for(String m:Messages)
				{
					tv1.append(m+"\n");
				}
		   }
		   });
	   
	   
	}
	 public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle presses on the action bar items
		    switch (item.getItemId()) {
		        	case R.id.new_registration:
		        		dbase.deleteDetails();
		        		Intent i=new Intent(Message.this,MainActivity.class);
		 			   	startActivity(i);
		 			   	this.finish();
		        		return true;
		        	default:
		        	return super.onOptionsItemSelected(item);

		    }
			
		}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.message_actions, menu);
	 
	       return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onResume() {
	    super.onResume();
	    registerReceiver(mReceiver, mIntentFilter);
	}
	/* unregister the broadcast receiver */
	//@Override
	protected void onPause() {
	    super.onPause();
	    unregisterReceiver(mReceiver);
	}
	public void onBackPressed() {
	    this.finish();
	    
	}
	private void setSenderLocation() {
		// TODO Auto-generated method stub
		
	}
	private String setSender() {
		// TODO Auto-generated method stub
		TelephonyManager telephonyManager = 
                (TelephonyManager) getSystemService (TELEPHONY_SERVICE);
		String devId=telephonyManager.getDeviceId().toString();
		String worker=dbase.getUser().toString();
		String sender=worker+"-"+devId;
		return sender;
		
		//tv1.setText("Sender     : "+sender+"\nLocation  : abc");
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{

        if (requestCode == 1) 
        {
             if(resultCode == RESULT_OK)
             {
            	 String customMessage=data.getStringExtra("edittextvalue");
            	 editText1.getText().clear();
            	 editText1.append(customMessage);
             }
        }
    } 
	
	//from nus dtn start
	
	protected void onDestroy() {
        super.onDestroy();

        try {
            // Stop the middleware
            // Note: This automatically stops the API proxies, and releases descriptors/listeners
            
        }
        catch ( Exception e ) {
            // Log the exception
            Log.e ( "BroadcastApp" , "Exception on stopping middleware" , e );
            // Inform the user
        }
    }
	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}
	public void setHandler(Handler handler) {
        this.handler = handler;
    }
	@Override
	public boolean handleMessage(android.os.Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
        case MESSAGE_READ:
            byte[] readBuf = (byte[]) msg.obj;
            // construct a string from the valid bytes in the buffer
            String readMessage = new String(readBuf, 0, msg.arg1);
            Log.d(TAG, readMessage);
            //(chatFragment).pushMessage("Buddy: " + readMessage);
            /*String[] msgs = readMessage.split("**");
            for(String mg:msgs)
            {
            	Messages.add(mg);
            }*/
            //Messages.addAll(tempMessages);
            int c=0;
            tempMessages.clear();
            StringTokenizer mg = new StringTokenizer(readMessage, "**");
            while(mg.hasMoreTokens())
            {
            	if(!Messages.isEmpty())
            	{
            		for(String str:Messages)
            			tempMessages.add(str);
            	}
            	String st=mg.nextToken();
            	if(tempMessages.isEmpty())
            		Messages.add(st);
            	else
            	{
            		c=0;
            		for(String s:tempMessages)
            		{
            			if(s.equals(st))
            				c++;
            		}
            		if(c==0)
            			Messages.add(st);
            	}
            	tempMessages.clear();
            }
            
            //MainActivity.setText(readMessage);
            Toast.makeText(getApplicationContext(), "Receiving Message", Toast.LENGTH_LONG).show();
            break;

        case MY_HANDLE:
            Object obj = msg.obj;
            (this).setChatManager((ChatManager) obj);

		}
		return true;
	}
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Starting server"+p2pInfo.groupOwnerAddress.getHostAddress().toString(), Toast.LENGTH_LONG).show();
		Thread handler = null;
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */

        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler(((MessageTarget) this).getHandler());
                handler.start();
                Toast.makeText(getApplicationContext(), "Connected as grp owner", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(((MessageTarget) this).getHandler(),p2pInfo.groupOwnerAddress);
            handler.start();
            Toast.makeText(getApplicationContext(), "Connected as peer", Toast.LENGTH_LONG).show();
        }
        //new Thread(new checkConnection()).start();
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (chatManager != null) 
		{
			String mess="";
			if(!editText1.getText().toString().equals(""))
			{	
				//mess=mess.concat(msg.getText().toString()+"**");
				//MainActivity.setText(mess);
				Messages.add(setSender()+"-"+editText1.getText().toString());
				editText1.setText("");
				//chatManager.write(msg.getText().toString().getBytes());
				//chatManager.write(mess.toString().getBytes());
				Toast.makeText(getApplicationContext(), "message saved", Toast.LENGTH_LONG).show();
			}
			if(!Messages.isEmpty())
			{
				for(String m:Messages)
				{
					mess=mess.concat(m+"**");
					//chatManager.write(m.getBytes());
					//MainActivity.setText(m);
				}
				
				Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "no message in buffer", Toast.LENGTH_LONG).show();
			}
			if(!mess.equals(""))
			{
				chatManager.write(mess.toString().getBytes());
				mess="";
			}
		}
		else
		{
			if(!editText1.getText().toString().equals(""))
			{
				Messages.add(setSender()+"-"+editText1.getText().toString());
				editText1.setText("");
				Toast.makeText(getApplicationContext(), "message saved", Toast.LENGTH_LONG).show();
			}
		}
	}
	public void setChatManager(ChatManager obj) {
        chatManager = obj;
    }
    
	
	
      
}
