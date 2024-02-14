package com.gogoring.dongoorami.member.domain;

import com.gogoring.dongoorami.global.common.BaseEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    private String name;

    private String profileImage;

    private String provider;

    private String providerId;

    private String gender;

    private LocalDate birthDate;

    private String introduction;

    @Builder
    public Member(String name, String profileImage, String provider, String providerId) {
        this.name = name;
        this.profileImage = profileImage;
        this.provider = provider;
        this.providerId = providerId;
        this.roles = new ArrayList<>() {{
            add(Role.ROLE_MEMBER);
        }};
    }

    public Member updateName(String name) {
        this.name = name;
        return this;
    }
}
