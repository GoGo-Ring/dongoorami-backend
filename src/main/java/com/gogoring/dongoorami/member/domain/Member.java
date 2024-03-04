package com.gogoring.dongoorami.member.domain;

import com.gogoring.dongoorami.global.common.BaseEntity;
import com.gogoring.dongoorami.member.exception.AlreadySignUpException;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    private Integer manner;

    @Builder
    public Member(String name, String profileImage, String provider, String providerId) {
        this.name = name;
        this.profileImage = profileImage;
        this.provider = provider;
        this.providerId = providerId;
        this.roles = new ArrayList<>() {{
            add(Role.ROLE_MEMBER);
        }};
        this.manner = 0;
    }

    public List<GrantedAuthority> getRoles() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : this.roles) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }

        return authorities;
    }

    public Integer getAge() {
        return this.birthDate == null ? null
                : (LocalDate.now().getYear() - this.getBirthDate().getYear()) + 1;
    }

    public Member updateName(String name) {
        this.name = name;
        return this;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateGenderAndBirthDate(String gender, LocalDate birthDate) {
        checkGenderAndBirthDateIsNull();
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void updateNameAndIntroduction(String name, String introduction) {
        this.name = name;
        this.introduction = introduction;
    }

    private void checkGenderAndBirthDateIsNull() {
        if (gender != null || birthDate != null) {
            throw new AlreadySignUpException(MemberErrorCode.ALREADY_SIGN_UP);
        }
    }
}
