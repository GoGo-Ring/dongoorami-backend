package com.gogoring.dongoorami.global.oauth2;

import com.gogoring.dongoorami.member.domain.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    private String provider;
    private Map<String, Object> attributes;
    private List<GrantedAuthority> authorities;
    private String providerId;
    private Member member;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return this.provider;
    }

    public String getProviderId() {
        return this.providerId;
    }

    public Member getMember() {
        return this.member;
    }
}
