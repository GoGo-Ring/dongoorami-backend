package com.gogoring.dongoorami.global.customMockUser;

import com.gogoring.dongoorami.global.jwt.CustomUserDetails;
import com.gogoring.dongoorami.member.domain.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class WithCustomMockUserSecurityContextFactory implements
        WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        Long id = 1L;
        String profileImage = annotation.profileImage();
        String provider = annotation.provider();
        String providerId = annotation.providerId();

        Member member = Member.builder()
                .profileImage(profileImage)
                .provider(provider)
                .providerId(providerId)
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                customUserDetails, "password", member.getRoles());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);

        return context;
    }
}
