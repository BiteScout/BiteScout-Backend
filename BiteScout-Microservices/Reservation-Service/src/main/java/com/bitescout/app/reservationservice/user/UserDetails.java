package com.bitescout.app.reservationservice.user;


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
