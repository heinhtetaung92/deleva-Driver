// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver.api;


import knayi.delevadriver.APIConfig;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

public interface RetrofitInterface
{

    @GET(APIConfig.JOBS_BASE_URL)
    public void getJobListByLocation(@Query("access_token") String s, @Query("location") String s1,@Query("timestamp") String ts, Callback<String> callback);

    @GET(APIConfig.MY_JOBS_URL)
    public void getMyJobList(@Query("access_token") String s, Callback<String> callback);

    @GET(APIConfig.JOB_DETAIL_URL)
    public void getJobDetail(@Path("id") String id, @Query("access_token") String token, Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.ACCEPT_JOB_URL)
    public void acceptJob(@Path("id") String id, @Query("access_token") String token, @Field("location") String location, @Field("timestamp") String timestamp, Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.REJECT_JOB_URL)
    public void rejectJob(@Path("id") String id, @Query("access_token") String token,@Field("location") String location, @Field("timestamp") String timestamp, @Field("message") String message, Callback<String> callback);

    @POST(APIConfig.JOB_DONE_URL)
    public void jobDone(@Path("id") String id, @Query("access_token") String token, @Body TypedInput input, Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.DRIVER_REGISTER)
    public void driverRegister(@Field("name") String name,
                               @Field("email") String email,
                               @Field("password") String password,
                               @Field("mobile_number") String mobileno,
                               @Field("address") String address,
                               @Field("last_ll") String ll,
                               Callback<String> callback);

    @POST(APIConfig.GET_TOKEN)
    public void getToken(@Body TypedInput input, Callback<String> callback);

    @GET(APIConfig.PROFILE_URL)
    public void getProfile(@Query("access_token") String token, Callback<String> callback);

    @FormUrlEncoded
    @PATCH(APIConfig.PROFILE_URL)
    public void updateProfile(@Query("access_token") String token,
                              @Field("name") String name,
                              @Field("email") String email,
                              @Field("mobile_number") String mobileno,
                              @Field("address") String address,
                              Callback<String> callback);

}


