package com.messaging.opensource.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CountMessagesByChatroomDto {

    Long count;
}
