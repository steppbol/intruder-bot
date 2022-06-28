package com.ffanaticism.intruder.telegramhandler.query.inline.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.telegramhandler.entity.InlineQueryType;
import com.ffanaticism.intruder.telegramhandler.query.inline.BaseInlineQuery;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmptyInlineQuery implements BaseInlineQuery {
    private final BotService botService;
    private final MessageService messageService;

    @Autowired
    public EmptyInlineQuery(BotService botService, MessageService messageService) {
        this.botService = botService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(User user, InlineQuery inlineQuery) {
        var answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setResults(buildInlineQueryResult());
        answerInlineQuery.setCacheTime(1);

        botService.answerInlineQuery(answerInlineQuery, inlineQuery.getFrom(), getInlineQueryType().toString());
    }

    @Override
    public InlineQueryType getInlineQueryType() {
        return InlineQueryType.EMPTY;
    }

    private List<InlineQueryResult> buildInlineQueryResult() {
        List<InlineQueryResult> inlineQueryResultArticles = new ArrayList<>();

        inlineQueryResultArticles.add(getInlineQueryResultArticle(
                messageService.getText("inline_query.help"),
                messageService.getText("inline_query.help.description"),
                "1",
                messageService.getText("reply.help_message")));

        inlineQueryResultArticles.add(getInlineQueryResultArticle(
                messageService.getText("inline_query.search_audio"),
                messageService.getText("inline_query.search_audio.description"),
                "2",
                messageService.getText("inline_query.search_audio.answer")));

        inlineQueryResultArticles.add(getInlineQueryResultArticle(
                messageService.getText("inline_query.search_video"),
                messageService.getText("inline_query.search_video.description"),
                "3",
                messageService.getText("inline_query.search_video.answer")));

        return inlineQueryResultArticles;
    }

    private InlineQueryResult getInlineQueryResultArticle(String title, String description, String id, String textMessageContent) {
        var inlineQueryResultArticle = new InlineQueryResultArticle();
        inlineQueryResultArticle.setId(id);
        inlineQueryResultArticle.setTitle(title);
        inlineQueryResultArticle.setDescription(description);

        var inputTextMessageContent = new InputTextMessageContent();

        inputTextMessageContent.setParseMode("HTML");
        inputTextMessageContent.setMessageText(textMessageContent);

        inlineQueryResultArticle.setInputMessageContent(inputTextMessageContent);

        return inlineQueryResultArticle;
    }
}
