package com.messaging.opensource.message;

import com.messaging.opensource.message.dto.CountMessagesByChatroomDto;
import com.messaging.opensource.message.dto.MessageDocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 특정 채팅방의 모든 메시지 조회 (시간순)
    @GetMapping("/chatroom/{chatroomId}")
    public ResponseEntity<List<MessageDocumentDto>> getMessagesByChatroom(
            @PathVariable Long chatroomId) {

        return ResponseEntity.ok().body(
                messageService.getMessagesByChatroomInOrder(chatroomId));
    }

    // 특정 채팅방의 최신순으로 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/recent")
    public ResponseEntity<List<MessageDocumentDto>> getRecentMessagesByChatroom(
            @PathVariable Long chatroomId) {

        return ResponseEntity.ok().body(
                messageService.getRecentMessagesByChatroom(chatroomId));
    }

    // 특정 시간 이후의 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/after")
    public ResponseEntity<List<MessageDocumentDto>> getMessagesAfterTimestamp(
            @PathVariable Long chatroomId,
            @RequestParam LocalDateTime timestamp) {

        return ResponseEntity.ok().body(
                messageService.getMessagesAfterTimestamp(chatroomId, timestamp));
    }

    // 특정 시간 이전의 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/before")
    public ResponseEntity<List<MessageDocumentDto>> getMessagesBeforeTimestamp(
            @PathVariable Long chatroomId,
            @RequestParam LocalDateTime timestamp) {

        return ResponseEntity.ok().body(
                messageService.getMessagesBeforeTimestamp(chatroomId, timestamp));
    }

    // 채팅방 메시지 개수 확인
    @GetMapping("/chatroom/{chatroomId}/count")
    public ResponseEntity<CountMessagesByChatroomDto> countMessagesByChatroom(
            @PathVariable Long chatroomId) {

        return ResponseEntity.ok().body(
                messageService.countMessagesByChatroom(chatroomId));
    }

    // 특정 사용자가 보낸 모든 메시지 조회
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<MessageDocumentDto>> getMessagesBySender(
            @PathVariable Long senderId) {

        return ResponseEntity.ok().body(
                messageService.getMessagesBySender(senderId));
    }

    // 특정 사용자가 특정 채팅방에 보낸 모든 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/sender/{senderId}")
    public ResponseEntity<List<MessageDocumentDto>> getMessagesBySenderInChatroom(
            @PathVariable Long senderId,
            @PathVariable Long chatroomId
    ) {
        return ResponseEntity.ok().body(
                messageService.getMessagesBySenderIdAndChatroomId(senderId, chatroomId));
    }

    // 채팅방 내 메시지 검색
    @GetMapping("/chatroom/{chatroomId}/search")
    public ResponseEntity<List<MessageDocumentDto>> searchMessages(
            @PathVariable Long chatroomId,
            @RequestParam String keyword) {

        return ResponseEntity.ok().body(
                messageService.searchMessagesByChatroomAndKeyword(chatroomId, keyword));
    }

    // 채팅방의 가장 최근 메시지 조회
    @GetMapping("/chatroom/{chatroomId}/latest")
    public ResponseEntity<MessageDocumentDto> getLatestMessageByChatroom(
            @PathVariable Long chatroomId) {

        return ResponseEntity.ok().body(
                messageService.getLatestMessageByChatroom(chatroomId));
    }

    // 특정 사용자의 특정 채팅방 메시지 삭제
    @DeleteMapping("/chatroom/{chatroomId}/sender/{senderId}")
    public ResponseEntity<?> deleteMessagesBySenderInChatroom(
            @PathVariable Long chatroomId,
            @PathVariable Long senderId) {

        messageService.deleteMessagesBySenderInChatroom(chatroomId, senderId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채팅방의 모든 메시지 삭제
    @DeleteMapping("/chatroom/{chatroomId}")
    public ResponseEntity<?> deleteAllMessagesByChatroom(
            @PathVariable Long chatroomId) {

        messageService.deleteAllMessagesByChatroom(chatroomId);
        return ResponseEntity.noContent().build();
    }
}