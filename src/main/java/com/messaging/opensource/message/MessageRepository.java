package com.messaging.opensource.message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<MessageDocument, String> {

    // 특정 채팅방의 모든 메시지 조회 (시간순)
    List<MessageDocument> findByChatroomIdOrderByTimestampAsc(Long chatroomId);

    // 최신순으로 정렬하여 조회
    List<MessageDocument> findByChatroomIdOrderByTimestampDesc(Long chatroomId);

    // 특정 시간 이후의 메시지 조회
    List<MessageDocument> findByChatroomIdAndTimestampAfter(Long chatroomId, LocalDateTime timestamp);

    // 특정 시간 이전의 메시지 조회 (페이징에 유용)
    List<MessageDocument> findByChatroomIdAndTimestampBeforeOrderByTimestampDesc(Long chatroomId, LocalDateTime timestamp);

    // 특정 채팅방의 메시지 개수 조회
    long countByChatroomId(Long chatroomId);

    // 특정 사용자가 보낸 메시지 조회
    List<MessageDocument> findBySenderId(Long senderId);

    // 특정 내용을 포함하는 메시지 검색
    List<MessageDocument> findByChatroomIdAndContentContaining(Long chatroomId, String keyword);

    // 특정 채팅방의 가장 최근 메시지 조회 (limit 1 효과)
    MessageDocument findTopByChatroomIdOrderByTimestampDesc(Long chatroomId);

    // 특정 사용자의 특정 채팅방 메시지 삭제
    void deleteByChatroomIdAndSenderId(Long chatroomId, Long senderId);
}