package com.bitescout.app.reviewservice.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {

}
