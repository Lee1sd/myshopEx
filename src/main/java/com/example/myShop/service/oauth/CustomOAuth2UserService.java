package com.example.myShop.service.oauth;

import com.example.myShop.entity.Member;
import com.example.myShop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = extractEmail(attributes);
        String name = extractNickname(attributes);

        Member member = memberService.saveOrUpdateKakaoMember(email, name);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + member.getRole().name())),
                attributes,
                "id"
        );
    }

    private String extractEmail(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = getMap(attributes, "kakao_account");
        Object emailObj = kakaoAccount.get("email");
        if (emailObj != null) {
            return emailObj.toString();
        }

        Object idObj = attributes.get("id");
        if (idObj == null) {
            throw new OAuth2AuthenticationException("Kakao response does not include user id.");
        }
        return idObj + "@kakao.local";
    }

    private String extractNickname(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = getMap(attributes, "kakao_account");
        Map<String, Object> profile = getMap(kakaoAccount, "profile");
        Object nicknameObj = profile.get("nickname");
        if (nicknameObj != null) {
            return nicknameObj.toString();
        }
        return "kakao-user";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }
}
