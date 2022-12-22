package com.bitstudy.app.repository;


import com.bitstudy.app.config.JpaConfig;
import com.bitstudy.app.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

//import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // 슬라이드 테스트
/** 슬라이드 테스트란 지난번 TDD 때 각 메서드들 다 남남으로 서로를 알아보지 못하게 만들었었다. 이것처럼 메서드들 각각 테스트한 결과를 서로 못보게 잘라서 만드는것 *
 * 
 * */

@Import(JpaConfig.class)
/** 원래대로라면 JPA 에서 모든 정보를 컨트롤 해야되는데 JpaConfig 의 경우는 읽어오지 못한다. 이유는 이건 시스템에서 만든게 아니라 우리가 별도로 만든 파일이기 때문. 그래서 따로 import를 해줘야 한다.
 안하면 config 안에 명시해놨던 JpaAuditing 기능이 동작하지 않는다.
 * */

class Ex04_JpaRepositoryTest {
    private final Ex04_ArticleRepository_기본테스트용 articleRepository;
    private final Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository;

    /* 원래는 둘 다 @Autowired가 붙어야 하는데, JUnit5 버전과 최신 버전의 스프링 부트를 이용하면 Test에서 생성자 주입패턴을 사용할 수 있다.  */


    /* 생성자 만들기 - 여기서는 다른 파일에서 매개변수로 보내주는걸 받는거라서 위에랑 상관 없이 @Autowired 를 붙여야 함 */
    public Ex04_JpaRepositoryTest(@Autowired Ex04_ArticleRepository_기본테스트용 articleRepository, @Autowired Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }
    
    
    /* - 트랜잭션시 사용하는 메서드
        사용법: repository명.메소드()
        1) findAll() - 모든 컬럼을 조회할때 사용. 페이징(pageable) 가능
                        당연히 select 작업을 하지만, 잠깐 사이에 해당 테이블에 어떤 변화가 있었는지 알 수 없기 때문에 select 전에 먼저 최신 데이터를 잡기 위해서 update를 한다.
                        동작 순서 : update -> select

        2) findById() - 한 건에 대한 데이터 조회시 사용
                        primary key로 레코드 한검 조회.
        3) save() - 레코드 저장할때 사용 (insert, update)
           saveAndFlush - 강제 저장
        4) count() - 레코드 개수 뽑을때 사용
        5) delete() - 레코드 삭제
        ------------------------------------------------------
        
        - 테스트용 데이터 가져오기
            1) mockaroo 사이트 접속

    * */
    
    /* select 테스트 */
    @Test
    void selectTest() {
        /** 셀렉팅을 할거니까 articleRepository 를 기준으로 테스트 할거임.
            maven방식: dao -> mapper 로 정보 보내고 DB 갔다 와서 C 까지 돌려보낼건데 dao에서 DTO를 list에 담아서 return
         * */

        List<Article> articles  =  articleRepository.findAll();
        
        /** assertJ 를 이용해서 테스트 할거임
         * articles 가 NotNull 이고 사이즈가 ?? 개면 통과
         *
         * * */
        assertThat(articles).isNotNull().hasSize(100);

    }

    @DisplayName("insert 테스트")
    @Test
    void insertTest() {
        //기존의 article 개수를 센 다음에 insert하고 기존 숫자보다 현재 숫자가 1 차이 나면 insert가 제대로 됐다는 뜻.
        //기존 카운트 구하기
        long prevCount = articleRepository.count();
        //insert 하기
        //
        Article article = Article.of("제목", "내용", "해시태그");
        articleRepository.save(article);
        //
        assertThat(articleRepository.count()).isEqualTo(prevCount +1) ;
        /*
        *   이 사태로 테스트를 돌리면 createAt 이거 못찾는다고 에러 남.
        *   이유: jpaConfig 파일에 auditing을 쓰겠다고 세팅을 해놨는데,
        *   해당 엔티티(Article.java) 에서 auditing을 쓴다고 명시를 안해놓은 상태라서.
        *   엔티티 가서 클래스레벨로 @EntityListeners(AuditingEntityListener.class) 걸어주자
        * */

    }

