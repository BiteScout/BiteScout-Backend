package com.bitescout.app.notificationservice.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Embeddable;
import lombok.*;


public record UserDetails (
    String firstName,
    String lastName,
    String phoneNumber,
    String country,
    String city,
    String postalCode,
    String address,
    String profilePicture
) {}
