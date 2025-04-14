package com.messaging.opensource.message;

import com.messaging.opensource.message.dto.CountMessagesByChatroomDto;
import com.messaging.opensource.message.dto.MessageDocumentDto;
import com.messaging.opensource.message.entity.MessageDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;

    // 특정 채팅방의 모든 메시지를 시간순으로 조회
    public List<MessageDocumentDto> getMessagesByChatroomInOrder(Long chatroomId) {
        List<MessageDocument> messageDocuments = messageRepository.findByChatroomIdOrderByTimestampAsc(chatroomId);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 특정 채팅방의 최신 메시지 조회 (최신순)
    public List<MessageDocumentDto> getRecentMessagesByChatroom(Long chatroomId) {
        List<MessageDocument> messageDocuments = messageRepository.findByChatroomIdOrderByTimestampDesc(chatroomId);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 메시지 저장
    @Transactional
    public String saveMessage(MessageDocument message) {
        return messageRepository.save(message).getId();
    }

    // 특정 시간 이후의 메시지 조회
    public List<MessageDocumentDto> getMessagesAfterTimestamp(Long chatroomId, LocalDateTime timestamp) {
        List<MessageDocument> messageDocuments = messageRepository.findByChatroomIdAndTimestampAfter(chatroomId, timestamp);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 특정 시간 이전의 메시지 조회 (페이징용)
    public List<MessageDocumentDto> getMessagesBeforeTimestamp(Long chatroomId, LocalDateTime timestamp) {
        List<MessageDocument> messageDocuments = messageRepository.findByChatroomIdAndTimestampBeforeOrderByTimestampDesc(chatroomId, timestamp);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 특정 사용자가 특정 채팅방에 작성한 메시지 조회
    public List<MessageDocumentDto> getMessagesBySenderIdAndChatroomId(Long senderId, Long chatroomId) {
        List<MessageDocument> messageDocuments = messageRepository.findBySenderIdAndChatroomId(senderId, chatroomId);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 특정 채팅방의 메시지 개수 확인
    public CountMessagesByChatroomDto countMessagesByChatroom(Long chatroomId) {
        Long count = messageRepository.countByChatroomId(chatroomId);
        return CountMessagesByChatroomDto.builder()
                .count(count)
                .build();
    }

    // 특정 사용자가 보낸 모든 메시지 조회
    public List<MessageDocumentDto> getMessagesBySender(Long senderId) {
        List<MessageDocument> messageDocuments = messageRepository.findBySenderId(senderId);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 채팅방 내 메시지 검색
    public List<MessageDocumentDto> searchMessagesByChatroomAndKeyword(Long chatroomId, String keyword) {
        List<MessageDocument> messageDocuments = messageRepository.findByChatroomIdAndContentContaining(chatroomId, keyword);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 채팅방의 가장 최근 메시지 조회
    public MessageDocumentDto getLatestMessageByChatroom(Long chatroomId) {
        return messageRepository.findTopByChatroomIdOrderByTimestampDesc(chatroomId).toDto();
    }

    // 특정 사용자의 특정 채팅방 메시지 삭제
    @Transactional
    public void deleteMessagesBySenderInChatroom(Long chatroomId, Long senderId) {
        messageRepository.deleteByChatroomIdAndSenderId(chatroomId, senderId);
    }

    // 대량 메시지 저장
    @Transactional
    public List<MessageDocumentDto> saveAllMessages(List<MessageDocument> messages) {
        List<MessageDocument> messageDocuments = messageRepository.saveAll(messages);
        return messageDocumentListToDtoList(messageDocuments);
    }

    // 특정 채팅방의 모든 메시지 삭제
    @Transactional
    public void deleteAllMessagesByChatroom(Long chatroomId) {
        List<MessageDocument> messages = messageRepository.findByChatroomIdOrderByTimestampAsc(chatroomId);
        messageRepository.deleteAll(messages);
    }

    private List<MessageDocumentDto> messageDocumentListToDtoList(List<MessageDocument> messages) {
        return messages.stream()
                .map(MessageDocument::toDto)
                .toList();
    }
}
