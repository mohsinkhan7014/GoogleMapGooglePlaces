package com.mohsin.googlemapgoogleplaces;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    String tag="MAin Activity";
    int Error_Dialog_REquest=9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServiceOK())
        {
            init();
        }
    }

    public void init()
    {
        Button btnmap=(Button)findViewById(R.id.btnMap);
        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }


    public boolean isServiceOK()
    {
        Log.d(tag,"is Service OK : checkeing ");
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available== ConnectionResult.SUCCESS)
        {
            Log.d(tag,"Service is Working fine ");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            Log.d(tag,"error but we can solve ");
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,Error_Dialog_REquest);
            dialog.show();

        }
        else
        {
            Toast.makeText(MainActivity.this,"You cannot make a request",Toast.LENGTH_LONG).show();
        }
        return false;
    }

}