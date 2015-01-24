// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver.api;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import knayi.delevadriver.Config;
import knayi.delevadriver.model.ApiRequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;


// Referenced classes of package knayi.delevadriver.api:
//            RetrofitInterface

public class AvaliableJobsAPI
{


    private static AvaliableJobsAPI mInstance;
    private RetrofitInterface mService;


    public AvaliableJobsAPI(){

    ApiRequestInterceptor request_Interceptor = new ApiRequestInterceptor();



    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setConnectTimeout(7000, TimeUnit.MILLISECONDS);
    okHttpClient.setReadTimeout(7000, TimeUnit.MILLISECONDS);

    final RestAdapter restAdapter = new
            RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.BASIC)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(Config.PRODUCTS_BASE_URL)
            .setRequestInterceptor(request_Interceptor)


            .setClient(new OkClient(okHttpClient))

            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String msg) {
                    // Log.e("//////////////Product API////////////////////////", msg);
                }
            })
            .setConverter(new StringConverter()) //Reply String result
            .build();

    //.setLog(new AndroidLog("YOUR_LOG_TAG"))

    //RestAdapter restAdapterII = new RestAdapter.Builder().setClient(new OkClient(okHttpClient)).build();

        /*new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.BASIC)
            .setEndpoint(Config.BASE_URL)
            .build();*/
    //.setClient(new OkClient(okHttpClient))



        /*final RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setConverter(new StringConverter())
                .setEndpoint(Config.PRODUCTS_BASE_URL).build();
*/
    mService = restAdapter.create(RetrofitInterface.class);



}


    /*RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Authorization","Basic <base64-encoded info@myanmarplus.net:L3bE33t3y16yr4Zk9ft1qhZid0IE938W pair>=");
            request.
            //request.addHeader("API key", "L3bE33t3y16yr4Zk9ft1qhZid0IE938W");
        }
    };*/

    //.setRequestInterceptor(requestInterceptor)


    public static AvaliableJobsAPI getInstance(){
        if(mInstance == null){
            mInstance = new AvaliableJobsAPI();
        }
        return  mInstance;
    }
    public RetrofitInterface getService(){
        return mService;
    }

public static class StringConverter implements Converter {


    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String text = null;
        try {
            text = fromStream(body.in());
        } catch (IOException ignored) {/*NOP*/ }

        return text;
    }

    @Override
    public TypedOutput toBody(Object o) {
        return null;
    }

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }
}
}
