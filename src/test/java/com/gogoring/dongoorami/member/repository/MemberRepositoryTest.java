package com.gogoring.dongoorami.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gogoring.dongoorami.global.config.QueryDslConfig;
import com.gogoring.dongoorami.member.MemberDataFactory;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("id로 회원을 조회할 수 있다.")
    void success_findByIdAndIsActivatedIsTrue() {
        // given
        Member member = MemberDataFactory.createMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        memberRepository.save(member);

        // when
        Member savedMember = memberRepository.findByIdAndIsActivatedIsTrue(member.getId())
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        // then
        assertThat(savedMember.getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("providerId로 회원을 조회할 수 있다.")
    void success_findByProviderIdAndIsActivatedIsTrue() {
        // given
        Member member = MemberDataFactory.createMember();
        memberRepository.save(member);

        // when
        Member savedMember = memberRepository.findByProviderIdAndIsActivatedIsTrue(
                member.getProviderId()).orElseThrow(() -> new MemberNotFoundException(
                MemberErrorCode.MEMBER_NOT_FOUND));

        // then
        assertThat(savedMember.getProviderId()).isEqualTo(member.getProviderId());
    }
}
