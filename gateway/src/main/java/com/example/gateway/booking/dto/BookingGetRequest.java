package com.example.gateway.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;

@Data
@Builder
public class BookingGetRequest {
    private Long userId;
    private String possibleState;
    @Positive(message = "From parameter can not be less than zero")
    private Integer from;
    private Integer size;

}
