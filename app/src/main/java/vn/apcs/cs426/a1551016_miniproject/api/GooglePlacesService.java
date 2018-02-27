package vn.apcs.cs426.a1551016_miniproject.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by khoanguyen on 6/20/17.
 */

public interface GooglePlacesService {
    @GET("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
    Call<ResultResponse> listResults(@Query("location") String location,
                                     @Query("radius") int radius,
                                     @Query("type") String type);
}
