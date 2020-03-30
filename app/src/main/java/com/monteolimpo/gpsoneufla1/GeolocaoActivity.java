package com.monteolimpo.gpsoneufla1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GeolocaoActivity extends Activity {

    TextView textLat, textLong, textDist, textvelocidade, textkilo;
    EditText edittempo;
    Button bttempo;
    private List<LatLng> list = new ArrayList<LatLng>(); //atualizar o caminho com latLng

    final LinkedList<Coordenadas> coordenadas = new LinkedList<Coordenadas>(); // Lista para armazenar coordenadas


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLat= (TextView) findViewById(R.id.textLat);
        textLong = (TextView) findViewById(R.id.textLong);
        textDist = (TextView) findViewById(R.id.textDist);
        textvelocidade = (TextView) findViewById(R.id.textvelocidade);
        bttempo = (Button) findViewById(R.id.bttempo);
        edittempo = (EditText) findViewById(R.id.edittempo);
        textkilo = (TextView) findViewById(R.id.textkilo);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        class myLocationListener implements LocationListener{

            @Override
            public void onLocationChanged(Location location) {


                if (location !=null){

                    double pLong = location.getLongitude();
                    double pLat = location.getLatitude();

                    Coordenadas c=new Coordenadas();

                    c.setLatitude(pLat);
                    c.setLongitude(pLong);

                    coordenadas.add(c);

                    if(coordenadas.size()!=0 && coordenadas.size()!=1){
                        Coordenadas d=coordenadas.get(coordenadas.size()-1);
                        Coordenadas f=coordenadas.get(coordenadas.size()-2);

                        double distance=0;
                        distance = Math.sqrt(Math.pow(d.latitude-f.latitude, 2)+Math.pow(d.longitude-f.longitude, 2));

                        textDist.setText(Double.toString(distance));
                        textvelocidade.setText(Double.toString(distance/2));
                        textkilo.setText(Double.toString((distance*3.6)/2));

                    }


                    textLat.setText(Double.toString(pLat));
                    textLong.setText(Double.toString(pLong));

                }

                bttempo.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {

                        double tempo = Double.parseDouble(edittempo.getText().toString());

                        Coordenadas busca = coordenadas.get((int) tempo);

                        AlertDialog.Builder dialogo = new AlertDialog.Builder(GeolocaoActivity.this);
                        dialogo.setTitle("Busca de coordenadas");
                        dialogo.setMessage("As coordenadas no tempo "+ tempo + " sao \nLatitude: "+ busca.getLatitude() +"\nLongitude: "+ busca.getLongitude());
                        dialogo.setNeutralButton("Voltar", null);
                        dialogo.show();
                    }
                });

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }}

        LocationListener ll = new myLocationListener();


        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000 , 0 , ll);
    }



}