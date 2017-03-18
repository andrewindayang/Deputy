package deputyapp.deputyapp.Network;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.greenrobot.event.EventBus;
import deputyapp.deputyapp.BuildConfig;
import deputyapp.deputyapp.DeputyApplication;
import deputyapp.deputyapp.Fragment.StartEndShiftFragment;
import deputyapp.deputyapp.Model.PostShift;
import deputyapp.deputyapp.Network.gson.DateTimeDeserializer;
import deputyapp.deputyapp.Network.service.DeputyService;
import deputyapp.deputyapp.Util.AppConstant;
import deputyapp.deputyapp.Util.BasicEvent;
import deputyapp.deputyapp.dao.Business;
import deputyapp.deputyapp.dao.PrevShiftsData;
import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();
    private final DeputyApplication application = (DeputyApplication) DeputyApplication.getContext();
    private static NetworkManager instance = null;
    private GsonConverter gsonConverter = new GsonConverter(getGSONBuilder());
    private DeputyService deputyService;
    private static final int LONG_LOG_CHUNK_SIZE = 3000;
    private ModelManager manager = ModelManager.getInstance();
    private Date lastNotificationSentDate = null;

    private NetworkManager() {
        createDeputyService();
    }

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public Date getLastNotificationSentDate() {
        return lastNotificationSentDate;
    }

    public void setLastNotificationSentDate(Date newDate) {
        lastNotificationSentDate = newDate;
    }

    private OkHttpClient createHTTPClient() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setReadTimeout(90, TimeUnit.SECONDS);
        httpClient.setConnectTimeout(90, TimeUnit.SECONDS);
        httpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
        setSSLFactoryForClient(httpClient);
        return httpClient;
    }

    private CookieManager createCookieManger() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return cookieManager;
    }

    private FieldNamingStrategy getGSONStrategy() {
        FieldNamingStrategy strategy = new FieldNamingStrategy() {
            @Override
            public String translateName(Field field) {
                if (field.getName().equalsIgnoreCase("default_location")) {
                    return "default";
                } else {
                    return field.getName();
                }
            }
        };
        return strategy;
    }

    private Gson getGSONBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setFieldNamingStrategy(getGSONStrategy())
                .create();
    }

    private RestAdapter.Log getRestLog() {
        return new RestAdapter.Log() {
            @Override
            public void log(String message) {
                String tmpString = message;
                while (tmpString.length() > LONG_LOG_CHUNK_SIZE) {
                    Log.d(TAG, tmpString.substring(0, LONG_LOG_CHUNK_SIZE));
                    tmpString = tmpString.substring(LONG_LOG_CHUNK_SIZE);
                }
                Log.d(TAG, tmpString);
            }
        };
    }

    private ErrorHandler getRestErrorHandler() {
        return new ErrorHandler() {
            @Override
            public Throwable handleError(RetrofitError cause) {
                Response r = cause.getResponse();
                if (cause.isNetworkError()) {

                } else {
                    if (r != null && r.getStatus() == 300) {
                        // 403 Error codes can be safely shown to the user, convert response to json.
                        RetrofitError error403 = RetrofitError.httpError(r.getUrl(), r, gsonConverter, JsonObject.class);
                        return new Error403Exception(cause, ((JsonObject) error403.getBodyAs(JsonObject.class)).get("error").getAsString());
                    } else {

                    }
                }
                return cause;
            }
        };
    }

    private void createDeputyService() {
        final OkHttpClient okHttpClient = createHTTPClient();
        //set cookie
        CookieManager managerCookie = createCookieManger();
        okHttpClient.setCookieHandler(managerCookie);
        okHttpClient.interceptors().add(new CookieBaker(managerCookie.getCookieStore()));


        RequestInterceptor restAuthorizationInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Deputy " + manager.getSHA1().get(0).getSha1().toString());
            }
        };

        ErrorHandler restErrorHandler = getRestErrorHandler();
        Executor executor = Executors.newCachedThreadPool();

        RestAdapter.Log restLog = getRestLog();

        //Don't forget to remove Interceptors (or change Logging Level to NONE) in production!
        //Otherwise people will be able to see your request and response on Log.
        //I have handle in there if its debug it will be basic otherwise will be NONE
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(restAuthorizationInterceptor)
                .setConverter(gsonConverter)
                .setErrorHandler(restErrorHandler)
                .setExecutors(executor, executor)
                .setClient(new OkClient(okHttpClient))
                .setLog(restLog)
                .setEndpoint(AppConstant.baseURL)
                .build();


        deputyService = restAdapter.create(DeputyService.class);
    }

    public static class Error403Exception extends RuntimeException {
        private final String serverMessage;

        public Error403Exception(Exception e, String message) {
            super(e);
            this.serverMessage = message;
        }

        @Override
        public String getMessage() {
            return this.serverMessage;
        }
    }

    // SET SSL
    public static OkClient setSSLFactoryForClient(OkHttpClient client) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            client.setSslSocketFactory(sslSocketFactory);
            client.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new OkClient(client);
    }


    public void getBusiness() {
        deputyService.getBusiness(new Callback<Business>() {
            @Override
            public void success(Business business, Response response) {
                manager.removeAllBusinessData();
                if(business != null){
                    manager.insertBusinessToDB(business);
                }
                EventBus.getDefault().post(BasicEvent.BUSINESS_LOADED);
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(BasicEvent.BUSINESS_LOADED_ERROR);
            }
        });
    }


    public void postStartShift(PostShift postShift,Callback<String> callback) {
        deputyService.postShiftStart(postShift,callback);
    }


    public void postEndShift(PostShift postShift,Callback<String> callback) {
        deputyService.postShiftEnd(postShift,callback);
    }

    public void getPrevShifts() {
        deputyService.getPreviousShift(new Callback<List<PrevShiftsData>>() {
            @Override
            public void success(List<PrevShiftsData> prevShiftsDatas, Response response) {
                if(prevShiftsDatas != null){
                    manager.removeAllPreviousShifts();
                    for(PrevShiftsData prevShiftsData : prevShiftsDatas){
                        PrevShiftsData newPrevShiftsData = new PrevShiftsData();
                        newPrevShiftsData.setEnd(prevShiftsData.getEnd());
                        newPrevShiftsData.setStart(prevShiftsData.getStart());
                        newPrevShiftsData.setEndLatitude(prevShiftsData.getEndLatitude());
                        newPrevShiftsData.setEndLongitude(prevShiftsData.getEndLongitude());
                        newPrevShiftsData.setStartLatitude(prevShiftsData.getStartLatitude());
                        newPrevShiftsData.setStartLongitude(prevShiftsData.getStartLongitude());
                        newPrevShiftsData.setImage(prevShiftsData.getImage());
                        manager.insertPrevShift(newPrevShiftsData);
                    }
                    EventBus.getDefault().post(BasicEvent.PREV_SHIFTS_LOADED);
                }else{
                    EventBus.getDefault().post(BasicEvent.PREV_SHIFTS_LOADED_NULL);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(BasicEvent.PREV_SHIFTS_LOADED_ERROR);
            }
        });
    }
}
