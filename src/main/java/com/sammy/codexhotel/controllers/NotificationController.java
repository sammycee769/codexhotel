package com.sammy.codexhotel.controllers;

import com.sammy.codexhotel.data.models.Notification;
import com.sammy.codexhotel.dtos.responses.ApiResponse;
import com.sammy.codexhotel.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse>getUserNotifications(@PathVariable String userId) {
        List<Notification> userNotifications = notificationService.getUserNotifications(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true,"Notifications retrieved",userNotifications));
    }

}