    @DisplayName("update 테스트")
    @Test
    void updateTest() {
        /* 기존의 데이터 하나 있어야 하고 그걸 수정했을 때를 관찰할 것
         *
         * 1) 기존의 영속성 컨텍스트로부터 엔티티 객체 하나를 가져온다.(DB에서 한줄 뽑아온다)
         * articleRepository -> 기존의 영속성 컨텍스트로부터
         * findById(1L) -> 하나의 엔티티를 가져온다.(id가 long타입인 1번 가져오기)
         * .orElseThrow() -> 없으면 throw 시켜서 일단 테스트가 끝나게 만들기.
         * */
        Article article = articleRepository.findById(1L).orElseThrow();
        /* 2) 업데이트로 해시태그를 바꾸기
         * 엔티티에 있는 setter를 이용해서 updateHashtag에 있는 문자열로 업데이트 하기
         * 1.변수 updateHashtag 에 바꿀 문자열 저장
         * 2.엔티티(article)에 있는 setter 를 이용해서 변수에 있는 문자열을 넣고
         * (해시태그 바꿀거니까 setHashtag. 이름 어찌할지 모르겠으면 실제 엔티티 파일 가서 setter 만들어보기. 그 이름 그대로 쓰면 됨.)
         * 3.데이터베이스에 업데이트 하기.
         */
        String updateHashtag = "#abcd";
        article.setHashtag(updateHashtag);
        //articleRepository.save(article);
        /*save로 놓고 테스트를 돌리면 콘솔(Run)탭에 update 구문이 나오지 않고
        * select 구문만 나온다.
        * 이유는 영속성 컨텍스트로부터 가져온 데이터를 그냥 save만 하고
        * 아무것도 하지 않고 끝내버리면 어쩌피 롤백되니까 스프링부트는
        * 다시 원래의 값으로 돌아가 질거다. 그래서 그냥 했다 치고
        * update를 하지 않고 넘어간다.(코드의 유효성은 확인)
        * 그래서 save를 하고 flush를 해줘야 한다.
        * flush란 (push 같은거)
        * 1. 변경점 감지.
        * 2. 수정된 Entity를 sql 저장소에 등록
        * 3. sql 저장소에 있는 쿼리를 DB에 전송
        */
        Article savedArticle = articleRepository.saveAndFlush(article);

        /*
        * 3)위에서 바꾼 savedArticle에 업데이트 된 hashtag필드에
        * updatehashtag 에 저장되어 있는 값(#abcd) 이 있는지 확인해봐라
        * */
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);

    }

    @DisplayName("Delete 테스트")
    @Test
    void deleteTest() {
        /*
        * 기존에 데이터들이 있다고 치고, 값을 하나 꺼내고, 지워야 한다.
        *
        * 1) 기존의 영속성 컨텍스트로부터 엔티티 객체 하나를 가져온다.(DB에서 한줄 뽑아온다) findByID
        * 2) 지우면 DB에서 하나 사라지기 때문에 count를 구해놓고 count()
        * 3) delete 하고(-1)  .delete()
        * 4) 2번에서 구한 count와 지금 개수 비교해서 1차이나면 테스트 통과 isEqualTo()
        *
        * */

        Article article = articleRepository.findById(1L).orElseThrow();
        long prevArticleCount = articleRepository.count();
        //연관된 댓글까지 삭제되기 때문에 코멘트 숫자까지 알아야 한다.
        long prevArticleCommentCount = articleCommentRepository.count();
        //article(ID가 1인 아티클)에 딸려있는 Comment의 수
        int deletedCommentSize = article.getArticleComments().size();

        articleRepository.delete(article);

        //현재 아티클 수가 저번 아티클 수보다 1 적은지 확인. 같으면 pass
        assertThat(articleRepository.count()).isEqualTo(prevArticleCount -1);

        //현재 코멘트 수가 저번 코먼트 수 - 지운 아티클에 딸린 comment 수인지 확인
        assertThat(articleCommentRepository.count()).isEqualTo(prevArticleCommentCount - deletedCommentSize);


    }

}








