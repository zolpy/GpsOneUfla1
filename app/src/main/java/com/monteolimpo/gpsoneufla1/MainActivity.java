package com.monteolimpo.gpsoneufla1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity { //ActionBarActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void chamaMaps(View view){
        Intent intent = new Intent (this, MapsActivity.class);
        startActivity(intent);
    }

    /*--
       public void chamaGeolocao(View view){
        Intent intent = new Intent (this, GeolocaoActivity.class);
        startActivity(intent);
    } */

    public void chamaCreditos(View view){
        Intent intent = new Intent (this, CreditosActivity.class);
        startActivity(intent);
    }
    public void chamaRotas(View view){
        Intent intent = new Intent (this, RotaActivity.class);
        startActivity(intent);
    }


}
