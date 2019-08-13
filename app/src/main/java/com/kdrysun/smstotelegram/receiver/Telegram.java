package com.kdrysun.smstotelegram.receiver;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

public class Telegram {

    public void send(String message) {
        TelegramBot bot = new TelegramBot("917964506:AAFmTrHNH6AX7ViqxI0zWFRERSCnjY1-tWI");
        bot.execute(new SendMessage(-1001333464175L, message));
    }
}
