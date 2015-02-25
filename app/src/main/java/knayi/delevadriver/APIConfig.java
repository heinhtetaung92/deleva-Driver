// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver;


public class APIConfig
{

    //public static final String BASE_URL = "kny.co:3000/api/v1.0/driver";

    //public static final String BASE_URL = "http://10.56.57.159:3001/api/v1.1/driver";
    public static final String BASE_URL = "http://kny.co:3001/api/v1.1/driver";
    public static final String JOBS_BASE_URL = "/jobs";
    public static final String MY_JOBS_URL = "/jobs/me";
    public static final String JOB_DETAIL_URL = "/jobs/{id}";
    public static final String ACCEPT_JOB_URL = "/jobs/{id}/accept";
    public static final String REJECT_JOB_URL = "/jobs/{id}/reject";
    public static final String JOB_DONE_URL = "/jobs/{id}/done";
    public static final String DRIVER_REGISTER = "/register";
    public static final String GET_TOKEN = "/getToken";
    public static final String PROFILE_URL = "/me";
    public static final String LOCATION_REPORT = "/location_report";


    public static final String TEST_URL = "/seed_data";

    public APIConfig()
    {
    }
}
