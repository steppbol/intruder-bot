package com.ffanaticism.intruder.telegramhandler.util;

import com.ffanaticism.intruder.serviceprovider.exception.CastingException;
import com.ffanaticism.intruder.serviceprovider.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public abstract class BotUtil {
    public static final int DOWNLOAD_MESSAGE_UPDATE_RATE = 50;
    public static final Pattern TIME_REGEX = Pattern.compile("\\d*:\\d*|\\d*");
    public static final Pattern SCOPE_REGEX = Pattern.compile("(?<=\\[)(.*?)(?=])", Pattern.DOTALL);

    public static String fetchArgument(String[] strings, int index) {
        return strings == null || strings.length < index + 1 ? "" : strings[index];
    }

    public static Duration getOffsetAndDuration(String[] text, int offsetIndex, int durationIndex) {
        var offset = 0f;
        var duration = 0f;
        try {
            var offsetSeconds = BotUtil.getSubstringFromArgumentByRegex(BotUtil.fetchArgument(text, offsetIndex), BotUtil.TIME_REGEX);
            offset = TimeUtil.getOffsetInSeconds(offsetSeconds);
            duration = TimeUtil.getDurationInSeconds(BotUtil.getSubstringFromArgumentByRegex(BotUtil.fetchArgument(text, durationIndex), BotUtil.TIME_REGEX), offsetSeconds);
        } catch (CastingException e) {
            log.error("Parsing offset and duration is failed");
            e.printStackTrace();
        }

        return new Duration(offset, duration);
    }

    public static String getSubstringFromArgumentByRegex(String text, Pattern pattern) {
        var substring = "";
        var matcher = pattern.matcher(text);
        if (matcher.find()) {
            substring = matcher.group();
        }

        return substring;
    }

    public static ReplyKeyboard buildReplyMarkup(String text, String callbackData) {
        var markupInline = new InlineKeyboardMarkup();
        var rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        var rowInline = new ArrayList<InlineKeyboardButton>();

        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);

        rowInline.add(inlineKeyboardButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public record Duration(float offset, float duration) {
    }
}
