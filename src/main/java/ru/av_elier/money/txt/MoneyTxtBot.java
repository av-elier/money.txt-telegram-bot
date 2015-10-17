package ru.av_elier.money.txt;

import com.dropbox.core.DbxException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Adelier on 16.10.2015.
 */
public class MoneyTxtBot {
    private static class Commands {
        static String TODAY = "/today";
        static String DROPBOX = "/dropbox";
        static String LAST = "/last";
    }

    private enum State {DEFAULT, DATA_READY, CATEGORY_READY, GETTING_DROPBOX_CODE};
    private static Map<Integer, State> states; // TODO store
    private static Map<Integer, OAuthWebController> chatDropboxControllers; // do not store


    private static Integer latest_update = null;

    private static final boolean DEBUG = false;

    public static void main(String[] args) throws InterruptedException {
        DbHelper.init();
        states = new HashMap<>();
        chatDropboxControllers = new HashMap<>();
        infiniteTelegramBotGetUpdates();
    }

    private static void infiniteTelegramBotGetUpdates() throws InterruptedException {
        final String BOT_TOKEN = System.getenv().get("BOT_TOKEN");
        if (BOT_TOKEN == null)
            return;
        TelegramBot bot = TelegramBotAdapter.build(BOT_TOKEN);

        while (true)
        {
            GetUpdatesResponse upd = bot.getUpdates(latest_update, 1, null);
            if (upd.result.size() == 0)
                continue;

            Update u = upd.result.get(0);
            processUpdate(bot, u);

            Thread.sleep(1000);
        }
    }

    private static void processUpdate(TelegramBot bot, Update u) {
        if (!states.containsKey(u.message.chat.id))
            states.put(u.message.chat.id, State.DEFAULT);
        latest_update = u.update_id + 1; // in advance
        switch (states.get(u.message.chat.id)) {
            case DEFAULT:
                processUpdateDefault(bot, u);
                break;
            case DATA_READY:
                processUpdateDateReady(bot, u);
                break;
            case CATEGORY_READY:
                processUpdateCategoryReady(bot, u);
                break;
            case GETTING_DROPBOX_CODE:
                processUpdateDropboxContinued(bot, u);
                break;
            default:
                states.put(u.message.chat.id, State.DEFAULT);
                bot.sendMessage(u.message.chat.id,
                        "HOW DID YOU GET THERE?? Wow! Go back to default state",
                        null, null, null);
        }
    }

    private static void processUpdateDefault(TelegramBot bot, Update u) {
        if (DEBUG){ // response what we hear
            String resp = String.format( "upd %d: from %d %s %s @%s - text: \"%s\"",
                u.update_id, u.message.from.id, u.message.from.first_name, u.message.from.last_name,
                u.message.from.username, u.message.text );
            bot.sendMessage(u.message.chat.id,
                    "We hear: " + resp,
                    null, null, null);
        }

        String updText = u.message.text;
        if (updText.equals(Commands.DROPBOX)) {
            processUpdateCmdDropbox(bot, u);
        }
        else if (updText.startsWith(Commands.LAST)) {
            processUpdateCmdLast(bot, u);
        }
        else if (updText.equals(Commands.TODAY)) {
            processUpdateCmdToday(bot, u);
        } else {
            processFullMoneyLine(bot, u);
        }
    }

    private static void processUpdateCmdDropbox(TelegramBot bot, Update u) {
        OAuthWebController oauthControl = new OAuthWebController();
        String authorizeUrl = oauthControl.getAuthUtl();
        bot.sendMessage(u.message.chat.id,
                "Go to: " + authorizeUrl + "\nAllow me to store money.txt in dropbox (you might have to log in)\nSend the code to me.",
                null, null, null);
        chatDropboxControllers.put(u.message.chat.id, oauthControl);
        states.put(u.message.chat.id, State.GETTING_DROPBOX_CODE);
    }
    private static void processUpdateDropboxContinued(TelegramBot bot, Update u) {
        OAuthWebController oauthControl = chatDropboxControllers.get(u.message.chat.id);
        String code = u.message.text;
        try {
            String accessToken = oauthControl.completeOAuth(code);
            DbHelper.Token token = new DbHelper.Token();
            token.chatId = u.message.chat.id;
            token.accessToken = accessToken;
            DbHelper.saveTokenForChatId(token);
            bot.sendMessage(u.message.chat.id,
                    "Dropbox connected, Yay!",
                    null, null, null);
        } catch (DbxException e) {
            bot.sendMessage(u.message.chat.id,
                "Something went wrong with dropbox authorisation (" + e.getMessage() + ")",
                null, null, null);
        }
        chatDropboxControllers.remove(u.message.chat.id);
        states.put(u.message.chat.id, State.DEFAULT);
    }


    private static void processUpdateCmdLast(TelegramBot bot, Update u) {
        int limit;
        try {
            String sRequestedCount = u.message.text.split(" ")[1];
            limit = Integer.parseInt(sRequestedCount);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            limit = 7;
        }
        if (limit > 20)
            limit = 20;

        TxtCommunicator txtComm = getTxtCommunicator(u.message.chat.id);
        List<MoneyTxtLine> last = txtComm.getLastLines(limit);

        String lastList = last.stream().map(line -> line.toString()).collect(Collectors.joining("\n"));

        bot.sendMessage(u.message.chat.id,
                String.format("Here is last %d records:\n\n", limit) + lastList,
                null, null, null);
    }

    private static void processUpdateCmdToday(TelegramBot bot, Update u) {
        bot.sendMessage(u.message.chat.id,
                "Date today (by the way, server 'today', not necessarily yours). Continue with category, exprassion" +
                        " and comment (and tell @av_elier what these parts are, and how regexpr should look like)",
                null, null, null);
        states.put(u.message.chat.id, State.DEFAULT);
//        states.put(u.message.chat.id, State.CATEGORY_READY);
    }

    private static void processUpdateDateReady(TelegramBot bot, Update u) {
        bot.sendMessage(u.message.chat.id,
                "no custom keyboard yet",
                null, null, null);
        states.put(u.message.chat.id, State.CATEGORY_READY);
    }

    private static void processUpdateCategoryReady(TelegramBot bot, Update u) {

    }

    private static void processFullMoneyLine(TelegramBot bot, Update u) {
        MoneyTxtLine line = null;
        try {
            line = new MoneyTxtLine(u.message.text);
        } catch (ParseException e) {
            bot.sendMessage(u.message.chat.id,
                    "Say what? Please enter expense lines in format like this\nyyyy.MM.dd category 1+2*3 note",
                    null, null, null);
            return;
        }

        if (line != null) {
            TxtCommunicator txtComm = getTxtCommunicator(u.message.chat.id);
            txtComm.addLine(line);
            bot.sendMessage(u.message.chat.id,
                    "Added line " + line.toString(),
                    null, null, null);
        }
    }

    private static TxtCommunicator getTxtCommunicator(int chatId) {
        return new DropboxTxtCommunicator(chatId); // TODO change fo Dropbox communicator
    }
}
