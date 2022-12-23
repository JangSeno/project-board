package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/*
* 할 일: Article.java와 ArticleComment.java의 중복필드 합치기
*
* 1) Article에 있는 메타데이터들(auditing에 관련된 필드들) 가져오기
* 2) 클래스 레벨에다가 @MappedSuperclass 달아주기
* 3) Auditing과 관련된 어노테이션 가져오기
*    ex) Article에서는 @EntityListeners(AuditingEntityListener.class)
*
*
*  * 파싱 : 일정한 문법을 토대로 나열된 data를 그 문법에 맞춰서 분석해서 새롭게 구성하는거
*
* */

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter // 모든 필드의 getter 들이 생성
@ToString // 모든 필드의 toString 생성
public class AuditingFields {
    //메타데이터
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt; // 생성일자

    @CreatedBy
    @Column(nullable = false,length = 100)
    private String createBy; // 생성자

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일자

    @LastModifiedBy
    @Column(nullable = false,length = 100)
    private String modifiedBy; // 수정자

}
