package com.messaging.opensource.websocket;

import com.messaging.opensource.message.MessageService;
import com.messaging.opensource.message.entity.MessageDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketChatHandler.class);

    private final MessageService messageService;

    @Autowired
    public WebSocketChatHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    // sessionMap Key - UserInfo chatRoomId
    private final Map<Long, Set<UserInfo>> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        UserInfo userInfo = extractUserInfo(session);

        sessionMap.computeIfAbsent(userInfo.getChatRoomId(), k -> new HashSet<>()).add(userInfo);
        sendMessageToChatRoom(userInfo.getChatRoomId(), userInfo.getName() + "님이 대화방에 들어오셨습니다.");
    }

    @Override
    protected void ㅂ(WebSocketSession session, TextMessage textMessage) throws Exception {
        super.handleTextMessage(session, textMessage);
        UserInfo userInfo = extractUserInfo(session);

        // send to chatroom
        String textMessagePayload = textMessage.getPayload();
        String message = userInfo.getName() + " : " + textMessagePayload;
        sendMessageToChatRoomExceptSelf(userInfo.getChatRoomId(), message, userInfo);

        // save message data, 비동기적으로 메시지 저장
        MessageDocument messageDocument = MessageDocument.builder()
                .senderId(userInfo.getUserId())
                .chatroomId(userInfo.getChatRoomId())
                .content(textMessagePayload)
                .timestamp(LocalDateTime.now())
                .build();

        CompletableFuture.runAsync(() -> {
            try {
                messageService.saveMessage(messageDocument);
            } catch (Exception e) {
                logger.error("Failed to save message: {}", e.getMessage(), e);
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserInfo userInfo = extractUserInfo(session);

        Set<UserInfo> usersInChatRoom = sessionMap.get(userInfo.getChatRoomId());
        if (usersInChatRoom != null) {
            usersInChatRoom.removeIf(user -> user.getUserId().equals(userInfo.getUserId()));
        }

        sendMessageToChatRoom(userInfo.getChatRoomId(), userInfo.getName() + "님이 대화방을 나가셨습니다.");
    }

    private void sendMessageToChatRoom(Long chatRoomId, String message) throws IOException {
        Set<UserInfo> users = sessionMap.get(chatRoomId);

        if (users != null) {
            for (UserInfo user : users) {
                try {
                    user.getSession().sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    // 오류 처리
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void sendMessageToChatRoomExceptSelf(Long chatRoomId, String message, UserInfo userInfo) throws IOException {
        Set<UserInfo> users = sessionMap.get(chatRoomId);
        WebSocketSession session = userInfo.getSession();

        if (users != null) {
            for (UserInfo user : users) {
                try {
                    if(!session.getId().equals(user.getSession().getId())) {
                        user.getSession().sendMessage(new TextMessage(message));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private UserInfo extractUserInfo(WebSocketSession session) throws IOException {
        try {
            String name = session.getHandshakeHeaders().getFirst("name");
            String clientIdInString = session.getHandshakeHeaders().getFirst("userId");
            String chatRoomIdString = session.getHandshakeHeaders().getFirst("chatRoomId");

            // 필수 헤더 값이 누락된 경우 예외 처리
            if (name == null || clientIdInString == null || chatRoomIdString == null) {
                session.sendMessage(new TextMessage("Error: Missing required headers."));
                session.close();
                return null; // 조기 종료
            }

            long clientId;
            try {
                clientId = Long.parseLong(clientIdInString);
            } catch (NumberFormatException e) {
                session.sendMessage(new TextMessage("Error: Invalid userId format: " + clientIdInString));
                session.close();
                return null; // 조기 종료
            }

            long chatRoomId;
            try {
                chatRoomId = Long.parseLong(chatRoomIdString);
            } catch (NumberFormatException e) {
                session.sendMessage(new TextMessage("Error: Invalid chatroomId format: " + chatRoomIdString));
                session.close();
                return null; // 조기 종료
            }

            return new UserInfo(name, clientId, chatRoomId, session);

        } catch (Exception e) {
            session.sendMessage(new TextMessage("Error: Unexpected server error."));
            session.close();
            throw new RuntimeException("WebSocket header processing failed", e);
        }
    }
}