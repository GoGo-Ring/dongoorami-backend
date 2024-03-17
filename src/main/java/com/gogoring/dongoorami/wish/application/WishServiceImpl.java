package com.gogoring.dongoorami.wish.application;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.exception.AccompanyErrorCode;
import com.gogoring.dongoorami.accompany.exception.AccompanyPostNotFoundException;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import com.gogoring.dongoorami.wish.domain.Wish;
import com.gogoring.dongoorami.wish.exception.WishErrorCode;
import com.gogoring.dongoorami.wish.exception.WishNotFoundException;
import com.gogoring.dongoorami.wish.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;
    private final AccompanyPostRepository accompanyPostRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void createWish(Long accompanyPostId, Long memberId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                accompanyPostId).orElseThrow(() -> new AccompanyPostNotFoundException(
                AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (wishRepository.existsByAccompanyPostAndMember(accompanyPost, member)) {
            Wish wish = wishRepository.findByAccompanyPostAndMember(accompanyPost, member)
                    .orElseThrow(() -> new WishNotFoundException(WishErrorCode.WISH_NOT_FOUND));

            wish.updateIsActivatedTrue();
        } else {
            Wish wish = Wish.builder()
                    .accompanyPost(accompanyPost)
                    .member(member)
                    .build();

            wishRepository.save(wish);
        }
    }

    @Override
    public void deleteWish(Long accompanyPostId, Long memberId) {
        AccompanyPost accompanyPost = accompanyPostRepository.findByIdAndIsActivatedIsTrue(
                accompanyPostId).orElseThrow(() -> new AccompanyPostNotFoundException(
                AccompanyErrorCode.ACCOMPANY_POST_NOT_FOUND));
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));

        Wish wish = wishRepository.findByAccompanyPostAndMemberAndIsActivatedIsTrue(accompanyPost,
                member).orElseThrow(() -> new WishNotFoundException(WishErrorCode.WISH_NOT_FOUND));

        wish.updateIsActivatedFalse();
    }
}
