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
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
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
    public void acceptJob(@Path("id") String id, @Query("access_token") String token, @Field("location") String location, @Field("timestamp") String timestamp, @Field("duration_text") String duration, Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.REJECT_JOB_URL)
    public void rejectJob(@Path("id") String id, @Query("access_token") String token,@Field("location") String location, @Field("timestamp") String timestamp, @Field("message") String message, Callback<String> callback);

    @POST(APIConfig.JOB_DONE_URL)
    public void jobDone(@Path("id") String id, @Query("access_token") String token, @Body TypedInput input, Callback<String> callback);

    @Multipart
    @POST(APIConfig.DRIVER_REGISTER)
    public void driverRegister(@Part("name") String name,
                               @Part("email") String email,
                               @Part("password") String password,
                               @Part("mobile_number") String mobileno,
                               @Part("address") String address,
                               @Part("last_ll") String ll,
                               @Part("pictures") TypedFile pic,
                               @Part("id_card") String nrc_id,
                               @Part("vehicle") String vehicle,
                               /*@Part("credit_card_type") String credit_type,
                               @Part("credit_card_no") String credit_no,
                               @Part("credit_card_exp") String credit_exp,
                               @Part("credit_card_cvv") String credit_cvv,*/
                               Callback<String> callback);

    @Multipart
    @POST(APIConfig.DRIVER_REGISTER)
    public void driverRegisterWithoutPicture(@Part("name") String name,
                               @Part("email") String email,
                               @Part("password") String password,
                               @Part("mobile_number") String mobileno,
                               @Part("address") String address,
                               @Part("last_ll") String ll,
                               @Part("id_card") String nrc_id,
                               @Part("vehicle") String vehicle,
                               Callback<String> callback);



    @POST(APIConfig.GET_TOKEN)
    public void getToken(@Body TypedInput input, Callback<String> callback);

    @GET(APIConfig.PROFILE_URL)
    public void getProfile(@Query("access_token") String token, Callback<String> callback);

    @FormUrlEncoded
    @PATCH(APIConfig.PROFILE_URL)
    public void updateProfileWithoutPicture(@Query("access_token") String token,
                              @Field("name") String name,
                              @Field("email") String email,
                              @Field("mobile_number") String mobileno,
                              @Field("address") String address,
                              @Field("id_card") String nrc_id,
                              @Field("vehicle") String vehicle,
                              Callback<String> callback);

    @Multipart
    @PATCH(APIConfig.PROFILE_URL)
    public void updateProfile(@Query("access_token") String token,
                              @Part("name") String name,
                              @Part("email") String email,
                              @Part("mobile_number") String mobileno,
                              @Part("address") String address,
                              @Part("pictures") TypedFile pic,
                              @Part("id_card") String nrc_id,
                              @Part("vehicle") String vehicle,
                              Callback<String> callback);


    @POST(APIConfig.LOCATION_REPORT)
    public void updateLocation(@Query("access_token") String token,
                               @Body TypedInput input,
                                Callback<String> callback);

    @POST(APIConfig.GCM_REISTERID)
    public void sendGCMRegisterID(@Query("access_token") String token, @Body TypedInput input, Callback<String> callback);

    @POST(APIConfig.LOGOUT)
    public void Logout(@Query("access_token") String token, Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.JOB_REPORT)
    public void reportJob(@Path("job_id") String id ,@Query("access_token") String token, @Field("message") String message, @Field("price") String price, Callback<String> callback);


    @FormUrlEncoded
    @POST(APIConfig.JOB_REJECT)
    public void rejectJob(@Path("job_id") String id ,@Query("access_token") String token, @Field("message") String message, @Field("nomore") String nomore, Callback<String> callback);


    @FormUrlEncoded
    @POST(APIConfig.JOB_AGREE)
    public void agreeJob(@Path("job_id") String id ,@Query("access_token") String token, @Field("message") String message, Callback<String> callback);


    @FormUrlEncoded
    @PATCH(APIConfig.PROFILE_URL)
    public void updatePassword(@Query("access_token") String token,
                               @Field("old_password") String oldPwd,
                               @Field("password") String newPwd,
                               Callback<String> callback);


    @FormUrlEncoded
    @POST(APIConfig.FORGET_PASSWORD)
    public void forgetPassword(@Field("email") String email,
                               Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.JOB_DELAY_REPORT)
    public void jobDelayReport(@Path("job_id") String id ,@Query("access_token") String token, @Field("location") String location, @Field("timestamp") String ts, @Field("message") String message, Callback<String> callback);


    @GET(APIConfig.PRICE_CATEGORY)
    public void getPriceCategory(Callback<String> callback);

}


