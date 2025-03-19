package com.ssafy.tournest.api.service;

import com.ssafy.tournest.api.entity.user.User;
import com.ssafy.tournest.api.entity.user.UserRefreshToken;
import com.ssafy.tournest.api.repository.user.UserRefreshTokenRepository;
import com.ssafy.tournest.api.repository.user.UserRepository;
import com.ssafy.tournest.api.service.dto.UserSignUpRequest;
import com.ssafy.tournest.oauth.entity.ProviderType;
import com.ssafy.tournest.oauth.entity.RoleType;
import com.ssafy.tournest.oauth.entity.UserPrincipal;
import com.ssafy.tournest.oauth.token.AuthToken;
import com.ssafy.tournest.oauth.token.AuthTokenProvider;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ssafy.tournest.config.properties.AppProperties;
import java.util.Date;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.dir}")
    private String projectPath;

    @Transactional
    public User createUser(UserSignUpRequest request, MultipartFile profileImage) {
        // 기존 중복 체크 로직
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        String imageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 파일 이름 생성 (중복 방지를 위해 UUID 사용)
                String fileName = UUID.randomUUID().toString() +
                        getFileExtension(profileImage.getOriginalFilename());
                System.out.println("fileName = " + fileName);
                System.out.println("profileImage = " + profileImage.getOriginalFilename());

                // 프로젝트 루트 디렉토리 아래에 업로드 디렉토리 생성
                String uploadPath = projectPath + File.separator + "src" + File.separator +
                        "main" + File.separator + "resources" + File.separator +
                        "static" + File.separator + "Images" + File.separator + "profileImages";
                File uploadDir = new File(uploadPath);
                System.out.println("uploadPath = " + uploadPath);

                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs();
                    System.out.println("Directory created: " + created);
                }

                // 파일 저장
                System.out.println("path: "+uploadDir + File.separator + fileName);
                File destination = new File(uploadDir + File.separator + fileName);
                profileImage.transferTo(destination);
                // 이미지 URL 생성
                imageUrl = "http://localhost:8080/tournest/images/profileImages/" + fileName;
                System.out.println("imageUrl = " + imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패", e);
            }
        }

        // 사용자 생성
        User user = new User(
                request.getUserId(),
                request.getUsername(),
                request.getEmail(),
                "Y",
                imageUrl,
                ProviderType.LOCAL,
                RoleType.USER,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        // RefreshToken 생성 및 저장
        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                user.getUserId(),
                "ROLE_USER",
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        UserRefreshToken userRefreshToken = new UserRefreshToken(
                savedUser.getUserId(),
                refreshToken.getToken()
        );

        refreshTokenRepository.save(userRefreshToken);

        return savedUser;
    }
    public boolean checkUserIdAvailability(String userId) {
        return !userRepository.existsByUserId(userId);
    }
    public boolean checkEmailAvailability(String email) {
        return !userRepository.existsByEmail(email);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }

    public String getUsernameById(String userId) {
        return userRepository.findByUserId(userId)
                .getUsername();
    }

}
