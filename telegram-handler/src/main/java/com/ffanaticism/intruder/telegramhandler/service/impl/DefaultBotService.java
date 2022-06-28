package com.ffanaticism.intruder.telegramhandler.service.impl;

import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.entity.FileEntity;
import com.ffanaticism.intruder.serviceprovider.entity.Image;
import com.ffanaticism.intruder.serviceprovider.entity.StreamedEntity;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.exception.CastingException;
import com.ffanaticism.intruder.serviceprovider.exception.LargeMediaFileException;
import com.ffanaticism.intruder.serviceprovider.exception.MemoryLimitExceededException;
import com.ffanaticism.intruder.serviceprovider.exception.NotDownloadableException;
import com.ffanaticism.intruder.serviceprovider.exception.NotSupportedPlatformException;
import com.ffanaticism.intruder.serviceprovider.exception.SearchingException;
import com.ffanaticism.intruder.serviceprovider.util.format.AudioFormat;
import com.ffanaticism.intruder.serviceprovider.util.format.ImageFormat;
import com.ffanaticism.intruder.serviceprovider.util.format.VideoFormat;
import com.ffanaticism.intruder.telegramhandler.bot.BotFacade;
import com.ffanaticism.intruder.telegramhandler.client.TelegramWebClient;
import com.ffanaticism.intruder.telegramhandler.config.TelegramHandlerProperty;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
public class DefaultBotService implements BotService {
    private static final long FUTURE_TIMEOUT_MINUTE = 15;

    private final BotFacade botFacade;
    private final MessageService messageService;
    private final TelegramHandlerProperty properties;
    private final TelegramWebClient telegramWebClient;

    @Autowired
    public DefaultBotService(@Lazy BotFacade botFacade, MessageService messageService, TelegramHandlerProperty properties, TelegramWebClient telegramWebClient) {
        this.botFacade = botFacade;
        this.messageService = messageService;
        this.properties = properties;
        this.telegramWebClient = telegramWebClient;
    }

    @Override
    public BotApiMethod<?> onUpdate(Update update) {
        return botFacade.onUpdate(update);
    }

    @Override
    public void setWebhook() {
        telegramWebClient.setWebhookPath(properties.getBotWebhookPath());
    }

    @Override
    public Message sendMessage(String text, String chatId, User from, String eventType) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableHtml(true);

