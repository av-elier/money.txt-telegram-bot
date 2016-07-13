package ru.av_elier.money.txt;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by Adelier on 17.10.2015.
 */
public class DbHelper {

    @DatabaseTable
    public static class Token {
        @DatabaseField(id = true)
        public Integer chatId;
        @DatabaseField
        public String accessToken;
    }

    private static String databaseUrl;
    private static String databaseUser;
    private static String databasePassword;

    public static void init() {
        databaseUrl      = System.getenv("DATABASE_URL");
        databaseUser     = System.getenv("DATABASE_USER");
        databasePassword = System.getenv("DATABASE_PASS");

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, databaseUser, databasePassword);
            TableUtils.createTableIfNotExists(connectionSource, Token.class);
            connectionSource.close();
        } catch (SQLException e) {
            // EXISTS // e.printStackTrace();
        }
    }

    public static void saveTokenForChatId(Token token) {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, databaseUser, databasePassword);
            Dao<Token, Integer> accountDao =
                    DaoManager.createDao(connectionSource, Token.class);
            accountDao.create(token);
            Logger.getLogger(DbHelper.class.getName()).info("Saved token to db");
            connectionSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getTokenForChatId(int chatId) {
        Token token = null;
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, databaseUser, databasePassword);
            Dao<Token, Integer> accountDao = DaoManager.createDao(connectionSource, Token.class);
            token = accountDao.queryForId(chatId);
            Logger.getLogger(DbHelper.class.getName()).info("Loaded token from db");
            connectionSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (token != null)
            return token.accessToken;
        else
            return "";
    }
}
