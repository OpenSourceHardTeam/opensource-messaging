package com.messaging.opensource.message;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 특정 채팅방의 모든 메시지 조회 (시간순)
    @GetMapping("/chatroom/{chatroomId}")
    public List<MessageDocument> getMessagesByChatroom(
            @PathVariable Long chatroomId) {
        return messageService.getMessagesByChatroomInOrder(chatroomId);
    }

    // 특정 채팅방의 최신 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/recent")
    public List<MessageDocument> getRecentMessagesByChatroom(
            @PathVariable Long chatroomId) {
        return messageService.getRecentMessagesByChatroom(chatroomId);
    }

    // 특정 시간 이후의 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/after")
    public List<MessageDocument> getMessagesAfterTimestamp(
            @PathVariable Long chatroomId,
            @RequestParam LocalDateTime timestamp) {
        return messageService.getMessagesAfterTimestamp(chatroomId, timestamp);
    }

    // 특정 시간 이전의 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/before")
    public List<MessageDocument> getMessagesBeforeTimestamp(
            @PathVariable Long chatroomId,
            @RequestParam LocalDateTime timestamp) {
        return messageService.getMessagesBeforeTimestamp(chatroomId, timestamp);
    }

    // 채팅방 메시지 개수 확인
    @GetMapping("/chatroom/{chatroomId}/count")
    public long countMessagesByChatroom(
            @PathVariable Long chatroomId) {
        return messageService.countMessagesByChatroom(chatroomId);
    }

    // 특정 사용자가 보낸 모든 메시지 조회
    @GetMapping("/sender/{senderId}")
    public List<MessageDocument> getMessagesBySender(
            @PathVariable Long senderId) {
        return messageService.getMessagesBySender(senderId);
    }

    // 채팅방 내 메시지 검색
    @GetMapping("/search")
    public List<MessageDocument> searchMessages(
            @RequestParam Long chatroomId,
            @RequestParam String keyword) {
        return messageService.searchMessagesByChatroomAndKeyword(chatroomId, keyword);
    }

    // 채팅방의 가장 최근 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/latest")
    public MessageDocument getLatestMessageByChatroom(
            @PathVariable Long chatroomId) {
        return messageService.getLatestMessageByChatroom(chatroomId);
    }

    // 특정 사용자의 특정 채팅방 메시지 삭제
    @DeleteMapping("/chatroom/{chatroomId}/sender/{senderId}")
    public void deleteMessagesBySenderInChatroom(
            @PathVariable Long chatroomId,
            @PathVariable Long senderId) {
        messageService.deleteMessagesBySenderInChatroom(chatroomId, senderId);
    }

    // 특정 채팅방의 모든 메시지 삭제
    @DeleteMapping("/chatroom/{chatroomId}")
    public void deleteAllMessagesByChatroom(
            @PathVariable Long chatroomId) {
        messageService.deleteAllMessagesByChatroom(chatroomId);
    }
}