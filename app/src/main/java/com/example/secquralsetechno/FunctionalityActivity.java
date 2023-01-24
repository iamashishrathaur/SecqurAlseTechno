package com.example.secquralsetechno;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

public class FunctionalityActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {
 boolean isConnected;
 RelativeLayout isShow;
 //internet
 Handler handler;
    boolean isCharging;
    //charging
    int PERMISSION_ID = 44;
    String currentDateTime;
    public static String imei;
    Uri imageUri=null;
    Location location;
    int batteryPct;
    float longitude;
    float latitude;
    private final int CAMERA = 2;
    TextView internetstatus,chargingstatus,charging,locationstatus,timestamp;
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Gallery");
    StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    FusedLocationProviderClient mFusedLocationClient;
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionality);
        this.handler = new Handler();
        this.handler.postDelayed(m_Runnable,5000);

        Objects.requireNonNull(getSupportActionBar()).hide();
        checkConnection();
        getCurrentTimeStamp();
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.intenate_alert);
        internetstatus=findViewById(R.id.internet_status);
        chargingstatus=findViewById(R.id.charging_status);
        charging=findViewById(R.id.charge);
        isShow=findViewById(R.id.isShow);
        locationstatus=findViewById(R.id.location);
        timestamp=findViewById(R.id.time_stamp);
        String latitudes= String.valueOf(latitude);
        String longitude1= String.valueOf(longitude);
        locationstatus.setText(latitudes+longitude1);

        timestamp.setText(currentDateTime);
        getUniqueIMEIId(this);
        timestamp.setOnClickListener(view -> Toast.makeText(FunctionalityActivity.this, ""+imei, Toast.LENGTH_SHORT).show());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (isConnected) {
            dialog.dismiss();
            isShow.setVisibility(View.VISIBLE);
            internetstatus.setText("true");

        } else {
            dialog.show();
            isShow.setVisibility(View.INVISIBLE);
            internetstatus.setText("false");
            TextView button=dialog.findViewById(R.id.try_again);
            button.setOnClickListener(view -> {
                checkConnection();
                if (isConnected){
                    dialog.dismiss();
                    isShow.setVisibility(View.VISIBLE);
                    internetstatus.setText("true");
                }
                else {
                    Toast.makeText(FunctionalityActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                    isShow.setVisibility(View.INVISIBLE);
                    internetstatus.setText("false");

                }
            });
        }

        Context context = getApplicationContext();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
        status == BatteryManager.BATTERY_STATUS_FULL;
        batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = level * 100 / scale;
        String pct= String.valueOf(batteryPct);
        charging.setText(pct+"%");
        String sc= String.valueOf(isCharging);
        chargingstatus.setText(sc);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("data");
        DatabaseClass databaseClass=new DatabaseClass(isConnected,isCharging,batteryPct,latitude,longitude,currentDateTime);
        databaseReference.child(UUID.randomUUID().toString()).setValue(databaseClass);
    }

    @SuppressLint("SetTextI18n")
    private void getLastLocation() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                        location = task.getResult();
                        if (location == null) {
                        } else {
                            latitude= (float) location.getLatitude();
                            longitude= (float) location.getLongitude();
                            String la= String.valueOf(latitude);
                            String lo= String.valueOf(longitude);

                            locationstatus.setText(lo +" "+ la);
                        }
                    });
                } else {
                    Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            } else {
                requestPermissions();
            }
        }
        private final LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                assert mLastLocation != null;
            }
        };
        private void requestPermissions() {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }
        private boolean isLocationEnabled() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
    public void getCurrentTimeStamp(){
        Time now = new Time();
        now.setToNow();
        currentDateTime = now.format("%H-%M-%S");
    }

    private void checkConnection() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
            registerReceiver(new ConnectionReceiver(), intentFilter);
            ConnectionReceiver.Listener = this;
            ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        @Override
        public void onNetworkChange(boolean isConnected) {
            checkConnection();
        }
    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
        handler.removeCallbacks(m_Runnable);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        checkConnection();
    }

    public void ClickImage(View view) {
        selectImage();
    }
    private void selectImage() {

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
        uplodeImage();
    }
    private void uplodeImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading....");
       // progressDialog.show();
        if (imageUri !=null) {
            String uid = UUID.randomUUID().toString();
            StorageReference filepath = storageReference.child("Images").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                DatabaseClass databaseClass = new DatabaseClass(uri.toString());
                String smodal = reference.push().getKey();
                assert smodal != null;
                reference.child(smodal).setValue(databaseClass);
                progressDialog.dismiss();
            })).addOnFailureListener(e -> progressDialog.dismiss()).addOnProgressListener(snapshot -> progressDialog.show());
        }
    }
    @SuppressLint("HardwareIds")
    public void getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
           imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        int GALLERY = 1;
        if (requestCode == GALLERY) {
            if (data != null) {
                imageUri = data.getData();
            }

        }  //  Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

    }
    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            FunctionalityActivity.this.handler.postDelayed(m_Runnable, 900000);
        }

    };//runnable

    }