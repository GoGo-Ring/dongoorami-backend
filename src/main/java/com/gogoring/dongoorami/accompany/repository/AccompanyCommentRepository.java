package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyCommentRepository extends JpaRepository<AccompanyComment, Long>,
        AccompanyCommentCustomRepository {

    Optional<AccompanyComment> findByIdAndIsActivatedIsTrue(Long id);

    List<AccompanyComment> findAllByAccompanyPostIdAndIsActivatedIsTrue(Long accompanyPostId);

    long countByAccompanyPostIdAndIsActivatedIsTrue(Long accompanyPostId);

    Long countByAccompanyPostIdAndIsActivatedIsTrueAndIsAccompanyApplyCommentTrue(
            Long accompanyPostId);

    boolean existsByAccompanyPostIdAndIsActivatedIsTrueAndMemberIdAndIsAccompanyApplyCommentTrue(
            Long accompanyPostId, Long memberId);
}
