package com.chatapp.backend.config;

import com.chatapp.backend.security.JwtUtil;
import com.chatapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-memory broker for /topic (group) and /queue (direct user)
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix for @MessageMapping methods in controllers
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
                // SockJS fallback removed — Android uses native WebSocket
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Extract JWT from STOMP CONNECT header
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        if (jwtUtil.isTokenValid(token) && !jwtUtil.isRefreshToken(token)) {
                            UUID userId = jwtUtil.extractUserId(token);
                            userRepository.findById(userId).ifPresent(user -> {
                                UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                        user, null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                    );
                                accessor.setUser(auth);
                                log.debug("WS authenticated: {}", user.getPhone());
                            });
                        }
                    }
                }
                return message;
            }
        });
    }
}
