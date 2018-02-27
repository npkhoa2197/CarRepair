package vn.apcs.cs426.a1551016_miniproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.apcs.cs426.a1551016_miniproject.api.DirectionResponse;
import vn.apcs.cs426.a1551016_miniproject.api.ResultResponse;
import vn.apcs.cs426.a1551016_miniproject.api.RetrofitClient;
import vn.apcs.cs426.a1551016_miniproject.entities.Direction.Route;
import vn.apcs.cs426.a1551016_miniproject.entities.Result;

import static android.graphics.Typeface.BOLD;
import static android.view.View.GONE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * Variables definition for initializing the map, getting the current position of the device and initializing infoWindow
     */
    private GoogleMap mMap;
    private MapWrapperLayout mapWrapperLayout;
    private View contentView;
    private ImageButton btnDetail;
    private ImageButton btnFindDirection;

    private GoogleApiClient googleApiClient;
    private boolean locationPermissionGranted;
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location currentLocation;
    private CameraPosition cameraPosition;

    //default location is HCM (latitude and longitude of HCM city)
    private final LatLng defaultLocation = new LatLng(10.762622, 106.660172);

    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    /**
     * Variables definition for Google Places API service
     */
    private RetrofitClient retrofitGooglePlace;
    private final static String API_KEY_GOOGLE_PLACE = "AIzaSyCo_YqOwgJpk4avqlQPrbvFiCLUvs2Ed5Q";
    private final static String BASE_URL_GOOGLE_PLACE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json/";

    private RetrofitClient retrofitGoogleDirection;
    private final static String API_KEY_GOOGLE_DIRECTION = "AIzaSyCPk1aoLYw7r0QqnEPWHfqF6QYGE9c0Qb8";
    private final static String BASE_URL_GOOGLE_DIRECTION = "https://maps.googleapis.com/maps/api/directions/json/";
    Polyline line;

    private final static int radius = 1500; //default radius of nearby search
    private final static String type = "car_repair"; //default type of nearby search
    private Button btnFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //
        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    //save the state in case the application is paused
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, currentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getDeviceLocation() {
        if (locationPermissionGranted) {
            currentLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (cameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else if (currentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), DEFAULT_ZOOM));
        }
        /*
        if the current location can't be found, the default one will be used
        here I choose the default one to be latitude and longitude of HCM city
        */
        else {
            Toast.makeText(this, "Current location is null. Using defaults.", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    private void updateLocationUI() {
        if (mMap == null)
            return;

        /*
        Request permission for getting the device's current location on runtime:
            let user choose between "Allow" and "Deny"

        The result of the request will be handled by a callback method onRequestPermissionResult
        If permission granted, current location will be set and current location button will appear
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (locationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            currentLocation = null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.mapWrapper);
        btnFind = (Button) findViewById(R.id.buttonFindNearby);
        contentView = LayoutInflater.from(this).inflate(R.layout.info_window_content, null);

        btnDetail = (ImageButton) contentView.findViewById(R.id.buttonDetail);
        btnFindDirection = (ImageButton) contentView.findViewById(R.id.buttonFindDirection);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapWrapperLayout.init(mMap, this);

        updateLocationUI();

        getDeviceLocation();

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchNearbyCarRepair();
                btnFind.setVisibility(GONE);
            }
        });

        /**
         * this is a listener for detail button in the info window of each marker
         * This function is just a simple transformation between two activities
         */
        OnInfoWindowElemTouchListener detailBtnClicked = new OnInfoWindowElemTouchListener(btnDetail) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent intent = new Intent(MapsActivity.this, DetailActivity.class);

                MapsActivity.this.startActivity(intent);

                intent.putExtra("title", ((TextView)contentView.findViewById(R.id.place_title)).getText().toString());
                String moreInfo = ((TextView)contentView.findViewById(R.id.more_info)).getText().toString();
                String [] moreInfoPart = moreInfo.split("- ");
                intent.putExtra("open", moreInfoPart[1]);
                intent.putExtra("address", moreInfoPart[2]);

                MapsActivity.this.startActivity(intent);
            }
        };

        /**
         * this is a listener for find direction button in the info window of each marker
         * It takes advantage of Google Direction API
         */
        OnInfoWindowElemTouchListener findDirectionBtnClicked = new OnInfoWindowElemTouchListener(btnFindDirection) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                retrofitGoogleDirection = new RetrofitClient(API_KEY_GOOGLE_DIRECTION, BASE_URL_GOOGLE_DIRECTION);

                String location = String.valueOf(currentLocation.getLatitude())
                        + ","
                        + String.valueOf(currentLocation.getLongitude());
                String destination = ((TextView)contentView.findViewById(R.id.location)).getText().toString();
                destination = destination.replace("lat/lng: (","");
                destination = destination.replace(")","");

                retrofitGoogleDirection.getDirectionService().listRoutes(location, destination)
                        .enqueue(new Callback<DirectionResponse>() {
                            @Override
                            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                                if (response.isSuccessful()) {
                                    if (line != null)
                                        line.remove();

                                    DirectionResponse directionResponse = response.body();

                                    if (directionResponse != null) {
                                        List<Route> routes = directionResponse.getRoutes();
                                        if (routes != null) {
                                            String encodedString = "";

                                            for (Route route : routes)
                                                encodedString = encodedString + route.getOverviewPolyline().getPoints();

                                            List<LatLng> list = decodePoly(encodedString);
                                            line = mMap.addPolyline(new PolylineOptions()
                                                    .addAll(list)
                                                    .width(10)
                                                    .color(Color.BLUE)
                                            );

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<DirectionResponse> call, Throwable t) {

                            }
                        });
            }
        };

        btnDetail.setOnTouchListener(detailBtnClicked);
        btnFindDirection.setOnTouchListener(findDirectionBtnClicked);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                TextView title = (TextView) contentView.findViewById(R.id.place_title);
                TextView moreInfo = (TextView) contentView.findViewById(R.id.more_info);
                TextView location = (TextView) contentView.findViewById(R.id.location);

                title.setText(marker.getTitle());
                title.setTypeface(null, BOLD);

                moreInfo.setText(marker.getSnippet());
                location.setText(marker.getPosition().toString());

                mapWrapperLayout.setMarkerWithInfoWindow(marker, contentView);
                return contentView;
            }
        });
    }

    /**
     * This fragment of code is taken from the course's tutorial
     */
    private List<LatLng> decodePoly(String encodedString) {
        int len = encodedString.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    /**
     * the following function is to fetch nearby car repair shops
     * It takes advantage of Google Places API with SearchNearby call
     * The lists of shops returned will be added to the map as markers
     */
    private void fetchNearbyCarRepair() {
        retrofitGooglePlace = new RetrofitClient(API_KEY_GOOGLE_PLACE, BASE_URL_GOOGLE_PLACE);

        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
        }

        String location = String.valueOf(currentLocation.getLatitude())
                + ","
                + String.valueOf(currentLocation.getLongitude());

        retrofitGooglePlace.getPlaceService().listResults(location, radius, type)
                .enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                        if (response.isSuccessful()) {
                            ResultResponse resultResponse = response.body();

                            if (resultResponse != null) {
                                List<Result> results = resultResponse.getResults();
                                if (results != null) {
                                    for (Result result : results) {
                                        if(result.getGeometry() != null) {
                                            if (result.getGeometry().getLocation() != null) {
                                                double tempLat = result.getGeometry().getLocation().getLat();
                                                double tempLng = result.getGeometry().getLocation().getLng();

                                                String openStatus = "- Opening hours not available.";
                                                LatLng tempPlace = new LatLng(tempLat, tempLng);

                                                if (result.getOpeningHours() != null) {
                                                    if (result.getOpeningHours() != null)
                                                        openStatus = "- Open Now";
                                                    else
                                                        openStatus = "- Close Now";
                                                }

                                                openStatus += "\n- " + result.getVicinity();

                                                // add a marker
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(tempPlace)
                                                        .title(result.getName())
                                                        .snippet(openStatus));
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultResponse> call, Throwable t) {
                        Toast.makeText(MapsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
