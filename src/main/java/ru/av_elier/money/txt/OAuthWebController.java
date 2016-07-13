package ru.av_elier.money.txt;

import static spark.Spark.*;
import com.dropbox.core.*;
import org.apache.commons.io.FileUtils;
import spark.Route;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Adelier on 16.10.2015.
 */
public class OAuthWebController {

    private static DbxRequestConfig config = new DbxRequestConfig(
            "MoneyTxtBot/1.0", Locale.getDefault().toString());

    public static DbxRequestConfig getConfig() {
        return config;
    }

    private String dbxWebAuth = null;
    DbxWebAuthNoRedirect webAuth = null;

    public String getAuthUtl() {
        // Get your app key and secret from the Dropbox developers website.
        final String APP_KEY = System.getenv("DROPBOX_APP_KEY");
        final String APP_SECRET = System.getenv("DROPBOX_APP_SECRET");
        final String REDIRECT_URL = System.getenv("DROPBOX_APP_REDIRECT");

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        String authorizeUrl = webAuth.start();


        return authorizeUrl;
    }
    public String completeOAuth(String code) throws DbxException {
        DbxAuthFinish authFinish = webAuth.finish(code);
        String dbxUserId = authFinish.userId;
        String accessToken = authFinish.accessToken;
        return accessToken;
    }

//    public void init() throws IOException, DbxException {
////        System.out.println("1. Go to: " + authorizeUrl);
////        System.out.println("2. Click \"Allow\" (you might have to log in first)");
////        System.out.println("3. Copy the authorization code.");
//        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
//
//
//    }
//
//    public void start() {
//        port(Integer.valueOf(System.getenv("PORT")));
//        get("/dropbox/", (req, res) -> {
//            // save something
//            Set<String> queryParams = req.queryParams();
//            Map<String, String[]> parameterMap = new HashMap<String, String[]>(queryParams.size());
//            for (String param : queryParams) {
//                parameterMap.put(param, req.queryParamsValues(param));
//            }
//
//
//            // and return user to the chat
//            res.redirect("https://telegram.me/MoneyTxtBot");
//            return null;
//        });
//
//    }
//    public void stop() {
//        stop();
//    }
}
