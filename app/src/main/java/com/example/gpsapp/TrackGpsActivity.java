package com.example.gpsapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.gpsapp.consts.Constants;
import com.example.gpsapp.model.ActivityRecognized;
import com.example.gpsapp.model.Configuration;
import com.example.gpsapp.service.BackgroundDetectedActivitiesService;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;


public class TrackGpsActivity extends AppCompatActivity {

    private static final String TAG = TrackGpsActivity.class.getSimpleName();
    //geofence variables
    BroadcastReceiver broadcastReceiver;
    private String activityText;

    //gps location variables
    private TextView configurationTextView;
    private LocationManager locationManager;
    private ListView listView;
    private List<String> items;
    private List<ActivityRecognized> activityList;
    private ArrayAdapter<String> adapter;
    LocationListener locationListener;

    private Configuration configuration;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        configuration = (Configuration) getIntent().getSerializableExtra("configuration");
        setContentView(R.layout.track_gps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkPermissions();
    }

    private void checkPermissions() {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }else {
                initApp();
            }
        } else {
            initApp();
        }

    }

    private void init() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        listView = findViewById(R.id.list);
        configurationTextView = findViewById(R.id.configurationTextView);
        configurationTextView.setText(configuration.getName());
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adapter);
        activityList = new ArrayList<>();

    }

    private void initApp() {
        init();
        startTrackingActivity();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //TODO insttead show send to server
                items.add("Location changed: " + location.getLatitude()  + " | " + location.getLongitude()) ;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                if(s.equals(LocationManager.GPS_PROVIDER)) {
                    items.add("GPS turned on");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onProviderDisabled(String s) {
                if(s.equals(LocationManager.GPS_PROVIDER)) {
                    items.add("GPS turned off");
                    adapter.notifyDataSetChanged();
                }
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1,locationListener);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    adapter.notifyDataSetChanged();
                    int type = intent.getIntExtra("type",-1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type,confidence);
                }
            }
        };


    }

    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
        items.add("Registered : " + Constants.BROADCAST_DETECTED_ACTIVITY);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        items.add("UnRegistered : " + Constants.BROADCAST_DETECTED_ACTIVITY);
        adapter.notifyDataSetChanged();
    }

    private void startTrackingActivity(){
        Intent intent = new Intent(TrackGpsActivity.this, BackgroundDetectedActivitiesService.class);
        items.add("Started service: " + BackgroundDetectedActivitiesService.class.getName() );
        startService(intent);
    }

    private void stopTrackingActivity(){
        Intent intent = new Intent(TrackGpsActivity.this, BackgroundDetectedActivitiesService.class);
        items.add("Stopped service: " + BackgroundDetectedActivitiesService.class.getName() );
        stopService(intent);
    }


    private void handleUserActivity(int type, int confidence) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                activityText = "IN_VEHICLE";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                activityText = "ON_BICECYCLE";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                activityText = "ON_FOOT";
                break;
            }
            case DetectedActivity.RUNNING: {
                activityText = "RUNNING";
                break;
            }
            case DetectedActivity.STILL: {
                activityText = "STILL";
                break;
            }
            case DetectedActivity.TILTING: {
                activityText = "TILTING";
                break;
            }
            case DetectedActivity.WALKING: {
                activityText = "WALKING";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                activityText = "UNKNOWN";
                break;
            }

        }
        if (confidence > Constants.CONFIDENCE) {
            addActivityToLog(activityText, confidence);
        }
    }


    private void addActivityToLog(String activityText, int confidence) {

        if (activityList.size() > 0) {
            ActivityRecognized activityRecognized = activityList.get(activityList.size() - 1);
            if (!activityRecognized.getActivityName().equals(activityText)) {
                activityList.add(new ActivityRecognized(activityRecognized.getActivityName(), "EXIT"));
                items.add("Activity: " + activityRecognized.getActivityName() + " ->  EXIT " +  confidence);
                adapter.notifyDataSetChanged();

                activityList.add(new ActivityRecognized(activityText,"ENTER"));
                items.add("Activity: " + activityText + " -> ENTER " +  confidence);
                adapter.notifyDataSetChanged();
            }
        } else {
            activityList.add(new ActivityRecognized(activityText, "ENTER"));
            items.add("Activity: " + activityText + " -> ENTER " +  confidence);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initApp();
                } else {
                    Toast.makeText(getApplicationContext(),"NOT GRANTED! ",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void clearLog(View view) {
        if (items!= null && items.size()>0) {
            items.clear();
            adapter.notifyDataSetChanged();
        }
    }

    public void stopTracking(View view) {
        locationManager.removeUpdates(locationListener);
        stopTrackingActivity();
        items.add("STOPPED");
        adapter.notifyDataSetChanged();
    }


    public void changeConfiguration(View view) {
        stopTracking(view);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
