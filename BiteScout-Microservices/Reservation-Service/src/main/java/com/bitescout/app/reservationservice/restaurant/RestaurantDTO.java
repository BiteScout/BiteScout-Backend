package com.bitescout.app.reservationservice.restaurant;

import lombok.Data;

import java.util.UUID;

@Data
public class RestaurantDTO {
    private String id;
    private String ownerId;
    private String name;
}