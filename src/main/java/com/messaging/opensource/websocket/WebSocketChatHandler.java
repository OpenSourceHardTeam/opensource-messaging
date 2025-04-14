package com.messaging.opensource.websocket;

import com.messaging.opensource.message.MessageService;
import com.messaging.opensource.message.entity.MessageDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketChatHandler.class);

    private final MessageService messageService;

    // sessionMap Key - UserInfo chatRoomId
    private final Map<Long, Set<UserInfo>> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    public WebSocketChatHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        UserInfo userInfo = extractUserInfo(session);

        if (userInfo != null) {
            addUserToChatRoom(userInfo);
            notifyUserJoined(userInfo);
        }
    }

    private void addUserToChatRoom(UserInfo userInfo) {
        sessionMap.computeIfAbsent(userInfo.getChatRoomId(), k -> new HashSet<>()).add(userInfo);
    }

    private void notifyUserJoined(UserInfo userInfo) {
        String joinMessage = userInfo.getName() + "님이 대화방에 들어오셨습니다.";
        sendSystemMessageToChatRoom(userInfo.getChatRoomId(), joinMessage);
    }

    @Override
    protected void handleTextMessage(
            @NonNull WebSocketSession session,
            @NonNull TextMessage textMessage) throws Exception {
        super.handleTextMessage(session, textMessage);
        UserInfo userInfo = extractUserInfo(session);

        if (userInfo != null) {
            String textMessagePayload = textMessage.getPayload();
            processUserMessage(userInfo, textMessagePayload);
        }
    }

    private void processUserMessage(UserInfo userInfo, String messageContent) {
        try {
            broadcastMessageToChatRoom(userInfo, messageContent);
            saveMessageAsync(userInfo, messageContent);
        } catch (IOException e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
        }
    }

    private void broadcastMessageToChatRoom(UserInfo userInfo, String messageContent) throws IOException {
        String formattedMessage = formatMessage(userInfo.getName(), messageContent);
        sendMessageToChatRoomExceptSelf(userInfo.getChatRoomId(), formattedMessage, userInfo);
    }

    private String formatMessage(String userName, String content) {
        return userName + " : " + content;
    }

    private void saveMessageAsync(UserInfo userInfo, String messageContent) {
        MessageDocument messageDocument = createMessageDocument(userInfo, messageContent);

        CompletableFuture.runAsync(() -> {
            try {
                messageService.saveMessage(messageDocument);
            } catch (Exception e) {
                logger.error("Failed to save message: {}", e.getMessage(), e);
            }
        });
    }

    private MessageDocument createMessageDocument(UserInfo userInfo, String content) {
        return MessageDocument.builder()
                .senderId(userInfo.getUserId())
                .chatroomId(userInfo.getChatRoomId())
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public void handleTransportError(
            @NonNull WebSocketSession session,
            @NonNull Throwable exception) throws Exception {
        logger.error("WebSocket transport error: {}", exception.getMessage(), exception);
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(
            @NonNull WebSocketSession session,
            @NonNull CloseStatus status) throws Exception {
        UserInfo userInfo = extractUserInfo(session);

        if (userInfo != null) {
            removeUserFromChatRoom(userInfo);
            notifyUserLeft(userInfo);
        }

        super.afterConnectionClosed(session, status);
    }

    private void removeUserFromChatRoom(UserInfo userInfo) {
        Set<UserInfo> usersInChatRoom = sessionMap.get(userInfo.getChatRoomId());
        if (usersInChatRoom != null) {
            usersInChatRoom.removeIf(user -> user.getUserId().equals(userInfo.getUserId()));

            // 채팅방이 비었다면 맵에서 제거
            if (usersInChatRoom.isEmpty()) {
                sessionMap.remove(userInfo.getChatRoomId());
            }
        }
    }

    private void notifyUserLeft(UserInfo userInfo) {
        String leaveMessage = userInfo.getName() + "님이 대화방을 나가셨습니다.";
        sendSystemMessageToChatRoom(userInfo.getChatRoomId(), leaveMessage);
    }

    private void sendSystemMessageToChatRoom(Long chatRoomId, String message) {
        try {
            sendMessageToChatRoom(chatRoomId, message);
        } catch (IOException e) {
            logger.error("Failed to send system message: {}", e.getMessage(), e);
        }
    }

    private void sendMessageToChatRoom(Long chatRoomId, String message) throws IOException {
        Set<UserInfo> users = sessionMap.get(chatRoomId);

        if (users == null || users.isEmpty()) {
            return;
        }

        IOException lastException = null;

        for (UserInfo user : users) {
            try {
                user.getSession().sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.warn("Failed to send message to user {}: {}", user.getUserId(), e.getMessage());
                lastException = e;
            }
        }

        // 모든 사용자에게 메시지 전송을 시도한 후, 마지막 예외가 있으면 throw
        if (lastException != null) {
            throw lastException;
        }
    }

    private void sendMessageToChatRoomExceptSelf(Long chatRoomId, String message, UserInfo sender) throws IOException {
        Set<UserInfo> users = sessionMap.get(chatRoomId);

        if (users == null || users.isEmpty()) {
            return;
        }

        String senderId = sender.getSession().getId();
        IOException lastException = null;

        for (UserInfo user : users) {
            try {
                if (!senderId.equals(user.getSession().getId())) {
                    user.getSession().sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                logger.warn("Failed to send message to user {}: {}", user.getUserId(), e.getMessage());
                lastException = e;
            }
        }

        // 모든 사용자에게 메시지 전송을 시도한 후, 마지막 예외가 있으면 throw
        if (lastException != null) {
            throw lastException;
        }
    }

    private UserInfo extractUserInfo(WebSocketSession session) {
        try {
            // 모든 필수 헤더 한 번에 추출
            Map<String, String> headers = extractRequiredHeaders(session);
            if (headers == null) {
                return null; // extractRequiredHeaders에서 이미 오류 메시지를 보냈음
            }

            // 숫자 값 파싱
            Long userId = parseNumericId(session, headers.get("userId"), "userId");
            Long chatroomId = parseNumericId(session, headers.get("chatroomId"), "chatroomId");

            if (userId == null || chatroomId == null) {
                return null; // parseNumericId에서 이미 오류 메시지를 보냈음
            }

            return new UserInfo(headers.get("name"), userId, chatroomId, session);

        } catch (Exception e) {
            logger.error("WebSocket header processing failed", e);
            try {
                sendErrorAndClose(session, "Unexpected server error.");
            } catch (IOException ioe) {
                logger.error("Failed to send error message", ioe);
            }
            return null;
        }
    }

    private Map<String, String> extractRequiredHeaders(WebSocketSession session) throws IOException {
        String name = session.getHandshakeHeaders().getFirst("name");
        String userIdString = session.getHandshakeHeaders().getFirst("userId");
        String chatroomIdString = session.getHandshakeHeaders().getFirst("chatRoomId");

        if (name == null || userIdString == null || chatroomIdString == null) {
            sendErrorAndClose(session, "Missing required headers.");
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("name", name);
        headers.put("userId", userIdString);
        headers.put("chatroomId", chatroomIdString);
        return headers;
    }

    private Long parseNumericId(WebSocketSession session, String idString, String fieldName) throws IOException {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            sendErrorAndClose(session, "Invalid " + fieldName + " format: " + idString);
            return null;
        }
    }

    private void sendErrorAndClose(WebSocketSession session, String errorMessage) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage("Error: " + errorMessage));
            session.close();
        }
    }
}