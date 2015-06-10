package com.example.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
@SuppressLint("NewApi")
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver implements Runnable,ConnectionInfoListener {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private Activity mActivity;
    private PeerListListener peerListListener;
    private int discoveryInterval;
    Collection <WifiP2pDevice> peerList;
    private static String myMac="";
    public static int PORT = 8988;
    private static boolean server_running = false;
    public static String thisDeviceName="";
    
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Activity activity, int discoveryInterval) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.discoveryInterval = discoveryInterval;
        //Create a new peer list listener
        peerListListener = new WifiDirectPeerListListener();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
       // WifiP2pDevice device2 = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
       // thisDeviceName = device2.deviceName;
        
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        	 if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                 // Wifi P2P is enabled
             } else {
                 // Wi-Fi P2P is not enabled
             }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	mManager.requestPeers ( mChannel , peerListListener );
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	
        	//Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
            // Respond to new connection or disconnections
        	NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.d(Message.TAG,
                        "Connected to p2p network. Requesting network details");
                mManager.requestConnectionInfo(mChannel,
                        (ConnectionInfoListener) mActivity);
            } else {
                // It's a disconnect
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        	WifiP2pDevice device = (WifiP2pDevice) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

        	myMac = device.deviceAddress;
        	thisDeviceName=device.deviceName;
        }
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {

            // Every 'discoveryInterval' millisecs, initiate a discovery
            // Infinite loop
            while ( true ) {

                // DEBUG
                Log.i ( "WifiDirectUdp" , "Initiating WifiDirect peer discovery" );

                // Initiate a discovery of peers
                mManager.discoverPeers ( mChannel , new ActionListener() {
                        // On success to initiate discovery
                        public void onSuccess() {
                            // Nothing to do, we will receive peers in onReceive()
                        }

                        // On failing to initiate discovery
                        public void onFailure ( int reasonCode ) {

                            // Log the reason, we will try discovery again later
                            Log.e ( "WifiDirectUdp" , "Unable to initiate peer discovery: " + 
                                    getReasonString ( reasonCode ) );
                        }
                    } );

                // Sleep for some time before initiating a new discovery
                Thread.sleep ( discoveryInterval );
            }
        }
        catch ( Exception e ) {
            // Log the exception
            Log.e ( "WifiDirectUdp" , "DiscoveryThread: Exception stopping thread" , e );
        }
        finally {

            // IMPORTANT!!
            // Cancel any ongoing P2P group formation
            mManager.cancelConnect ( mChannel , null );

            // Stop any peer discovery
            mManager.stopPeerDiscovery ( mChannel , null );
        }
		
	}
	public Collection<WifiP2pDevice> getPeersList()
	 {
		 return peerList;
	 }
	
	/** Listener for new peers. */
	 class WifiDirectPeerListListener implements PeerListListener {

		    /** Called when a list of peers is available. */
		    public void onPeersAvailable ( WifiP2pDeviceList peers ) {
		
		        // Get a list of discovered peers
		        // IMP: Peers may or may not have WifiDirect or this link layer, this is ok
		        peerList = peers.getDeviceList();
		
		        // Iterate through the peer list, and connect to each one of them
		        // Note: This simply creates a hotspot with the group of mobiles, not an actual socket connection
		        // Note: Once you connect, the discovery process is terminated
		        for ( WifiP2pDevice peer : peerList ) {
		
		            // DEBUG
		            Log.i ( "WifiDirectUdp" , "Discovered peer with MAC address: " + peer.deviceAddress );
		
		            // Create a config (minimally with the device's MAC address)
		            WifiP2pConfig config = new WifiP2pConfig();
		            config.deviceAddress = peer.deviceAddress;
		
		            // Connect ONLY IF this device is available (not connected yet to the group)
		            if ( peer.status != WifiP2pDevice.AVAILABLE )
		                continue;
		
		            // DEBUG
		            Log.i ( "WifiDirectUdp" , "Sending invitation to peer with MAC address: " + peer.deviceAddress );
		
		            // Connect to the peer (include the peer into the group)
		            // IMP: Legacy devices will NOT be connected, unless we EXPLICITLY create a group
		            // IMP: Only devices with WifiDirect ON will be invited (random strangers will not be)
		            mManager.connect ( mChannel , config , new ActionListener() {
		
		                    // On success to initiate connection
		                    public void onSuccess() {
		                        // Nothing to do, we will receive connection status in onReceive()
		                    	/*MainActivity.setText("Connected");
		                    	if (!server_running){
		            				new MainActivity.ServerAsyncTask().execute();
		            				server_running = true;
		            				MainActivity.setText("Starting server async task");
		            			}*/
		                    	//MainActivity.setText("Starting Server");
		                    }
		
		                    // On failure to initiate connection
		                    public void onFailure ( int reasonCode ) {
		
		                        // Log the reason
		                        Log.e ( "WifiDirectUdp" , "Unable to initiate peer connection: " + 
		                                getReasonString ( reasonCode ) );
		                    }
		                } );
		
		            // TODO: Later, we keep track of peers, and connect only to those not in the list?
		            // How to know if a peer left? Take help from Hello thread?
		        }
		    }

 	}
	 /** Helper method to convert reason code to a readable string. */
	 private static String getReasonString ( int reasonCode ) {
	
	     // Convert to a readable string
	     String reasonString = null;
	     if ( reasonCode == WifiP2pManager.P2P_UNSUPPORTED ) 
	         reasonString = "WifiDirect not supported on this mobile";
	     else if ( reasonCode == WifiP2pManager.BUSY )
	         reasonString = "WifiDirect driver is too busy";
	     else if ( reasonCode == WifiP2pManager.ERROR )
	         reasonString = "Internal error in WifiDirect driver";
	     else 
	         reasonString = "Unknown reason";
	
	     // Return it
	     return reasonString;
	 }

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		//Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
		//MainActivity.setText("Connected");
	}
	public static String getMac()
	{
		return myMac;
	}
	public static String getDeviceName()
	{
		return thisDeviceName;
	}
	 
}

