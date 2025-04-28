package com.example.MovieWebsiteProject.Service;

import com.example.MovieWebsiteProject.Entity.InvalidatedToken;
import com.example.MovieWebsiteProject.Entity.User;
import com.example.MovieWebsiteProject.Exception.AppException;
import com.example.MovieWebsiteProject.Exception.ErrorCode;
import com.example.MovieWebsiteProject.Repository.InvalidatedTokenRepository;
import com.example.MovieWebsiteProject.Repository.UserRepository;
import com.example.MovieWebsiteProject.dto.projection.UserAuthInfo;
import com.example.MovieWebsiteProject.dto.request.AuthenticationRequest;
import com.example.MovieWebsiteProject.dto.request.IntrospectRequest;
import com.example.MovieWebsiteProject.dto.response.AuthenticationResponse;
import com.example.MovieWebsiteProject.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    JwtService jwtService;

    public UserAuthInfo authenticateUserAccount(AuthenticationRequest request) {
        UserAuthInfo user = userRepository.findUsernameAndPasswordByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTES));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        } else {
            return user;
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        boolean authenticated = false;

        UserAuthInfo user = authenticateUserAccount(request);
        authenticated = true;
        String token = "null";
        try {
            token = jwtService.generateToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Server error!, " + e.getMessage());
        }
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(authenticated)
                .role(user.getRole())
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            jwtService.verifyToken(request.getToken());
        } catch (Exception e) {
            System.out.println("Lỗi khi verify token: " + e.getMessage());
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void logout(String token) throws ParseException, JOSEException {
        var signedToken = jwtService.verifyToken(token);
        String jwtID = signedToken.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtID)
                .expiryTime(expirationTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        System.out.println("principal: " + authentication.getPrincipal());
        throw new AppException(ErrorCode.EXPIRED_LOGIN_SESSION);
    }

    public String extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                System.out.println("token logout = " + cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }
}
