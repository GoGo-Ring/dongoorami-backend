package com.gogoring.dongoorami.wish.repository;

import com.gogoring.dongoorami.accompany.domain.AccompanyPost;
import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.wish.domain.Wish;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    Boolean existsByAccompanyPostAndMember(AccompanyPost accompanyPost, Member member);

    Optional<Wish> findByAccompanyPostAndMember(AccompanyPost accompanyPost, Member member);
}