package com.messaging.opensource.message;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
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
}
