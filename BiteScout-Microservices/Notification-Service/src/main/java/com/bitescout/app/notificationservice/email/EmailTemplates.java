package com.bitescout.app.notificationservice.email;

import lombok.Getter;

@Getter
public enum EmailTemplates {

    SPECIAL_OFFER_NOTIFICATION("special-offer-notification.html",
            "Special offer available from favorite restaurant"),
    INCOMING_RESERVATION_NOTIFICATION("incoming-reservation-notification.html",
            "There is a new reservation request for your restaurant"), //to restaurant owner
    RESERVATION_STATUS_NOTIFICATION("reservation-status-notification.html",
            "There is an update on your reservation status"), //to user, to notify of acceptance or refusal of their reservation request
    DEFAULT("default.html",
    "Default");

    private final String template;
    private final String subject;

    EmailTemplates(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