        return botFacade.sendMessage(sendMessage, from, eventType);
    }

    @Override
    public void editMessage(String text, String chatId, Integer messageId, User from, String eventType) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        editMessageText.enableHtml(true);

        botFacade.editMessage(editMessageText, from, eventType);
    }

    @Override
    public void deleteMessage(String chatId, Integer messageId, User from, String eventType) {
        var deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        botFacade.deleteMessage(deleteMessage, from, eventType);
    }

    @Override
    public Message sendImage(StreamedEntity image, String chatId, User from, String eventType) {
        var sendImage = new SendPhoto();
        sendImage.setChatId(chatId);
        if (image.getInputStream() != null) {
            sendImage.setPhoto(new InputFile(image.getInputStream(), image.getName() + "." + ImageFormat.JPG_OUTPUT_FILE_FORMAT));
        } else {
            sendImage.setPhoto(new InputFile(image.getUrl()));
        }

        return botFacade.sendImage(sendImage, from, eventType);
    }

    @Override
    public Message sendVideo(StreamedEntity video, String chatId, User from, String eventType) {
        var sendVideo = new SendVideo();
        sendVideo.setChatId(chatId);
        if (video.getInputStream() != null) {
            sendVideo.setVideo(new InputFile(video.getInputStream(), video.getName() + "." + VideoFormat.MP4_OUTPUT_FILE_FORMAT));
        } else {
            sendVideo.setVideo(new InputFile(video.getUrl()));
        }

        return botFacade.sendVideo(sendVideo, from, eventType);
    }

    @Override
    public Message sendAudio(StreamedEntity audio, String chatId, User from, String eventType, ReplyKeyboard replyKeyboard) {
        var sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setThumb(new InputFile(audio.getCoverStream(), "cover" + "." + ImageFormat.JPG_OUTPUT_FILE_FORMAT));

        if (audio.getInputStream() != null && !audio.isDirect()) {
            sendAudio.setAudio(new InputFile(audio.getInputStream(), audio.getName() + "." + AudioFormat.MP3_OUTPUT_FILE_FORMAT));
        } else if (audio.isDirect()) {
            sendAudio.setAudio(new InputFile(audio.getUrl()));
        }

        if (replyKeyboard != null) {
            sendAudio.setReplyMarkup(replyKeyboard);
        }

        return botFacade.sendAudio(sendAudio, from, eventType);
    }

    @Override
    public void sendAudio(String fileId, List<MessageEntity> messageEntities, String caption, User from, String eventType) {
        var sendAudio = new SendAudio();
        sendAudio.setChatId(properties.getBotChannelId());
        sendAudio.setAudio(new InputFile(fileId));
        sendAudio.setCaption(caption);
        sendAudio.setCaptionEntities(messageEntities);

        botFacade.sendAudio(sendAudio, from, eventType);
    }

    @Override
    public Message sendVoice(StreamedEntity audio, String chatId, User from, String eventType) {
        var sendVoice = new SendVoice();
        sendVoice.setChatId(chatId);
        sendVoice.setVoice(new InputFile(audio.getInputStream(), audio.getName() + "." + AudioFormat.OGG_OUTPUT_FILE_FORMAT));

        return botFacade.sendVoice(sendVoice, from, eventType);
    }

    @Override
    public Message sendAnimation(StreamedEntity animation, String chatId, User from, String eventType) {
        var sendAnimation = new SendAnimation();
        sendAnimation.setChatId(chatId);
        sendAnimation.setAnimation(new InputFile(animation.getInputStream(), animation.getName() + "." + ImageFormat.GIF_OUTPUT_FILE_FORMAT));

        return botFacade.sendAnimation(sendAnimation, from, eventType);
    }

    @Override
    public void editMessageReplyMarkup(String chatId, Integer messageId, String inlineMessageId, InlineKeyboardMarkup inlineKeyboardMarkup, User from, String eventType) {
        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setInlineMessageId(inlineMessageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        botFacade.editMessageReplyMarkup(editMessageReplyMarkup, from, eventType);
    }

    @Override
    public void downloadImage(CompletableFuture<Image> downloadFuture, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType) {
        downloadFuture
                .thenAccept(sendFunction::apply)
                .orTimeout(FUTURE_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .exceptionally(ex -> handleException(ex, chatId, from, eventType));
    }

    @Override
    public void downloadVideo(Function<Integer, CompletableFuture<Video>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType) {
        var messageId = sendMessage(messageService.getText("reply.start_downloading"), chatId, from, eventType).getMessageId();
        downloadFunction.apply(messageId)
                .thenAccept(media -> sendMedia(media, sendFunction, messageId, chatId, from, eventType))
                .orTimeout(FUTURE_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .exceptionally(ex -> handleException(ex, messageId, chatId, from, eventType));
    }

    @Override
    public void downloadAudio(Function<Integer, CompletableFuture<Audio>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType) {
        var messageId = sendMessage(messageService.getText("reply.start_downloading"), chatId, from, eventType).getMessageId();
        downloadFunction.apply(messageId)
                .thenAccept(media -> sendMedia(media, sendFunction, messageId, chatId, from, eventType))
                .orTimeout(FUTURE_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .exceptionally(ex -> handleException(ex, messageId, chatId, from, eventType));
    }

    @Override
    public void searchVideo(Supplier<CompletableFuture<List<Video>>> searchSupplier, Function<Video, Function<Integer, CompletableFuture<Video>>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType) {
        var message = sendMessage(messageService.getText("reply.start_searching"), chatId, from, eventType);
        var messageId = message.getMessageId();

        searchSupplier.get()
                .thenCompose(foundVideo -> {
                    if (foundVideo.size() == 0) {
                        throw new CompletionException(new SearchingException());
                    } else {
                        var video = foundVideo.get(0);
                        sendMessage(video.getUrl(), chatId, from, eventType);
                        return downloadFunction.apply(video).apply(messageId);
                    }
                })
                .thenAccept(media -> sendMedia(media, sendFunction, messageId, chatId, from, eventType))
                .orTimeout(FUTURE_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .exceptionally(ex -> handleException(ex, messageId, chatId, from, eventType));
    }

    @Override
    public void searchAudio(Supplier<CompletableFuture<List<Video>>> searchSupplier, Function<Video, Function<Integer, CompletableFuture<Audio>>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType) {
        var messageId = sendMessage(messageService.getText("reply.start_searching"), chatId, from, eventType).getMessageId();

        searchSupplier.get()
                .thenCompose(foundVideo -> {
                    if (foundVideo.size() > 0) {
                        var video = foundVideo.get(0);
                        sendMessage(video.getUrl(), chatId, from, eventType);
                        return downloadFunction.apply(video).apply(messageId);
                    } else {
                        throw new CompletionException(new SearchingException());
                    }
                })
                .thenAccept(media -> sendMedia(media, sendFunction, messageId, chatId, from, eventType))
                .orTimeout(FUTURE_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .exceptionally(ex -> handleException(ex, messageId, chatId, from, eventType));
    }

    @Override
    public void answerInlineQuery(AnswerInlineQuery answerInlineQuery, User from, String eventType) {
        botFacade.answerInlineQuery(answerInlineQuery, from, eventType);
    }

    @Override
    public void sendSearchAudioAnswerInlineQuery(String queryId, List<Video> video, int offset, int resultSize, User from, String eventType) {
        sendSearchMediaAnswerInlineQuery(queryId, messageService.getText("command.audio"), messageService.getText("callback_query.download_audio_private"), video, offset, resultSize, from, eventType);
    }

    @Override
    public void sendSearchVideoAnswerInlineQuery(String queryId, List<Video> video, int offset, int resultSize, User from, String eventType) {
        sendSearchMediaAnswerInlineQuery(queryId, messageService.getText("command.video"), messageService.getText("callback_query.download_video_private"), video, offset, resultSize, from, eventType);
    }

    private void sendSearchMediaAnswerInlineQuery(String queryId, String messageText, String callbackDataText, List<Video> video, int offset, int resultSize, User from, String eventType) {
        List<InlineQueryResult> inlineQueryResultArticles = new ArrayList<>();

        var filteredResults = video.stream().skip(offset - resultSize).toList();
        var id = offset - resultSize + 1;
        for (var result : filteredResults) {
            inlineQueryResultArticles.add(buildInlineQueryResultArticle(result, id++, messageText, callbackDataText));
        }

        var answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(queryId);
        answerInlineQuery.setResults(inlineQueryResultArticles);
        answerInlineQuery.setCacheTime(1);
        answerInlineQuery.setNextOffset(String.valueOf(offset + resultSize));
        answerInlineQuery(answerInlineQuery, from, eventType);
    }

    private void sendMedia(FileEntity media, Function<FileEntity, Message> sendFunction, Integer updateMessageId, String chatId, User from, String eventType) {
        try {
            if (media != null) {
                editMessage(messageService.getText("reply.start_sending"), chatId, updateMessageId, from, eventType);
                var message = sendFunction.apply(media);
                if (message != null) {
                    log.info("Media file sent, chat id: {}, user: {}, user id: {}, event type: {}. Success", chatId,
                            from.getUserName(), from.getId(), eventType);
                } else {
                    log.error("Media file sent, chat id: {}, user: {}, user id: {}, event type: {}. Failed", chatId,
                            from.getUserName(), from.getId(), eventType);
                }
            }
        } catch (Exception e) {
            log.error("Media file sent error, chat id: {}, user: {}, user id: {}, event type: {}. Failed", chatId,
                    from.getUserName(), from.getId(), eventType);
            e.printStackTrace();
        } finally {
            if (media != null) {
                media.deleteFile();
            }
            deleteMessage(chatId, updateMessageId, from, eventType);
        }
    }

    private Void handleException(Throwable ex, Integer messageId, String chatId, User from, String eventType) {
        deleteMessage(chatId, messageId, from, eventType);
        return handleException(ex, chatId, from, eventType);
    }

    private Void handleException(Throwable ex, String chatId, User from, String eventType) {
        try {
            var exception = ex.getCause();

            if (exception != null) {
                var innerException = ex.getCause().getCause();
                if (innerException != null) {
                    exception = innerException;
                }

                throw exception;
            }
        } catch (NotDownloadableException e) {
            sendMessage(messageService.getText("exception.not_downloadable_exception"), chatId, from, eventType);
            e.printStackTrace();
        } catch (CastingException e) {
            sendMessage(messageService.getText("exception.casting_exception"), chatId, from, eventType);
            e.printStackTrace();
        } catch (MemoryLimitExceededException e) {
            sendMessage(messageService.getText("exception.not_enough_free_space"), chatId, from, eventType);
            e.printStackTrace();
        } catch (LargeMediaFileException e) {
            sendMessage(messageService.getText("exception.large_file"), chatId, from, eventType);
            e.printStackTrace();
        } catch (NotSupportedPlatformException e) {
            sendMessage(messageService.getText("exception.not_supported_type_exception"), chatId, from, eventType);
            e.printStackTrace();
        } catch (SearchingException e) {
            sendMessage(messageService.getText("exception.searching_exception"), chatId, from, eventType);
            e.printStackTrace();
        } catch (Throwable e) {
            sendMessage(messageService.getText("exception.any_exception"), chatId, from, eventType);
            e.printStackTrace();
        }
        return null;
    }

    private InlineQueryResultArticle buildInlineQueryResultArticle(FileEntity fileEntity, int id, String messageText, String callbackDataText) {
        var inlineQueryResultArticle = new InlineQueryResultArticle();
        inlineQueryResultArticle.setId(String.valueOf(id));
        inlineQueryResultArticle.setThumbHeight(48);
        inlineQueryResultArticle.setThumbWidth(48);
        inlineQueryResultArticle.setHideUrl(true);

        var thumbUrlCharacteristic = fileEntity.getCharacteristic(Video.THUMBNAIL_URL_CHARACTERISTIC);
        inlineQueryResultArticle.setThumbUrl(thumbUrlCharacteristic != null ? thumbUrlCharacteristic.getValue() : "");
        var titleCharacteristic = fileEntity.getCharacteristic(Video.TITLE_CHARACTERISTIC);
        inlineQueryResultArticle.setTitle(StringEscapeUtils.unescapeHtml4(titleCharacteristic != null ? titleCharacteristic.getValue() : ""));
        var descriptionCharacteristic = fileEntity.getCharacteristic(Video.DESCRIPTION_CHARACTERISTIC);
        inlineQueryResultArticle.setDescription(StringEscapeUtils.unescapeHtml4(descriptionCharacteristic != null ? descriptionCharacteristic.getValue() : ""));

        var inputTextMessageContent = new InputTextMessageContent();
        inputTextMessageContent.setParseMode("HTML");
        inputTextMessageContent.setMessageText(messageText + " " + fileEntity.getUrl());

        var rowInline = new ArrayList<InlineKeyboardButton>();

        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(messageService.getText("button.send_private"));
        inlineKeyboardButton.setCallbackData(callbackDataText + "[" + fileEntity.getUrl() + "]");
        rowInline.add(inlineKeyboardButton);

        var rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        rowsInline.add(rowInline);

        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);

        inlineQueryResultArticle.setReplyMarkup(markupInline);
        inlineQueryResultArticle.setInputMessageContent(inputTextMessageContent);

        return inlineQueryResultArticle;
    }
}
