package com.messaging.opensource.message.entity;

import com.messaging.opensource.message.dto.MessageDocumentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
@EntityListeners(AuditingEntityListener.class)
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chatroom_id", nullable = false)
    private Long chatroomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "timestamp", nullable = false, updatable = false)
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