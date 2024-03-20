package com.gogoring.dongoorami.message.repository;

import com.gogoring.dongoorami.member.domain.Member;
import com.gogoring.dongoorami.message.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageCustomRepository {

}
