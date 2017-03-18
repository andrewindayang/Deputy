package deputyapp.deputyapp.Network.service;

import java.util.List;

import deputyapp.deputyapp.Model.PostShift;
import deputyapp.deputyapp.dao.Business;
import deputyapp.deputyapp.dao.PrevShiftsData;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface DeputyService {

    @GET("/business")
    void getBusiness(Callback<Business>businessCallback);

    @GET("/shifts")
    void getPreviousShift(Callback<List<PrevShiftsData>>prevShiftlistCallback);

    @POST("/shift/start")
    void postShiftStart(@Body PostShift postShift,
                        Callback<String> stringCallback);

    @POST("/shift/end")
    void postShiftEnd(@Body PostShift postShift,
                      Callback<String> stringCallback);

}
