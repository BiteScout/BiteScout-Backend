package com.bitescout.app.notificationservice.kafka.review;

import lombok.Getter;

@Getter
public enum InteractionType {
    LIKE("liked"),
    DISLIKE("disliked"),
    REPLY("replied to");

    private final String templatePhrase;

    InteractionType(String templateWord){
        this.templatePhrase = templateWord;
    }
}

