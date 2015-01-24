// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver.api;


import knayi.delevadriver.APIConfig;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RetrofitInterface
{

    @GET(APIConfig.JOBS_BASE_URL)
    public void getJobListByLocation(@Query("") String s, @Query("") String s1, Callback<String> callback);

}
