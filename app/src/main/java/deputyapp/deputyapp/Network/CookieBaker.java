package deputyapp.deputyapp.Network;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;

public class CookieBaker implements Interceptor {
    private CookieStore cookieStore;

    public CookieBaker(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        for (HttpCookie cookie: cookieStore.getCookies()) {
            cookie.setVersion(0);
        }

        Response response = chain.proceed(chain.request());

        for (HttpCookie cookie: cookieStore.getCookies()) {
            cookie.setVersion(0);
        }

        return response;
    }
}