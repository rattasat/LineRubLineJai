package com.me.linerublinejai.controllers;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@LineMessageHandler
public class LineWebHookController {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @EventMapping
    public void handleEvent(MessageEvent<TextMessageContent> event) throws Exception {
        String replyToken = event.getReplyToken();
        String lineUserId = event.getSource().getUserId();
        List<Message> messages = new ArrayList<>();
        switch (event.getMessage().getText().toLowerCase()) {
            case "profile":
                UserProfileResponse profile = lineMessagingClient.getProfile(lineUserId).get();
                messages.add(new TextMessage(String.format("Display Name: %s", profile.getDisplayName())));
                messages.add(new TextMessage(String.format("Status message: %s", profile.getStatusMessage())));
                break;
            case "sticker":
                messages.add(new StickerMessage("11538", "51626514"));
                break;
            case "location":
                messages.add(new LocationMessage("Location", "The Office Plus", 18.7607191, 98.9689888));
                break;
            case "image":
                messages.add(new ImageMessage("https://codeitdown.com/wp-content/uploads/2014/02/responsive-font-size-240x240.jpg",
                        "https://codeitdown.com/wp-content/uploads/2014/02/responsive-font-size-240x240.jpg"));
                break;
            case "confirm":
                ConfirmTemplate confirmTemplate = new ConfirmTemplate("Are you sure?", Arrays.asList(new PostbackAction("OK", "confirmId=1"),
                        new URIAction("CANCEL", "https://www.facebook.com/kokoro.brit")));
                messages.add(new TemplateMessage("confirm", confirmTemplate));
                break;
            case "quick reply":
                // MAX 13
                List<QuickReplyItem> quickReplyItems = new ArrayList<>();
                quickReplyItems.add(QuickReplyItem.builder().action(CameraAction.withLabel("Camera")).build());
                quickReplyItems.add(QuickReplyItem.builder().action(CameraRollAction.withLabel("Camera roll")).build());
                quickReplyItems.add(QuickReplyItem.builder().action(new MessageAction("Message", "test message action")).build());
                quickReplyItems.add(QuickReplyItem.builder().action(new DatetimePickerAction("Datetime", "data", "date")).build());
                quickReplyItems.add(QuickReplyItem.builder().action(LocationAction.withLabel("Location")).build());

                QuickReply quickReply = QuickReply.items(quickReplyItems);
                messages.add(new TextMessage("Quick reply list", quickReply));
                break;
             default:
        }
        if (!messages.isEmpty()) {
            lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages));
        }
    }

    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) throws Exception {
        System.out.println("[Postback event] data: " + event.getPostbackContent().getData());
        if (event.getPostbackContent().getParams() != null) {
            System.out.println("[Postback event] params: " + event.getPostbackContent().getParams());
        }
    }

    @EventMapping
    public void handleLocationMessage(MessageEvent<LocationMessageContent> event) throws Exception {
        String replyToken = event.getReplyToken();
        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, Arrays.asList(new LocationMessage("your location",
                event.getMessage().getAddress(),
                event.getMessage().getLatitude(),
                event.getMessage().getLongitude()))));
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) throws Exception {
        String replyToken = event.getReplyToken();
        String lineUserId = event.getSource().getUserId();
        UserProfileResponse profile = lineMessagingClient.getProfile(lineUserId).get();
        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(String.format("Hi! %s", profile.getDisplayName()))));
    }

}
