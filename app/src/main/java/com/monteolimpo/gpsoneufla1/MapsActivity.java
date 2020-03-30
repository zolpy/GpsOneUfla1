package com.monteolimpo.gpsoneufla1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements LocationListener {
    private SupportMapFragment mapFrag;
    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private boolean allowNetwork; // Liga a triangulação de antenas muito mais rapido do que o GPS, mais impreciso
    private Marker marker; //Fazer marcação no mapa
    private List<LatLng> list = new ArrayList<LatLng>(); //atualizar o caminho com latLng
    private Polyline polyline;
    //public Location teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment1);
        map = mapFrag.getMap();
        configMap();
    }


    @Override
    public void onResume(){
        super.onResume();

        allowNetwork = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent it = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(it); //Caso o GPS não esteja ativo Abre O Settings para acionar o GPS
        }
        /** Se o Settings estiver ativo Liga o NETWORK_PROVIDER primeiro, por que ele é muito mais rapido
         * inicia GPS_PROVIDER apos o NETWORK_PROVIDER */
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            //Criteria criteria = new Criteria();
            //teste = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        locationManager.removeUpdates(this);
    }


    public void configMap(){
        map = mapFrag.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        list = new ArrayList<LatLng>();
        //drawRoute();

        /*Looper looper= new Looper();
        Location localizacaoini = new Location();
        localizacaoini = locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this , looper);*/
        //LatLng latLng = new LatLng(localizacaoini.getLatitude(),localizacaoini.getLongitude()); //zoom de 2 a 21


        //LatLng latLng = new LatLng(-21.226885, -44.976208); //zoom de 2 a 21 location
        //LatLng latLng = new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude()); //zoom de 2 a 21
        //LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude()); //zoom de 2 a 21



        Location location = new Location(LocationManager.GPS_PROVIDER);
        LatLng latLng = new LatLng(-21.228,-44.9751); //zoom de 2 a 21 location
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(3).bearing(0).tilt(90).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);

        //map.moveCamera(update); //muito Ruuuim
        map.animateCamera(update, 3000, new GoogleMap.CancelableCallback(){
            @Override
            public void onCancel() {
                Log.i("Script", "CancelableCallback.onCancel()");
            }

            @Override
            public void onFinish() {
                Log.i("Script", "CancelableCallback.onFinish()");
            }
        });


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){

            @Override
            public android.view.View getInfoContents(Marker marker) {
                TextView tv = new TextView(MapsActivity.this);
                tv.setText(Html.fromHtml("<b><font color=\"#000000\">"+marker.getTitle()+"<br>"+ marker.getSnippet()+":</font></b> "));
                //tv.setText(Html.fromHtml("<b><font color=\"#ff0000\">" + marker.getTitle() + "</font></b> " + marker.getSnippet()));

                return tv;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                LinearLayout ll = new LinearLayout(MapsActivity.this);
                ll.setPadding(20, 20, 20, 20);
                ll.setBackgroundColor(Color.BLUE);
                TextView tv = new TextView(MapsActivity.this);
                tv.setText(Html.fromHtml("<b><font color=\"#ffffff\">"+marker.getTitle()+"<br>"+ marker.getSnippet()+"</font></b> "));
                ll.addView(tv);

                return null; //return ll
            }

        });


        // EVENTS
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) { }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("Script", "setOnMapClickListener()");

                if (marker != null) {
                    marker.remove();
                }
                customAddMarker(new LatLng(latLng.latitude, latLng.longitude), "Latitude: " + latLng.latitude + " º", "Longitude: " + latLng.longitude + " º");
                //list.add(latLng);
                //drawRoute();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("Script", "1: Marker: " + marker.getTitle());
                return false;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i("Script", "2: Marker: " + marker.getTitle());
            }
        });
        configLocation(latLng);
    }

    public void customAddMarker(LatLng latLng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);
        marker = map.addMarker(options);
        //Marker marker = map.addMarker(options);

    }
    public void drawRoute(){
        PolylineOptions po;

        if (polyline == null){
            po = new PolylineOptions();

            for (int i = 0, tam = list.size(); i < tam; i++){
            po.add(list.get(i));
            }
            po.color(Color.BLACK).width(4);
            polyline = map.addPolyline(po);
        }
        else{
            polyline.setPoints(list);
        }
    }

    public void getDistance(View view){
        double distance = 0;

        for(int i = 0, tam = list.size(); i < tam; i++){
            if(i < tam - 1){
                distance += distance(list.get(i), list.get(i+1));
            }
        }

        Toast.makeText(MapsActivity.this, "Distancia: " + distance + " metros", Toast.LENGTH_LONG).show();
    }


    public static double distance(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }

    public void configLocation(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(3).bearing(10).tilt(0).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);

        map.setMyLocationEnabled(true);
        map.animateCamera(update);
        MyLocation myLocation = new MyLocation();
        map.setLocationSource(myLocation);
        myLocation.setLocation(latLng);
    }




    public void getLocation(View view){
        Geocoder gc = new Geocoder(MapsActivity.this);
        //drawRoute();
        List<Address> addressList;
       // drawRoute();
        try {
            addressList = gc.getFromLocation(list.get(list.size() - 1).latitude, list.get(list.size() - 1).longitude, 1);

            String address = addressList.get(0).getThoroughfare()+"\n";
            address += "Cidade: "+addressList.get(0).getSubAdminArea()+"\n";
            address += "Estado: "+addressList.get(0).getAdminArea()+"\n";
            address += "País: "+addressList.get(0).getCountryName()+"\n";
            address += "Latitude: "+addressList.get(0).getLatitude()+"º \n";
            address += "Longitude: "+addressList.get(0).getLongitude()+" º";

            //LatLng ll = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

            Toast.makeText(MapsActivity.this, "Local: "+address, Toast.LENGTH_LONG).show();
            //Toast.makeText(MapsActivity.this, "LatLng: "+ll, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




    public class MyLocation implements LocationSource{
        private OnLocationChangedListener listener;


        @Override
        public void activate(OnLocationChangedListener listener) {
            this.listener = listener;
            Log.i("Script", "activate()");
        }

        @Override
        public void deactivate() {
            Log.i("Script", "deactivate()");
        }


        public void setLocation(LatLng latLng){
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            list.add(latLng);
            drawRoute();


            if (listener != null) {
                listener.onLocationChanged(location);
            }




        }

    }



    @Override
    public void onLocationChanged(Location location) {
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
            allowNetwork = false;
        }

        if(allowNetwork || location.getProvider().equals(LocationManager.GPS_PROVIDER)){
            configLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


}

