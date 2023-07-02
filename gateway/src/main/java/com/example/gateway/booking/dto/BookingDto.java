package com.example.gateway.booking.dto;

import com.example.gateway.item.dto.ShortItemDto;
import com.example.gateway.item.dto.ShortUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookingDto {
    public static final Status DEFAULT_STATUS = Status.WAITING;

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status = DEFAULT_STATUS;
    private ShortUserDto booker;
    private ShortItemDto item;
}
