package com.messaging.opensource.message;

import com.messaging.opensource.message.dto.CountMessagesByChatroomDto;
import com.messaging.opensource.message.dto.MessageDocumentDto;
import com.messaging.opensource.message.entity.MessageDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메시지 관련 API 인터페이스
 */
public interface MessageControllerInterface {

    /**
     * 특정 채팅방의 모든 메시지 조회 (시간순)
     * @param chatroomId 채팅방 ID
     * @return 채팅방의 메시지 목록
     */
    @GetMapping("/chatroom/{chatroomId}")
    ResponseEntity<List<MessageDocumentDto>> getMessagesByChatroom(@PathVariable Long chatroomId);

    /**
     * 특정 채팅방의 최신순으로 메시지 조회
     * @param chatroomId 채팅방 ID
     * @return 채팅방의 최신순 메시지 목록
     */
    @GetMapping("/chatroom/{chatroomId}/recent")
    ResponseEntity<List<MessageDocumentDto>> getRecentMessagesByChatroom(@PathVariable Long chatroomId);

    /**
     * 특정 시간 이후의 메시지 조회
     * @param chatroomId 채팅방 ID
     * @param timestamp 기준 시간
     * @return 해당 시간 이후의 메시지 목록
     */
    @GetMapping("/chatroom/{chatroomId}/after")
    ResponseEntity<List<MessageDocumentDto>> getMessagesAfterTimestamp(
            @PathVariable Long chatroomId,
            @RequestParam LocalDateTime timestamp);

    /**
     * 특정 시간 이전의 메시지 조회
     * @param chatroomId 채팅방 ID
     * @param timestamp 기준 시간
     * @return 해당 시간 이전의 메시지 목록
     */
    @GetMapping("/chatroom/{chatroomId}/before")
    ResponseEntity<List<MessageDocumentDto>> getMessagesBeforeTimestamp(
            @PathVariable Long chatroomId,
            @RequestParam LocalDateTime timestamp);

    /**
     * 채팅방 메시지 개수 확인
     * @param chatroomId 채팅방 ID
     * @return 채팅방의 메시지 개수 정보
     */
    @GetMapping("/chatroom/{chatroomId}/count")
    ResponseEntity<CountMessagesByChatroomDto> countMessagesByChatroom(@PathVariable Long chatroomId);

    /**
     * 특정 사용자가 보낸 모든 메시지 조회
     * @param senderId 사용자 ID
     * @return 해당 사용자가 보낸 메시지 목록
     */
    @GetMapping("/sender/{senderId}")
    ResponseEntity<List<MessageDocumentDto>> getMessagesBySender(@PathVariable Long senderId);

    /**
     * 특정 사용자가 특정 채팅방에 보낸 모든 메시지 조회
     * @param senderId 사용자 ID
     * @param chatroomId 채팅방 ID
     * @return 해당 사용자가 특정 채팅방에 보낸 메시지 목록
     */
    @GetMapping("/chatroom/{chatroomId}/sender/{senderId}")
    ResponseEntity<List<MessageDocumentDto>> getMessagesBySenderInChatroom(
            @PathVariable Long senderId,
            @PathVariable Long chatroomId);

    /**
     * 채팅방 내 메시지 검색
     * @param chatroomId 채팅방 ID
     * @param keyword 검색 키워드
     * @return 검색 결과 메시지 목록
     */
    @GetMapping("/chatroom/{chatroomId}/search")
    ResponseEntity<List<MessageDocumentDto>> searchMessages(
            @PathVariable Long chatroomId,
            @RequestParam String keyword);

    /**
     * 채팅방의 가장 최근 메시지 조회
     * @param chatroomId 채팅방 ID
     * @return 채팅방의 최신 메시지
     */
    @GetMapping("/chatroom/{chatroomId}/latest")
    ResponseEntity<MessageDocumentDto> getLatestMessageByChatroom(@PathVariable Long chatroomId);

    /**
     * 특정 사용자의 특정 채팅방 메시지 삭제
     * @param chatroomId 채팅방 ID
     * @param senderId 사용자 ID
     * @return 응답 엔티티
     */
    @DeleteMapping("/chatroom/{chatroomId}/sender/{senderId}")
    ResponseEntity<?> deleteMessagesBySenderInChatroom(
            @PathVariable Long chatroomId,
            @PathVariable Long senderId);

    /**
     * 특정 채팅방의 모든 메시지 삭제
     * @param chatroomId 채팅방 ID
     * @return 응답 엔티티
     */
    @DeleteMapping("/chatroom/{chatroomId}")
    ResponseEntity<?> deleteAllMessagesByChatroom(@PathVariable Long chatroomId);
}