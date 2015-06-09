// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver;


public class APIConfig
{

    //public static final String BASE_URL = "kny.co:3000/api/v1.0/driver";

    //public static final String BASE_URL = "http://10.56.57.159:3001/api/v1.1/driver";
    public static final String BASE_URL = "http://deleva.sg/api/v1.2";

    public static final String DOMAIN_URL = "http://deleva.sg";
    //public static final String DOMAIN_URL = "http://kny.co:3001";

    //public static final String BASE_URL = "http://deleva.sg:3001/api/v1.1/driver";
    public static final String JOBS_BASE_URL = "/driver/jobs";
    public static final String MY_JOBS_URL = "/driver/jobs/me";
    public static final String JOB_DETAIL_URL = "/driver/jobs/{id}";
    public static final String ACCEPT_JOB_URL = "/driver/jobs/{id}/accept";
    public static final String REJECT_JOB_URL = "/driver/jobs/{id}/reject";
    public static final String JOB_DONE_URL = "/driver/jobs/{id}/done";
    public static final String DRIVER_REGISTER = "/driver/register";
    public static final String GET_TOKEN = "/driver/getToken";
    public static final String PROFILE_URL = "/driver/me";
    public static final String LOCATION_REPORT = "/driver/location_report";
    public static final String GCM_REISTERID = "/driver/key_update";
    public static final String LOGOUT = "/logout";
    public static final String JOB_REJECT = "/driver/jobs/{job_id}/reject";
    public static final String JOB_REPORT = "/driver/jobs/{job_id}/price_nego";
    public static final String JOB_AGREE = "/driver/jobs/{job_id}/agree";
    public static final String FORGET_PASSWORD = "/driver/forget";
    public static final String JOB_DELAY_REPORT = "/driver/jobs/{job_id}/report";
    public static final String PRICE_CATEGORY = "/price_category.json";



    public static final String TEST_URL = "/seed_data";

    public APIConfig()
    {
    }

}
