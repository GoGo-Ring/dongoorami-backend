package com.gogoring.dongoorami.message.repository;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.message.domain.Message;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface MessageCustomRepository {

    Slice<Message> findLatestMessages(Member member, Long cursorId, int size,
            List<Long> partnerIds);

    Slice<Message> findMessagesWithPartner(Member sender, Member receiver, Long cursorId, int size);

}
