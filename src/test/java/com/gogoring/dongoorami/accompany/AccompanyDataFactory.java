package com.gogoring.dongoorami.accompany;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.accompany.domain.AccompanyPost.AccompanyPurposeType;
import com.gogoring.dongoorami.accompany.dto.request.AccompanyPostFilterRequest;
import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.member.domain.Member;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class AccompanyDataFactory {

    @Value("${cloud.aws.s3.default-image-url}")
    static private String defaultImageUrl;

    static public List<MockMultipartFile> createMockMultipartFiles(int size) throws Exception {
        List<MockMultipartFile> images = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            images.add(new MockMultipartFile("images", "김영한.JPG",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream("src/test/resources/김영한.JPG")));
        }

        return images;
    }

    static public List<String> createImageUrls(int size) {
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            imageUrls.add(defaultImageUrl);
        }

        return imageUrls;
    }

    static public List<AccompanyComment> createAccompanyComment(Member member, int size) {
        List<AccompanyComment> accompanyComments = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            accompanyComments.add(
                    AccompanyComment.builder().member(member).content("가는 길만 동행해도 괜찮을까요!?")
                            .isAccompanyApplyComment(false)
                            .build());
        }

        return accompanyComments;
    }

    static public List<AccompanyPost> createAccompanyPosts(Member member, int size,
            Concert concert) {
        List<AccompanyPost> accompanyPosts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            accompanyPosts.add(AccompanyPost.builder()
                    .writer(member)
                    .concert(concert)
                    .startDate(LocalDate.of(2024, 3, 22))
                    .endDate(LocalDate.of(2024, 3, 22))
                    .title("서울 같이 갈 울싼 사람 구합니다~~")
                    .gender("여")
                    .region("수도권(경기, 인천 포함)")
                    .content("같이 올라갈 사람 구해요~")
                    .startAge(23L)
                    .endAge(37L)
                    .totalPeople(2L)
                    .purposes(Arrays.asList(AccompanyPurposeType.ACCOMMODATION,
                            AccompanyPurposeType.TRANSPORTATION)).build());
        }

        return accompanyPosts;
    }

    static public List<AccompanyPost> createAccompanyPosts(Member member, int size,
            AccompanyPostFilterRequest accompanyPostFilterRequest, Concert concert) {
        List<AccompanyPost> accompanyPosts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            accompanyPosts.add(AccompanyPost.builder()
                    .writer(member)
                    .concert(concert)
                    .startDate(LocalDate.of(2024, 3, 22))
                    .endDate(LocalDate.of(2024, 3, 22))
                    .title("서울 같이 갈 울싼 사람 구합니다~~")
                    .gender(accompanyPostFilterRequest.getGender())
                    .region(accompanyPostFilterRequest.getRegion())
                    .content("같이 올라갈 사람 구해요~")
                    .startAge(accompanyPostFilterRequest.getStartAge())
                    .endAge(accompanyPostFilterRequest.getEndAge())
                    .totalPeople(accompanyPostFilterRequest.getTotalPeople())
                    .purposes(accompanyPostFilterRequest.getPurposes().stream().map(
                            AccompanyPurposeType::getValue).toList()).build());
        }

        return accompanyPosts;
    }

}