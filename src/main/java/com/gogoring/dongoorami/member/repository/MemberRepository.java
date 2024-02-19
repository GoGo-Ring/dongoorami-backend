package com.gogoring.dongoorami.member.repository;

import com.gogoring.dongoorami.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderIdAndIsActivatedIsTrue(String providerId);
}
