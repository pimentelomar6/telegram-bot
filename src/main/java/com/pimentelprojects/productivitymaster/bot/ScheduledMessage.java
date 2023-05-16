package com.pimentelprojects.productivitymaster.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduledMessage {

    public static void sendMessage(Date date, String msj, String chatId, TelegramBot bot){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    bot.sendNotification(chatId, msj);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }, date);
    }
}
