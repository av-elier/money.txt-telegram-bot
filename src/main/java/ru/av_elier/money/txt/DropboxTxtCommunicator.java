package ru.av_elier.money.txt;

import com.dropbox.core.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adelier on 16.10.2015.
 */
public class DropboxTxtCommunicator extends TxtCommunicator {
    DbxClient client;

    public DropboxTxtCommunicator(int chatId) {
        DbxRequestConfig config = new DbxRequestConfig(
            "MoneyTxtBot/1.0", Locale.getDefault().toString());
        String accessToken = DbHelper.getTokenForChatId(chatId); // TODO get stored token or ask for a new

        this.client = new DbxClient(config, accessToken);
    }

    @Override
    public void addLine(MoneyTxtLine line) {
        try {
            File tempFile = File.createTempFile("money.txt", null);

            DbxEntry.File downloadedFile;
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));) {
                downloadedFile = client.getFile("/money.txt", null, outputStream);
            }
            new LocalTxtCommunicator(tempFile).addLine(line);
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(tempFile), 1024)) {
                DbxEntry.File uploadedFile = client.uploadFile("/money.txt",
                        DbxWriteMode.update(downloadedFile.rev), tempFile.length(), inputStream);
            }
            tempFile.delete();
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<MoneyTxtLine> getLastLines(int limit) {
        try {
            File tempFile = File.createTempFile("money.txt", null);
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));) {
                DbxEntry.File downloadedFile = client.getFile("/money.txt", null, outputStream);
            }
            List<MoneyTxtLine> result = new LocalTxtCommunicator(tempFile).getLastLines(limit);
            tempFile.delete();
            return result;
        } catch (IOException | DbxException e) {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }
}
