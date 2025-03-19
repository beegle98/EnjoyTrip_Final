package com.ssafy.tournest.plan.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="attractions")
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Attraction {

    @Id @GeneratedValue @Column(name="no")
    private Long id;

    private Integer  content_id;

    @Column(length = 500)
    private String title;

    private Integer content_type_id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="si_gun_gu_code")
//    private Gugun guguns;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="area_code")
//    private Sido sidos;

    private Integer si_gun_gu_code;

    private Integer area_code;

    @Column(length = 100)
    private String first_image1;

    private Integer mapLevel;

    @Column(length = 100)
    private String first_image2;

    @Column(length = 1000)
    private String homepage;

    @Column(precision = 20, scale = 17)
    private BigDecimal latitude;

    @Column(precision = 20, scale = 17)
    private BigDecimal longitude;

    @Column(length = 20)
    private String tel;

    @Column(length = 100)
    private String addr1;

    @Column(length = 100)
    private String addr2;

    @Column(length = 10000)
    private String overview;
}
