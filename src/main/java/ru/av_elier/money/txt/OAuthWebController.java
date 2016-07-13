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
}
