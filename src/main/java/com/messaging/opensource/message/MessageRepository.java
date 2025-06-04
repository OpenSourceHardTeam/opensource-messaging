package com.messaging.opensource.message;

import com.messaging.opensource.message.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // 특정 채팅방의 모든 메시지 조회 (시간순)
    List<MessageEntity> findByChatroomIdOrderByTimestampAsc(Long chatroomId);

    // 최신순으로 정렬하여 조회
    List<MessageEntity> findByChatroomIdOrderByTimestampDesc(Long chatroomId);

    // 특정 시간 이후의 메시지 조회
    List<MessageEntity> findByChatroomIdAndTimestampAfter(Long chatroomId, LocalDateTime timestamp);

    // 특정 시간 이전의 메시지 조회 (페이징에 유용)
    List<MessageEntity> findByChatroomIdAndTimestampBeforeOrderByTimestampDesc(Long chatroomId, LocalDateTime timestamp);

    // 특정 사용자가 특정 채팅방에 작성한 메시지 조회
    List<MessageEntity> findBySenderIdAndChatroomId(Long senderId, Long chatroomId);

    // 특정 채팅방의 메시지 개수 조회
    long countByChatroomId(Long chatroomId);

    // 특정 사용자가 보낸 메시지 조회
    List<MessageEntity> findBySenderId(Long senderId);

    // 특정 내용을 포함하는 메시지 검색
    List<MessageEntity> findByChatroomIdAndContentContaining(Long chatroomId, String keyword);

    // 특정 채팅방의 가장 최근 메시지 조회
    MessageEntity findTopByChatroomIdOrderByTimestampDesc(Long chatroomId);

    // 특정 사용자의 특정 채팅방 메시지 삭제
    void deleteByChatroomIdAndSenderId(Long chatroomId, Long senderId);
}