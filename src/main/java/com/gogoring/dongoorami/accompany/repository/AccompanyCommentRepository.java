package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyCommentRepository extends JpaRepository<AccompanyComment, Long> {

    Optional<AccompanyComment> findByIdAndIsActivatedIsTrue(Long id);

    List<AccompanyComment> findAllByAccompanyPostId(Long accompanyPostId);

    Long countByAccompanyPostIdAndIsActivatedIsTrueAndIsAccompanyApplyCommentTrue(
            Long accompanyPostId);

    boolean existsByAccompanyPostIdAndMemberIdAndIsAccompanyApplyCommentTrue(Long accompanyPostId,
            Long memberId);
}
