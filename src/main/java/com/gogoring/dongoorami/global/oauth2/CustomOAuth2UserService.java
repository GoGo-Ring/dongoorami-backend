package com.gogoring.dongoorami.global.oauth2;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.domain.Role;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.getAttributes());

        Member member = saveOrUpdate(oAuth2UserInfo);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : member.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }

        return new CustomOAuth2User(provider, oAuth2User.getAttributes(), authorities,
                member.getProviderId(), member);
    }

    private Member saveOrUpdate(OAuth2UserInfo oAuth2UserInfo) {
        Member member = memberRepository.findByProviderIdAndIsActivatedIsTrue(
                        oAuth2UserInfo.getProviderId())
                .map(entity -> entity.updateName(oAuth2UserInfo.getName()))
                .orElse(oAuth2UserInfo.toEntity());

        return memberRepository.save(member);
    }
}
