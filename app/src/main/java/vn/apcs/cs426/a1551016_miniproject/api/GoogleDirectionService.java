package vn.apcs.cs426.a1551016_miniproject.api;

import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by khoanguyen on 6/21/17.
 */

public interface GoogleDirectionService {
    @GET("https://maps.googleapis.com/maps/api/directions/json")
    Call<DirectionResponse> listRoutes(@Query("origin") String origin,
                                       @Query("destination") String destination);
}
