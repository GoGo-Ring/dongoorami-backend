package com.gogoring.dongoorami.wish.repository;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.wish.domain.Wish;
import org.springframework.data.domain.Slice;

public interface WishCustomRepository {

    Slice<Wish> findAllByMember(Long cursorId, int size, Member member);
}
