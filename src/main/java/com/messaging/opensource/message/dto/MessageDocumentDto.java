package com.messaging.opensource.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MessageDocumentDto {

    String id;

    Long chatroomId;

    Long senderId;

    String content;

    LocalDateTime timestamp;
}
