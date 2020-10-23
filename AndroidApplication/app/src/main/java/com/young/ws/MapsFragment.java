package com.young.ws;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapsFragment extends Fragment implements PlacesListener {
    MapView mapView;
    GoogleMap gmap;
    MyLocation myLocation;
    LocationManager locationManager;

    List<Marker> previous_marker;

    Location nowLocation;

    public MapsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = null;
        viewGroup = (ViewGroup) inflater.inflate(
                R.layout.fragment_maps, container, false
        );

        previous_marker = new ArrayList<>();

        myLocation = new MyLocation();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mapView = (MapView) viewGroup.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return viewGroup;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1,
                0,
                myLocation
        );

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gmap = googleMap;
                googleMap.setMyLocationEnabled(true);
//                LatLng latLng = new LatLng(37.4556574, 126.4367135);
//                googleMap.addMarker(
//                        new MarkerOptions().position(latLng).title("공항").snippet("xxx")
//                );
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng latLng;
                if(l==null){
                    latLng = new LatLng(37.4556574, 126.4367135);
                }
                else{
                    latLng = new LatLng(l.getLatitude(),l.getLongitude());
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

                showPlaceInformation(latLng);

            }
        });




        return viewGroup;
    }

    class MyLocation implements LocationListener {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("--------------------------","onStatusChanged");

        }

        @Override
        public void onLocationChanged(@NonNull Location location) {

//            if(location != null){
//                double lat = location.getLatitude();
//                double lon = location.getLongitude();
//                Log.d("--------------------------",lat+"");
//            }else{
//                Log.d("--------------------------","NULL..........................");
//            }

            double lat = location.getLatitude();
            double lon = location.getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPause() { //일시정지
        super.onPause();
        if(gmap!=null){
            gmap.setMyLocationEnabled(false);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() { //다시시작
        super.onResume();
        if(gmap!=null){
            gmap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet("XXX");
                    Marker item = gmap.addMarker(markerOptions);
                    previous_marker.add(item);

                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    public void showPlaceInformation(LatLng location)
    {
        gmap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(this)
                .key(Contants.GOOGLE_PLACE_API_KEY)//API Key
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(2500) //500 미터 내에서 검색
                .type(PlaceType.MOVIE_THEATER) //영화관
                .build()
                .execute();
    }
}