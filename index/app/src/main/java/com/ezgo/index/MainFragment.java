package com.ezgo.index;


import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private View rootView;
    GoogleMap mMap;

    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;   // Google API用戶端物件
    private LocationRequest mLocationRequest;   // Location請求物件

    private ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private PendingIntent mGeofencePendingIntent;
    private MyData myData=new MyData();

    private Location currentLocation;   // 記錄目前最新的位置
    private Location mLastLocation;
    private Marker currentMarker;

    private static final LatLng center = new LatLng(25.00234,121.48368);
    private double mark1Lat=25.043121; //北商位置
    private double mark1Lng=121.524816;

    private double myLat;
    private double myLng;
    private double a=0.00001;

    private double[][] marklatlng ={
            {25.00234, 121.48368},
            {25.043121, 121.524816}
    };

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        try{
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mainMap);
            mapFragment.getMapAsync(MainFragment.this);
        }catch (Exception e){
            e.printStackTrace();
        }

        //檢查是否有開啟GPS
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(getActivity(), "請開啟GPS", Toast.LENGTH_LONG).show();
        }

        //連接GOOGLE API
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return rootView;
    }

    //---------------加入Geofence--------------
    private void addGeoFence(){
        Double geofenceList[][]=myData.getGeofenceList();

        for (int i=0; i<geofenceList.length; i++){
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId((geofenceList[i][2]).toString())
                    .setCircularRegion(geofenceList[i][0], geofenceList[i][1],25)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

            addCircle(geofenceList[i][0], geofenceList[i][1]);//測試用circle
        }
    }

    //--------------建立Geofence----------------
    private void startGeofenceMonitoring(){
        try{
            //加入Geofence
            addGeoFence();

            // 建立Geofence請求物件
            GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofences(mGeofenceList)
                    .build();

            mGeofencePendingIntent = getGeofencePendingIntent();
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofenceRequest,mGeofencePendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()){
                                Log.e(TAG, "Successfully added geofence");
                                Toast.makeText(getActivity(),"Geofence成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Log.e(TAG, "Failed to add geofence"+status.getStatus());
                                Toast.makeText(getActivity(),"Geofence失敗",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }catch (SecurityException e){
            Log.e(TAG, "SecurityException - " + e.getMessage());
        }
    }

    private PendingIntent getGeofencePendingIntent(){
        if(mGeofencePendingIntent != null){
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(getActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //--------------停止Geofence----------------
    private void stopGeofenceMonitoring(){
        Log.d(TAG, "stopGeofenceMonitoring");
        ArrayList<String> geofenceIds = new ArrayList<String>();
        Double geofenceList[][]=myData.getGeofenceList();

        for(int i=0; i<geofenceList.length; i++){
            geofenceIds.add((geofenceList[i][2]).toString());
        }

        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,geofenceIds);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        // 移除位置請求服務
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            stopGeofenceMonitoring();
        }
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        // 移除Google API用戶端連線
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Log.d(TAG, "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();

        // 連線到Google API用戶端
        if (!mGoogleApiClient.isConnected() && currentMarker != null) {
            mGoogleApiClient.connect();
        }
        Log.d(TAG, "onResume");
        getActivity().setTitle(R.string.app_name); //將標題設為EZ Go
    }

    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到Google Services啟動位置更新服務，位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged

        // 建立Location請求物件
        mLocationRequest = new LocationRequest()
                .setInterval(2000)  // 設定讀取位置資訊的間隔時間為一秒（1000ms）
                .setFastestInterval(5000)   // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);   // 設定優先讀取高精確度的位置資訊（GPS）
/*
        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

        }

        LatLng latLng = new LatLng(
                mLastLocation.getLatitude(), mLastLocation.getLongitude());
        addMarker(latLng,"最後位置");
*/

        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        Log.d(TAG, "onConnected");

        startGeofenceMonitoring();  //建立Geofence
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Google Services連線中斷
        // int參數是連線中斷的代號
        Log.d(TAG, "Google Services連線中斷");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(getActivity(), "google_play_service_missing", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置

        currentLocation = location;
        LatLng latLng = new LatLng(
                location.getLatitude(), location.getLongitude());


/*
        // 設定目前位置的標記
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        }
        else {
            currentMarker.setPosition(latLng);
        }
*/

 /*
        myLat=location.getLatitude();
        myLng=location.getLongitude();

        int i;
        for(i=0; i<marklatlng.length; i++){
            if(myLat>marklatlng[i][0]-a && myLat<marklatlng[i][0]+a && myLng>marklatlng[i][1]-a && myLng<marklatlng[i][1]+a){
                Toast.makeText(getActivity(),"我的位置:"+ myLat+ "  ,  "+ myLng,Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if(i==marklatlng.length){
            Toast.makeText(getActivity(),"我的位置:"+ myLat+ "  ,  "+ myLng+"\n北商位置:"+ mark1Lat+ "  ,  "+ mark1Lng,Toast.LENGTH_SHORT).show();
        }

        // 移動地圖到目前的位置
        //moveMap(latLng);
*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            LatLng taiZoo = new LatLng(24.997057, 121.584917);
            LatLng taiZoo1 = new LatLng(24.999108, 121.581069);

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); /*地圖種類*/

            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setCompassEnabled(true); /*顯示指北針*/
            uiSettings.setMyLocationButtonEnabled(true); /*顯示自己位置按鈕*/

            moveMap(taiZoo);
            //mapOverlay();

            //建立各園區marker
            String animalMarkers[][]=myData.getAnimalMarkers();

            for(int i=0; i<animalMarkers.length; i++){
                LatLng position = new LatLng(Double.parseDouble(animalMarkers[i][0]),Double.parseDouble(animalMarkers[i][1]));
                mMap.addMarker(new MarkerOptions().position(position).title(animalMarkers[i][2]));
            }

            //各園區marker點擊事件
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    final List<String> animalList=myData.getAnimalList(marker.getTitle());

                    new AlertDialog.Builder(getActivity())
                            .setTitle( marker.getTitle()) //添加標題
                            .setItems(animalList.toArray(new String[animalList.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { //倒入資料項目
                                    String name = animalList.get(which);
                                    Toast.makeText(getActivity(), "你選擇的動物是" + name, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });


/*
            addMarker(taiZoo,"木柵動物園");
            addMarker(taiZoo1,"木柵動物園");
*/

            //drawPolyline(mMap);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private  void addCircle(Double lat,Double lng){
        LatLng latLng = new LatLng(lat,lng);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(20)
                .strokeWidth(0)
                .strokeColor(Color.argb(200, 255,0,0))
                .fillColor( Color.argb(50, 255,0,0) );
        mMap.addCircle( circleOptions );
    }

    //---------------------------------移動地圖到參數指定的位置-------------------------
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .bearing(147)
                        .zoom(16)
                        .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

/*
        LatLngBounds testPlace = new LatLngBounds(
                new LatLng(25.001630, 121.483476), new LatLng(25.002435, 121.484718));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testPlace.getCenter(), 16));*/
    }

    //---------------------------------在地圖加入指定位置與標題的標記-------------------------
    private void addMarker(LatLng place, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.zooicon0));

        mMap.addMarker(markerOptions);

    }

    //---------------------------------覆蓋圖層-------------------------
    private void mapOverlay(){
        LatLngBounds taipeiZoo = new LatLngBounds(
                new LatLng(24.984760, 121.573060),// South west corner
                new LatLng(25.005686, 121.602542)); // North east corner

        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.zoo_map))
                .position(new LatLng(24.996279, 121.584971), 1350f, 1350f);
        //.positionFromBounds(taipeiZoo);

        mMap.addGroundOverlay(newarkMap);
    }

    //--------------------------------------------------地圖畫線----------------------------------------
    private void drawPolyline(GoogleMap map){
        PolylineOptions polylineOpt = new PolylineOptions();
        polylineOpt.add(new LatLng(24.997504, 121.585517));
        polylineOpt.add(new LatLng(24.999108, 121.581069));

        polylineOpt.color(Color.BLUE);//線條顏色
        Polyline polyline = map.addPolyline(polylineOpt);
        polyline.setWidth(10);//線條寬度
    }

    public void onDestroyView()
    {
        try {
            Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.mainMap));
            if (fragment != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroyView();
    }

}
