package com.messaging.opensource.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@AllArgsConstructor
public class UserInfo {
    private String name;
    private Long userId;
    private Long chatRoomId;
    private WebSocketSession session;
}