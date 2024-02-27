package com.gogoring.dongoorami.accompany.application;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostRequest;
import com.gogoring.dongoorami.accompany.dto.response.AccompanyPostsResponse;
import com.gogoring.dongoorami.accompany.repository.AccompanyPostRepository;
import com.gogoring.dongoorami.global.util.ImageType;
import com.gogoring.dongoorami.global.util.S3ImageUtil;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.member.exception.MemberErrorCode;
import com.gogoring.dongoorami.member.exception.MemberNotFoundException;
import com.gogoring.dongoorami.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccompanyServiceImpl implements AccompanyService {

    private final AccompanyPostRepository accompanyPostRepository;
    private final MemberRepository memberRepository;
    private final S3ImageUtil s3ImageUtil;
    @Value("${cloud.aws.s3.default-image-url}")
    private String defaultImageUrl;

    @Override
    public Long createAccompanyPost(AccompanyPostRequest accompanyPostRequest, Long memberId) {
        Member member = memberRepository.findByIdAndIsActivatedIsTrue(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<String> imageUrls = new ArrayList<>();
        if (accompanyPostRequest.getImages().isEmpty() || accompanyPostRequest.getImages().get(0).isEmpty()) {
            imageUrls.add(defaultImageUrl);
        } else {
            accompanyPostRequest.getImages().stream()
                    .map(image -> imageUrls.add(s3ImageUtil.putObject(image, ImageType.ACCOMPANY_POST)));
        }

        return accompanyPostRepository.save(accompanyPostRequest.toEntity(member, imageUrls))
                .getId();
    }

    @Override
    public AccompanyPostsResponse getAccompanyPosts(Long cursorId, int size) {
        Slice<AccompanyPost> accompanyPosts;
        if (cursorId == null) {
            accompanyPosts = accompanyPostRepository.findAllByOrderByIdDesc(
                    PageRequest.of(0, size));
        } else {
            accompanyPosts = accompanyPostRepository.findByIdLessThanOrderByIdDesc(
                    cursorId, PageRequest.of(0, size));
        }

        return new AccompanyPostsResponse(
                accompanyPosts.hasNext(),
                accompanyPosts.getContent().stream()
                        .map(accompanyPost ->
                                AccompanyPostsResponse.AccompanyPostInfo.builder()
                                        .id(accompanyPost.getId())
                                        .title(accompanyPost.getTitle())
                                        .gender(accompanyPost.getGender())
                                        .concertName(accompanyPost.getConcertName())
                                        .status(accompanyPost.getStatus().getName())
                                        .totalPeople(accompanyPost.getTotalPeople())
                                        .updatedAt(accompanyPost.getUpdatedAt())
                                        .writer(accompanyPost.getMember().getName())
                                        .viewCount(accompanyPost.getViewCount())
                                        .commentCount(0L) // 임시
                                        .build()
                        ).toList());
    }
}
