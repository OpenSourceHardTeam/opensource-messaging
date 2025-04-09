package com.messaging.opensource.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    // 특정 채팅방의 모든 메시지를 시간순으로 조회
    public List<MessageDocument> getMessagesByChatroomInOrder(Long chatroomId) {
        return messageRepository.findByChatroomIdOrderByTimestampAsc(chatroomId);
    }

    // 특정 채팅방의 최신 메시지 조회 (최신순)
    public List<MessageDocument> getRecentMessagesByChatroom(Long chatroomId) {
        return messageRepository.findByChatroomIdOrderByTimestampDesc(chatroomId);
    }

    // 메시지 저장
    public MessageDocument saveMessage(MessageDocument message) {
        return messageRepository.save(message);
    }

    // 특정 시간 이후의 메시지 조회
    public List<MessageDocument> getMessagesAfterTimestamp(Long chatroomId, LocalDateTime timestamp) {
        return messageRepository.findByChatroomIdAndTimestampAfter(chatroomId, timestamp);
    }

    // 특정 시간 이전의 메시지 조회 (페이징용)
    public List<MessageDocument> getMessagesBeforeTimestamp(Long chatroomId, LocalDateTime timestamp) {
        return messageRepository.findByChatroomIdAndTimestampBeforeOrderByTimestampDesc(chatroomId, timestamp);
    }

    // 특정 채팅방의 메시지 개수 확인
    public long countMessagesByChatroom(Long chatroomId) {
        return messageRepository.countByChatroomId(chatroomId);
    }

    // 특정 사용자가 보낸 모든 메시지 조회
    public List<MessageDocument> getMessagesBySender(Long senderId) {
        return messageRepository.findBySenderId(senderId);
    }

    // 채팅방 내 메시지 검색
    public List<MessageDocument> searchMessagesByChatroomAndKeyword(Long chatroomId, String keyword) {
        return messageRepository.findByChatroomIdAndContentContaining(chatroomId, keyword);
    }

    // 채팅방의 가장 최근 메시지 조회
    public MessageDocument getLatestMessageByChatroom(Long chatroomId) {
        return messageRepository.findTopByChatroomIdOrderByTimestampDesc(chatroomId);
    }

    // 특정 사용자의 특정 채팅방 메시지 삭제
    public void deleteMessagesBySenderInChatroom(Long chatroomId, Long senderId) {
        messageRepository.deleteByChatroomIdAndSenderId(chatroomId, senderId);
    }

    // 대량 메시지 저장
    public List<MessageDocument> saveAllMessages(List<MessageDocument> messages) {
        return messageRepository.saveAll(messages);
    }

    // 특정 채팅방의 모든 메시지 삭제
    public void deleteAllMessagesByChatroom(Long chatroomId) {
        List<MessageDocument> messages = messageRepository.findByChatroomIdOrderByTimestampAsc(chatroomId);
        messageRepository.deleteAll(messages);
    }
}
