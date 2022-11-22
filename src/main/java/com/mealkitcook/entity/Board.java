package com.mealkitcook.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends TimeEntity{

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public Board(Long id, String title, String content){
        this.id = id;
        this.title = title;
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;


}
