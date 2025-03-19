package com.ssafy.tournest.api.controller.user;

import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.api.service.UserService;
import com.ssafy.tournest.api.service.dto.EmailCheckRequest;
import com.ssafy.tournest.api.service.dto.UserIdCheckRequest;
import com.ssafy.tournest.api.service.dto.UserSignUpRequest;
import com.ssafy.tournest.common.ApiResponse;
import javax.validation.Valid;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    public static Long userSeq;
    @GetMapping
    public ApiResponse getUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userService.getUser(principal.getUsername());
        userSeq = user.getUserSeq();
        return ApiResponse.success("user", user);
    }
    @PostMapping("/signup")
    public ResponseEntity<User> signUp(
            @Valid @ModelAttribute UserSignUpRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        User createdUser = userService.createUser(request, profileImage);
        return ResponseEntity.ok(createdUser);
    }
    @PostMapping("/check-userId")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestBody UserIdCheckRequest request) {
        boolean isAvailable = userService.checkUserIdAvailability(request.getUserId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody EmailCheckRequest request) {
        boolean isAvailable = userService.checkEmailAvailability(request.getEmail());
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }
}
