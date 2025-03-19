package com.ssafy.tournest.api.controller.auth;

import com.ssafy.tournest.api.entity.auth.AuthReqModel;
import com.ssafy.tournest.api.entity.user.UserRefreshToken;
import com.ssafy.tournest.api.repository.user.UserRefreshTokenRepository;
import com.ssafy.tournest.common.ApiResponse;
import com.ssafy.tournest.config.properties.AppProperties;
import com.ssafy.tournest.oauth.entity.RoleType;
import com.ssafy.tournest.oauth.entity.UserPrincipal;
import com.ssafy.tournest.oauth.token.AuthToken;
import com.ssafy.tournest.oauth.token.AuthTokenProvider;
import com.ssafy.tournest.utils.CookieUtil;
import com.ssafy.tournest.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    @PostMapping("/login")
    public ApiResponse login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AuthReqModel authReqModel
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReqModel.getId(),
                        authReqModel.getPassword()
                )
        );

        String userId = authReqModel.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userId,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // refresh token 쿠키에서 가져옴
        String CookieRefreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(userId);
        if (userRefreshToken == null) {
            // 없는 경우 새로 등록
            AuthToken refreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            userRefreshToken = new UserRefreshToken(userId, refreshToken.getToken());
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
            System.out.println("AuthController Cookie Update: "+refreshToken.getToken());
            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
        }else if(!userRefreshToken.getRefreshToken().equals(CookieRefreshToken)){

            System.out.println("AuthController Cookie Update: "+userRefreshToken.getRefreshToken());
            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, userRefreshToken.getRefreshToken(), cookieMaxAge);
        }

        return ApiResponse.success("token", accessToken.getToken());
    }

    @GetMapping("/refresh")
    public ApiResponse refreshToken (HttpServletRequest request, HttpServletResponse response) {
        // access token 확인

        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

        System.out.println("refresh controller..... accessToken: " +accessToken);
//        if (!authToken.validate()) {
//            System.out.println("accessToken Invalid");
//            return ApiResponse.invalidAccessToken();
//        }


        // expired access token 인지 확인
//        Claims claims = authToken.getExpiredTokenClaims();
//        if (claims == null) {
//            return ApiResponse.notExpiredTokenYet();
//        }

        Claims claims = authToken.getClaimFromToken();

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // refresh token 쿠키에서 가져옴
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        System.out.println("authRefreshToken: "+authRefreshToken);

//        if (authRefreshToken.validate()) {
//            System.out.println("RefreshToken invalid");
//            return ApiResponse.invalidRefreshToken();
//        }


        // userId refresh token 으로 DB 확인
        System.out.println("userId: "+userId);
        System.out.println("RefreshToken: "+refreshToken);
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);
        if (userRefreshToken == null) {
            return ApiResponse.invalidRefreshToken();
        }

        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );
        System.out.println("newAccessToken: "+newAccessToken.getToken());
        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
//        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();
//        if (validTime <= THREE_DAYS_MSEC) {
//            // refresh 토큰 설정
//            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
//
//            authRefreshToken = tokenProvider.createAuthToken(
//                    appProperties.getAuth().getTokenSecret(),
//                    new Date(now.getTime() + refreshTokenExpiry)
//            );
//
//            // DB에 refresh 토큰 업데이트
//            userRefreshToken.setRefreshToken(authRefreshToken.getToken());
//
//            int cookieMaxAge = (int) refreshTokenExpiry / 60;
//            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
//            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
//        }

        return ApiResponse.success("token", newAccessToken.getToken());
    }

}
