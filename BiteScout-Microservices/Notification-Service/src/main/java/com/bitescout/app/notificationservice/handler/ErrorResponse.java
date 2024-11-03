package com.bitescout.app.notificationservice.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {

}
