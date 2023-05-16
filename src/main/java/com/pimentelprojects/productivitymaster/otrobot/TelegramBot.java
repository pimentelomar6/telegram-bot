package com.pimentelprojects.productivitymaster.otrobot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.Serializable;


public class TelegramBot extends BotApiMethod {
    @Override
    public String getMethod() {
        return "sendMessage";
    }

    @Override
    public Serializable deserializeResponse(String s) throws TelegramApiRequestException {
        return null;
    }
}
