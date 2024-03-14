package com.gogoring.dongoorami.concert;

import com.gogoring.dongoorami.concert.domain.Concert;
import com.gogoring.dongoorami.concert.domain.ConcertReview;
import com.gogoring.dongoorami.member.domain.Member;
import java.util.ArrayList;
import java.util.List;

public class ConcertDataFactory {

    public static Concert createConcert() {
        return Concert.builder()
                .kopisId("abcefg")
                .name("고고링 백걸즈의 스프링 탐방기")
                .startedAt("2024.03.12")
                .endedAt("2024.05.12")
                .place("예술의전당 [서울] (리사이틀홀)")
                .actor("이유정, 최정은 등")
                .crew("김현, 박수빈, 박해성 등")
                .runtime("1시간 30분")
                .age("만 7세 이상")
                .producer("라이브러리컴퍼니")
                .agency("(재)서초문화재단")
                .host("리드예술기획, 앙상블 자비에")
                .management("(재)창원문화재단, 창원시립예술단")
                .cost("R석 30,000원, S석 20,000원, A석 10,000원")
                .poster("http://www.kopis.or.kr/upload/pfmPoster/PF_PF236579_240304_151739.gif")
                .summary("""
                        [공연소개]

                        오케스트라로 만나는 영화음악!
                        영화와 오케스트라는 강력한 시너지를 발휘하여 영화음악이 더욱 감동적이고 효과적으로 전달되도록 도와준다.
                        오케스트라는 다양한 악기들과 섹션들로 구성된 큰 악단으로, 감정, 분위기, 장면 전환 등을 강력하게 표현하는 데 사용된다.
                        한국 영화음악을 대표하는 넘버들을 오케스트라 버전으로 만나본다.

                        [프로그램]

                        ● 드라마 ［마에스트라］ Maestra
                        ● 영화 ［인어공주］ Main Theme
                        ● 영화 ［덕혜옹주］ Main Theme
                        ● 영화 ［여고괴담］ Memento Mori

                        * 상기 프로그램은 연주자의 사정에 따라 일부 변경될 수 있습니다.""")
                .genre("서양음악(클래식)")
                .status("공연중")
                .schedule("화요일 ~ 금요일(19:30), 토요일 ~ 일요일(15:00)")
                .introductionImages(
                        List.of("http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF237130_240311_0226522.jpg",
                                "http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF237130_240311_0226521.jpg"))
                .build();
    }

    public static List<ConcertReview> createConcertReviews(Concert concert, Member member,
            int size) {
        List<ConcertReview> concertReviews = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            concertReviews.add(ConcertReview.builder()
                    .concert(concert)
                    .member(member)
                    .title("최고의 공연입니다~")
                    .content("재관람 의향 있어요 너무너무 재밌었습니다!")
                    .rating(5)
                    .build());
        }

        return concertReviews;
    }
}
