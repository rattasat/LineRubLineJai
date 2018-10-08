package com.me.linerublinejai.controllers;

import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.me.linerublinejai.services.LineWebHookService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@LineMessageHandler
public class LineWebHookController {

    @Autowired
    private LineWebHookService lineWebHookService;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        String userId = event.getSource().getUserId();
        String replyToken = event.getReplyToken();
        String text = event.getMessage().getText().trim();
        lineWebHookService.textMessageEvent(userId, replyToken, text);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) throws Exception {
        String userId = event.getSource().getUserId();
        String replyToken = event.getReplyToken();
        lineWebHookService.handleFollow(userId, replyToken);
    }

    @EventMapping
    public void handlePostBackEvent(PostbackEvent event) throws Exception {
        String userId = event.getSource().getUserId();
        String replyToken = event.getReplyToken();
        Map<String, String> params = event.getPostbackContent().getParams();
        String data = event.getPostbackContent().getData();
        lineWebHookService.handlePostbackEvent(data, params, userId, replyToken);
    }

}
