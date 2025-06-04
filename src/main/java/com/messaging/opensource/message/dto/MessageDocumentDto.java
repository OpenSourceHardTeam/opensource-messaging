package com.messaging.opensource.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MessageDocumentDto {

    Long id;  // String에서 Long으로 변경

    Long chatroomId;

    Long senderId;

    String content;

    LocalDateTime timestamp;
}