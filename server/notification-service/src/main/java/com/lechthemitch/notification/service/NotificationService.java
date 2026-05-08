package com.lechthemitch.notification.service;

import com.lechthemitch.notification.dto.AttendanceNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    // Maps user email to their active SSE emitter
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String email) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));
        emitter.onError((e) -> emitters.remove(email));
        
        emitters.put(email, emitter);
        log.info("User subscribed for real-time notifications: {}", email);
        
        // Send initial connection event
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (IOException e) {
            emitters.remove(email);
        }
        
        return emitter;
    }

    public void sendAttendanceNotification(AttendanceNotificationRequest request) {
        log.info("Processing attendance notification for student: {}", request.getStudentName());
        
        String parentEmail = request.getParentEmail();
        SseEmitter emitter = emitters.get(parentEmail);
        
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("ATTENDANCE")
                        .data(request));
                log.info("Sent real-time notification to: {}", parentEmail);
            } catch (IOException e) {
                log.error("Failed to send real-time notification to: {}. Removing emitter.", parentEmail);
                emitters.remove(parentEmail);
            }
        } else {
            log.info("No active UI connection for parent: {}. Skipping real-time push.", parentEmail);
        }
        
        // Legacy logging
        log.info("Notification details - Student: {}, Session: {}, Time: {}", 
                request.getStudentName(), request.getSessionName(), request.getAttendanceTime());
    }
}
