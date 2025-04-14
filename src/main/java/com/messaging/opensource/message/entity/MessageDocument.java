package com.messaging.opensource.message.entity;

import com.messaging.opensource.message.dto.MessageDocumentDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
@Document(collection = "messages")
public class MessageDocument {

    @Id
    private String id;

    private Long chatroomId;

    private Long senderId;

    private String content;

    @CreatedDate
    private LocalDateTime timestamp;

    public MessageDocumentDto toDto() {
        return MessageDocumentDto.builder()
                .id(this.getId())
                .content(this.getContent())
                .chatroomId(this.getChatroomId())
                .senderId(this.getSenderId())
                .timestamp(this.getTimestamp())
                .build();
    }
}
