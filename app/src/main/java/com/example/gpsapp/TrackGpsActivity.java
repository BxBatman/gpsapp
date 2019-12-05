package com.example.gpsapp;

import android.Manifest;
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
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.gpsapp.http.HttpApi;
import com.example.gpsapp.http.RetrofitClientInstance;
import com.example.gpsapp.service.BackgroundDetectedActivitiesService;
import com.example.models.ActivityRecognized;
import com.example.models.ConfigurationDto;
import com.example.models.LocationDto;
import com.google.android.gms.location.DetectedActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    long minutesInMilliseconds;
    HttpApi api;

    private ConfigurationDto configurationDto;
    private boolean updatesActive = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        configurationDto = (ConfigurationDto) getIntent().getSerializableExtra("configuration");
        setContentView(R.layout.track_gps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkPermissions();
    }

    private void checkPermissions() {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhaveLocationPermission()) {
                requestForLocationPermission();
            }else if(!checkIfAlreadyhaveInternetPermission()){
                requestForInternetPermission();
            }else {
                initApp();
            }
        } else {
            initApp();
        }

    }

    private void init() {
        api = RetrofitClientInstance.getRetrofitInstance().create(HttpApi.class);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        listView = findViewById(R.id.list);
        configurationTextView = findViewById(R.id.configurationTextView);
        configurationTextView.setText(configurationDto.getName());
        minutesInMilliseconds = configurationDto.getTimeIntervalInMinutes() * 60000;
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
               sendLocation(createLocationDto(location));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                if(s.equals(LocationManager.GPS_PROVIDER)) {
                    items.add( getTime()+" GPS turned on");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onProviderDisabled(String s) {
                if(s.equals(LocationManager.GPS_PROVIDER)) {
                    items.add(getTime() +" GPS turned off");
                    adapter.notifyDataSetChanged();
                }
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1,locationListener);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minutesInMilliseconds,1,locationListener);
        updatesActive = true;

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

    private String getTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:ss");
        return df.format(c);
    }

    private void sendLocation(LocationDto locationDto) {
        Log.d(TAG, "Sending location");
        api.saveLocation(locationDto).enqueue(new Callback<LocationDto>() {
            @Override
            public void onResponse(Call<LocationDto> call, Response<LocationDto> response) {
                items.add(getTime()+" LocationDto send") ;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<LocationDto> call, Throwable t) {
                items.add(getTime() +" Problem with sending location") ;
                adapter.notifyDataSetChanged();
            }
        });
    }

    private LocationDto createLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setAcc(String.valueOf(location.getAccuracy()));
        locationDto.setAlt(String.valueOf(location.getAltitude()));
        locationDto.setBea((int)location.getBearing());
        locationDto.setLat(String.valueOf(location.getLatitude()));
        locationDto.setLng(String.valueOf(location.getLongitude()));
        locationDto.setProv(location.getProvider());
        locationDto.setSpd((int)location.getSpeed());
        locationDto.setSat(0);
        locationDto.setTid("");
        locationDto.setTime(String.valueOf(System.currentTimeMillis()));
        locationDto.setSerial("");
        locationDto.setPlat("Android");
        locationDto.setPlatVer(String.valueOf(Build.VERSION.SDK_INT));
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        locationDto.setBat(batLevel);

        return locationDto;

    }

    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
        items.add(getTime() + " Registered : " + Constants.BROADCAST_DETECTED_ACTIVITY);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        items.add(getTime() + " UnRegistered : " + Constants.BROADCAST_DETECTED_ACTIVITY);
        adapter.notifyDataSetChanged();
    }

    private void startTrackingActivity(){
        Intent intent = new Intent(TrackGpsActivity.this, BackgroundDetectedActivitiesService.class);
        items.add(getTime() + " Started service: " + BackgroundDetectedActivitiesService.class.getName() );
        startService(intent);
    }

    private void stopTrackingActivity(){
        Intent intent = new Intent(TrackGpsActivity.this, BackgroundDetectedActivitiesService.class);
        items.add(getTime() + " Stopped service: " + BackgroundDetectedActivitiesService.class.getName() );
        stopService(intent);
    }

    private void ifNotActiveEnableLocationManager() {
        if (!updatesActive) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1,locationListener);
            //        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minutesInMilliseconds,1,locationListener);
        }
    }


    private void handleUserActivity(int type, int confidence) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                activityText = "IN_VEHICLE";
                ifNotActiveEnableLocationManager();
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                activityText = "ON_BICECYCLE";
                ifNotActiveEnableLocationManager();
                break;
            }
            case DetectedActivity.ON_FOOT: {
                activityText = "ON_FOOT";
                ifNotActiveEnableLocationManager();
                break;
            }
            case DetectedActivity.RUNNING: {
                activityText = "RUNNING";
                ifNotActiveEnableLocationManager();
                break;
            }
            case DetectedActivity.STILL: {
                activityText = "STILL";
                locationManager.removeUpdates(locationListener);
                updatesActive = false;
                break;
            }
            case DetectedActivity.TILTING: {
                activityText = "TILTING";
                ifNotActiveEnableLocationManager();
                break;
            }
            case DetectedActivity.WALKING: {
                activityText = "WALKING";
                ifNotActiveEnableLocationManager();
                break;
            }
            case DetectedActivity.UNKNOWN: {
                activityText = "UNKNOWN";
                ifNotActiveEnableLocationManager();
                break;
            }

        }
        if (confidence > Constants.CONFIDENCE) {
            addActivityToLog(activityText, confidence);
        }
    }


    private void addActivityToLog(String activityText, int confidence) {
        Log.d(TAG, "Add activity");
        if (activityList.size() > 0) {
            ActivityRecognized activityRecognized = activityList.get(activityList.size() - 1);
            if (!activityRecognized.getActivityName().equals(activityText)) {
                activityList.add(new ActivityRecognized(activityRecognized.getActivityName(), "EXIT"));
                items.add(getTime() + " Activity: " + activityRecognized.getActivityName() + " ->  EXIT " +  confidence);
                adapter.notifyDataSetChanged();

                activityList.add(new ActivityRecognized(activityText,"ENTER"));
                items.add(getTime() + " Activity: " + activityText + " -> ENTER " +  confidence);
                adapter.notifyDataSetChanged();
            }
        } else {
            activityList.add(new ActivityRecognized(activityText, "ENTER"));
            items.add(getTime() + " Activity: " + activityText + " -> ENTER " +  confidence);
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
                    Toast.makeText(getApplicationContext(),"Location permission not granted! ",Toast.LENGTH_LONG).show();
                }
                break;
                //need to check if working
            case 102:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initApp();
                } else {
                    Toast.makeText(getApplicationContext(),"Internet permission not granted! ",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean checkIfAlreadyhaveLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkIfAlreadyhaveInternetPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    private void requestForInternetPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 102);
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
        items.add(getTime() + " STOPPED");
        adapter.notifyDataSetChanged();
    }


    public void changeConfiguration(View view) {
        stopTracking(view);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
