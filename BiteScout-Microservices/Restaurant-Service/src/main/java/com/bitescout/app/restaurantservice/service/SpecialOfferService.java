package com.bitescout.app.restaurantservice.service;

import com.bitescout.app.restaurantservice.dto.*;
import com.bitescout.app.restaurantservice.entity.*;
import com.bitescout.app.restaurantservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialOfferService {
    private final SpecialOfferRepository specialOfferRepository;
    private final RestaurantRepository restaurantRepository;

    private final ModelMapper modelMapper;

    public SpecialOfferResponseDTO createSpecialOffer(String restaurantId, SpecialOfferRequestDTO specialOfferRequest) {
        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        SpecialOffer specialOffer = modelMapper.map(specialOfferRequest, SpecialOffer.class);
        specialOffer.setRestaurant(restaurant);

        return modelMapper.map(specialOfferRepository.save(specialOffer), SpecialOfferResponseDTO.class);
    }

    public List<SpecialOfferResponseDTO> getSpecialOffers(String restaurantId) {
        return specialOfferRepository.findAllByRestaurantId(UUID.fromString(restaurantId)).stream()
                .map(specialOffer -> modelMapper.map(specialOffer, SpecialOfferResponseDTO.class))
                .collect(Collectors.toList());
    }

    public SpecialOfferResponseDTO updateSpecialOffer(String restaurantId, String specialOfferId, SpecialOfferRequestDTO specialOfferRequest) {
        SpecialOffer specialOffer = specialOfferRepository.findById(UUID.fromString(specialOfferId))
                .orElseThrow(() -> new RuntimeException("Special offer not found"));

        if (!specialOffer.getRestaurant().getId().equals(UUID.fromString(restaurantId))) {
            throw new RuntimeException("Special offer not found");
        }

        modelMapper.map(specialOfferRequest, specialOffer);

        return modelMapper.map(specialOfferRepository.save(specialOffer), SpecialOfferResponseDTO.class);
    }

    public void deleteSpecialOffer(String restaurantId, String specialOfferId) {
        SpecialOffer specialOffer = specialOfferRepository.findById(UUID.fromString(specialOfferId))
                .orElseThrow(() -> new RuntimeException("Special offer not found"));

        if (!specialOffer.getRestaurant().getId().equals(UUID.fromString(restaurantId))) {
            throw new RuntimeException("Special offer not found");
        }

        specialOfferRepository.delete(specialOffer);
    }
}
