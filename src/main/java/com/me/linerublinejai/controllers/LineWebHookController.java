package com.me.linerublinejai.controllers;

import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.me.linerublinejai.services.LineWebHookService;
import org.springframework.beans.factory.annotation.Autowired;

@LineMessageHandler
public class LineWebHookController {

    @Autowired
    private LineWebHookService lineWebHookService;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        lineWebHookService.handleTextContent(event);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) throws Exception {
        lineWebHookService.handleFollow(event);
    }

    @EventMapping
    public void handlePostBackEvent(PostbackEvent event) throws Exception {

    }

}
