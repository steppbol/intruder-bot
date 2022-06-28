package com.ffanaticism.intruder.telegramhandler.bot;

import com.ffanaticism.intruder.serviceprovider.entity.Characteristic;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.telegramhandler.command.BaseCommand;
import com.ffanaticism.intruder.telegramhandler.entity.CallbackQueryType;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import com.ffanaticism.intruder.telegramhandler.entity.InlineQueryType;
import com.ffanaticism.intruder.telegramhandler.query.callback.BaseCallbackQuery;
import com.ffanaticism.intruder.telegramhandler.query.inline.BaseInlineQuery;
import com.ffanaticism.intruder.telegramhandler.service.CallbackQueryService;
import com.ffanaticism.intruder.telegramhandler.service.CommandService;
import com.ffanaticism.intruder.telegramhandler.service.InlineQueryService;
import com.ffanaticism.intruder.telegramhandler.util.UserCharacteristic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BotContext {
    private final CommandService commandService;
    private final CallbackQueryService callbackQueryService;
    private final InlineQueryService inlineQueryService;
    private final Map<CommandType, BaseCommand> commands = new HashMap<>();
    private final Map<CallbackQueryType, BaseCallbackQuery> callbackQueries = new HashMap<>();
    private final Map<InlineQueryType, BaseInlineQuery> inlineQueries = new HashMap<>();

    @Autowired
    public BotContext(CommandService commandService, List<BaseCommand> commands, CallbackQueryService callbackQueryService, List<BaseCallbackQuery> callbackQueries, InlineQueryService inlineQueryService, List<BaseInlineQuery> inlineQueries) {
        commands.forEach(command -> this.commands.put(command.getCommandType(), command));
        callbackQueries.forEach(callbackQuery -> this.callbackQueries.put(callbackQuery.getCallbackQueryType(), callbackQuery));
        inlineQueries.forEach(inlineQuery -> this.inlineQueries.put(inlineQuery.getInlineQueryType(), inlineQuery));

        this.inlineQueryService = inlineQueryService;
        this.callbackQueryService = callbackQueryService;
        this.commandService = commandService;
    }

    public void handleCommand(Message message) {
        var commandText = message.getText();
        if (commandText != null) {
            var currentCommandType = commandService.getCommandType(commandText);
            if (currentCommandType != null) {
                var command = currentCommandType.name();
                var baseCommand = commands.get(currentCommandType.type());
                if (baseCommand != null) {
                    message.setText(Pattern.compile(command, Pattern.LITERAL
                                    | Pattern.CASE_INSENSITIVE
                                    | Pattern.UNICODE_CASE)
                            .matcher(message.getText())
                            .replaceAll(Matcher.quoteReplacement("")));
                    baseCommand.execute(getUser(message.getFrom()), message);
                }
            }
        }
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        var currentCallbackQueryType = callbackQueryService.getCallbackQueryType(callbackQuery.getData());
        if (currentCallbackQueryType != null) {
            var baseCallbackQuery = callbackQueries.get(currentCallbackQueryType);
            if (baseCallbackQuery != null) {
                baseCallbackQuery.execute(getUser(callbackQuery.getFrom()), callbackQuery);
            }
        }
    }

    public void handleInlineQuery(InlineQuery inlineQuery) {
        var currentInlineQueryType = inlineQueryService.getInlineQueryType(inlineQuery.getQuery());
        if (currentInlineQueryType != null) {
            var baseInlineQuery = inlineQueries.get(currentInlineQueryType);
            if (baseInlineQuery != null) {
                baseInlineQuery.execute(getUser(inlineQuery.getFrom()), inlineQuery);
            }
        }
    }

    private User getUser(org.telegram.telegrambots.meta.api.objects.User user) {
        return User.builder()
                .characteristics(Arrays.asList(
                        new Characteristic(false, UserCharacteristic.USER_NAME, user.getUserName()),
                        new Characteristic(false, UserCharacteristic.FIRST_NAME, user.getFirstName()),
                        new Characteristic(false, UserCharacteristic.LAST_NAME, user.getLastName() != null ? user.getLastName() : "N/A"),
                        new Characteristic(true, UserCharacteristic.USER_ID, user.getId().toString())))
                .build();
    }
}
