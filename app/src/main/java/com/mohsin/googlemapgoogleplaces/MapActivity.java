package com.mohsin.googlemapgoogleplaces;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    String Tag = "MapActivity";
    private static final String Fine_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Corse_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    boolean mlocationpermissiongranted = false;
    private static final int Location_PErmisiion_COde = 12245;


    FusedLocationProviderClient mfusedLocationProviderClient;
    private static final float default_zoom = 15f;

    private GoogleMap mMap;


    EditText msearchtext;
    ImageView mgps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationPermission();
       // init1();

        msearchtext=findViewById(R.id.input_search);
        mgps=findViewById(R.id.ic_gps);
        Places.initialize(getApplicationContext(),"AIzaSyBzF67WeJj5ITc5wzch-T1OfFYoj6eUIZo");
    }

   private void  init1()
   {
       Log.d(Tag,"NEW INIt1 intializing");
       msearchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
               if(actionid== EditorInfo.IME_ACTION_SEARCH||actionid== EditorInfo.IME_ACTION_DONE||keyEvent.getAction()==KeyEvent.ACTION_DOWN||keyEvent.getAction()==KeyEvent.KEYCODE_ENTER)
               {
                         //execute our method for searching
                   geoLocate();

               }
               return false;
           }
       });
       HideSoftKeyword();

       mgps.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.d(Tag,"I am getting My Location");
               getDeviceLocation();
           }
       });
   }

private void geoLocate()
{
    Log.d(Tag, "geolocate");
    String searchString=msearchtext.getText().toString();
    Geocoder geocoder=new Geocoder(MapActivity.this);
    List<Address> list=new ArrayList<>();
    try {
        list=geocoder.getFromLocationName(searchString,1);
    }catch (IOException e)
    {
        Log.d(Tag,"geolocate : IO Exception"+e.getMessage());
    }
    Address address=list.get(0);
    if(list.size()>0)
    {

        Log.d(Tag,"geolocate : found a location"+address.toString());
    }

    moveCAmera(new LatLng(address.getLatitude(),address.getLongitude()),default_zoom,address.getAddressLine(0));

}


    private void getDeviceLocation() {
        Log.d(Tag, "getdevicelocation : getting the device loation");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        try {
            if (mlocationpermissiongranted) {
                final Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(Tag, "Oncomplete Found location");
                            Location currentLocation = (Location) task.getResult();
                            moveCAmera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), default_zoom,"My Location");
                            // moveCAmera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),default_zoom);


                        } else {
                            Log.d(Tag, "Current location i s null");
                            Toast.makeText(MapActivity.this, "unable to find location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(Tag, "getdevicelocation : Security Exception" + e.getMessage());
        }
    }





    private void moveCAmera(LatLng latLng, float zoom,String tittle) {
        Log.d(Tag, "movecamera : moving camera 1st :" + latLng.latitude + ",lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(!tittle.equals("My Location"))
        {
            MarkerOptions options=new MarkerOptions().position(latLng).title(tittle);
            mMap.addMarker(options);
        }
        HideSoftKeyword();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MapActivity.this, "MAp is ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;
        if (mlocationpermissiongranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().isZoomGesturesEnabled();
            init1();
        }
    }

    private void init()
    {
        Toast.makeText(MapActivity.this,"Initializing the map",Toast.LENGTH_LONG).show();
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
       //prepare the map
        mapFragment.getMapAsync(MapActivity.this);
    }


    public void getLocationPermission()
    {
        Log.d(Tag,"getlocationpermission: gettinglocationpermission");
        String[] permission ={Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Fine_Location)== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Corse_Location)== PackageManager.PERMISSION_GRANTED)
            {
                mlocationpermissiongranted=true;
                init();
            }
            else
            {
                ActivityCompat.requestPermissions(this,permission,Location_PErmisiion_COde);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,permission,Location_PErmisiion_COde);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(Tag,"onRequestPermissionsResult : called");
        mlocationpermissiongranted=false;
        switch (requestCode)
        {
            case Location_PErmisiion_COde:
            {
                if(grantResults.length>0)
                {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mlocationpermissiongranted = false;
                            Log.d(Tag,"onRequestPermissionsResult : permission faild");
                            return;
                        }
                    }
                    Log.d(Tag,"onRequestPermissionsResult : permission granted");
                    mlocationpermissiongranted = true;
                    init();
                }
            }
        }
    }


    private void HideSoftKeyword()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    //googlw places auto comple suggestion

//    private AdapterView.OnItemClickListener mAutocompleteListner=new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            HideSoftKeyword();
//            AutocompletePrediction item=
//            final String PlaceId=item.getPlaceId();
//
//        }
//    };

}
