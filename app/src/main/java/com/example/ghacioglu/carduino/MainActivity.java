package com.example.ghacioglu.carduino;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textStatus;

    private static View Tab2;
    private static View Tab1;

    static boolean once = true;
    static boolean isConnected = false;
    static boolean parkLights = false;
    static boolean headLights = false;
    static boolean ign1Status = false;
    static boolean ign2Status = false;
    static boolean engineStatus = false;
    static boolean islocked = false;


    String address = null, name = null;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    static Set<BluetoothDevice> pairedDev;
    static BluetoothAdapter myBlue;
    static BluetoothSocket btSock = null;
    static String namee = null;
    static String carAdress = "98:D3:10:11:AC:4F"; // Car module's mac address

    static volatile boolean stopWorker;
    static int readBufferPosition;
    static byte[] readBuffer;
    static Thread workerThread;
    static InputStream mmInputStream;

    static ImageView unlockButton = null;
    static ImageView lockButton = null;
    static ImageView windowLockButton = null;
    static ImageView parklightButton = null;
    static ImageView headlightButton = null;
    static Context cntx = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setw() throws IOException {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = null;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    Tab1 = rootView;
                    if (once) {
                        try {
                            ImageView imgcarConnected = (ImageView) Tab1.findViewById(R.id.imgcarConnected);
                            ImageView imgcarDisconnected = (ImageView) Tab1.findViewById(R.id.imgcarDisconnected);
                            boolean status = bluetooth_connect_device(getContext());
                            if (status == true) {
                                imgcarConnected.setVisibility(View.VISIBLE);
                                imgcarDisconnected.setVisibility(View.INVISIBLE);
                            } else {
                                imgcarConnected.setVisibility(View.INVISIBLE);
                                imgcarDisconnected.setVisibility(View.VISIBLE);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        once = false;
                    }

                    setUI(Tab1);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_second, container, false);
                    Tab2 = rootView;
                    break;
            }



            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }

    public static void setUI(View tab1) {

        TextView textStatus = (TextView) tab1.findViewById(R.id.txtStatus);

        textStatus.setText("34FHN67"); //Plate number or name of the car

        if (isConnected == true) {

            textStatus.setTextColor(Color.parseColor("#04d60e"));

        } else {
            textStatus.setTextColor(Color.parseColor("#f20014"));
        }

        final ImageView vehicleImage = (ImageView) Tab1.findViewById(R.id.imageView2);
        unlockButton = (ImageView) tab1.findViewById(R.id.imgUnlock);
        lockButton = (ImageView) tab1.findViewById(R.id.imgLock);
        final ImageView windowLockButton = (ImageView) tab1.findViewById(R.id.imgWindowLock);
        final ImageView parklightButton = (ImageView) tab1.findViewById(R.id.imgParklights);
        final ImageView headlightButton = (ImageView) tab1.findViewById(R.id.imgHeadlights);
        final ImageView starterButton = (ImageView) tab1.findViewById(R.id.imgStarter);
        final Context cntx = tab1.getContext();


        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("CMDUNLOCK");      //Unlock command
                Toast.makeText(cntx, "Doors are opening", Toast.LENGTH_SHORT).show();
            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("CMDLOCK");
                Toast.makeText(cntx, "Doors are closing", Toast.LENGTH_SHORT).show();
            }
        });

        windowLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("CMDLOCKANDCLOSEWINDOWS");
                Toast.makeText(cntx, "Windows and doors are closing", Toast.LENGTH_LONG).show();
            }
        });

        parklightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parkLights == false) {
                    sendCommand("CMDPARKLIGHTON");
                    Toast.makeText(cntx, "Parking lights are turning on", Toast.LENGTH_SHORT).show();
                    parkLights = true;
                } else {
                    sendCommand("CMDPARKLIGHTOFF");
                    Toast.makeText(cntx, "Parking lights are turning off", Toast.LENGTH_SHORT).show();
                    parkLights = false;
                }
            }
        });

        headlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headLights == false) {
                    sendCommand("CMDHEADLIGHTON");
                    vehicleImage.setImageResource(R.drawable.e34lon);
                    Toast.makeText(cntx, "Headlignts are turning on", Toast.LENGTH_SHORT).show();
                    headLights = true;
                } else {
                    sendCommand("CMDHEADLIGHTOFF");
                    vehicleImage.setImageResource(R.drawable.e34loff);
                    Toast.makeText(cntx, "Headlights are turning off", Toast.LENGTH_SHORT).show();
                    headLights = false;
                }
            }
        });

        starterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(engineStatus == false){

                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    if(isConnected == true) {
                                        sendCommand("CMDSTARTENGINE");
                                        engineStatus = true;
                                        Toast.makeText(cntx, "Engine is turning on", Toast.LENGTH_SHORT).show();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;

                                case DialogInterface.BUTTON_NEUTRAL:

                                    if(ign1Status == false || ign2Status == false){
                                        sendCommand("CMDIGNALLON");
                                        ign1Status=true;
                                        ign2Status=true;
                                        Toast.makeText(cntx, "Ignition On", Toast.LENGTH_SHORT).show();
                                    }else{
                                        sendCommand("CMDIGNALLOFF");
                                        ign1Status=false;
                                        ign2Status=false;
                                        engineStatus =false;
                                        Toast.makeText(cntx, "Ignition Off", Toast.LENGTH_SHORT).show();
                                    }
                                    break;

                            }
                        }
                    }
                };

                if(engineStatus == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(cntx);
                    builder.setMessage("Are you sure you want to run the engine").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).setNeutralButton("Inginition", dialogClickListener).show();
                }

                if(engineStatus==true){
                    sendCommand("CMDIGNALLOFF");
                    ign1Status=false;
                    ign2Status=false;
                    engineStatus =false;
                    Toast.makeText(cntx, "Engine is turning off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void processReceived(String data){

        if(data.contains("{")){

            try {
                JSONObject jObj = new JSONObject(data);
                parkLights = jObj.getBoolean("parklightStatus");
                headLights = jObj.getBoolean("headlightStatus");
                if(headLights == true)
                {
                    //resimsetle
                }
                islocked = jObj.getBoolean("lockStatus");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static Boolean bluetooth_connect_device(Context context) throws IOException {
        try {
            myBlue = BluetoothAdapter.getDefaultAdapter();
            pairedDev = myBlue.getBondedDevices();

        } catch (Exception we) {
        }

        myBlue = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo = myBlue.getRemoteDevice(carAdress);//connects to the device's address and checks if it's available
        try {
            btSock = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            btSock.connect();
            BluetoothDevice connectedDevice = btSock.getRemoteDevice();
            if (connectedDevice.getAddress() == carAdress) {
                isConnected = true;
                beginListenForData(context);
                Toast.makeText(context, "Connected to Car", Toast.LENGTH_LONG).show();
                sendCommand("GETCARSTATUS");
            } else {
                isConnected = false;
                Toast.makeText(context, "Couldn't connect to car", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isConnected;
    }

    public void onClick(View v) {
        try {

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void sendCommand(String msg) {
        try {
            if (btSock != null) {
                btSock.getOutputStream().write(msg.toString().getBytes());
            }
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void beginListenForData(final Context ctx) {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        if (btSock != null) {
                            mmInputStream = btSock.getInputStream();

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                processReceived(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }
}
