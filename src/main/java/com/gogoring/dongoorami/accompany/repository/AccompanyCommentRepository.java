package com.gogoring.dongoorami.accompany.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyCommentRepository extends JpaRepository<AccompanyComment, Long> {

    Optional<AccompanyComment> findByIdAndIsActivatedIsTrue(Long id);

    Long countByIsActivatedIsTrueAndIsAccompanyApplyCommentTrue();

    boolean existsByMemberIdAndIsAccompanyApplyCommentTrue(Long memberId);
}
