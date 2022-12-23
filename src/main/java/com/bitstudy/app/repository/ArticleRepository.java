package com.bitstudy.app.repository;

import com.bitstudy.app.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/** TDD를 위해서 임시로 만들어놓은 저장소 (이거로 DB에 접근할거다)
 *
 * - TDD 만드는 방법
 * 1) 우클릭  > Go to > Test (ctrl + shift + T)
 * 2) JUint5 버전인지 확인
 * 3) 이름 ArticleRepositoryTest 를 JpaRepositoryTest 로 변경
 * */

/*
*  할 일 - 클래스 레벨에 @RepositoryRestResource 넣어서 해당 클래스를 spring rest data 사용할 준비 해두기
* */
@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
