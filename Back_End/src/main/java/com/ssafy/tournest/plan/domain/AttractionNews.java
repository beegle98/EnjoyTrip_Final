package com.ssafy.tournest.plan.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Attr;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="tourism_news")
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class AttractionNews {
    @Id
    @GeneratedValue
    private Long no;

    @Column(length = 500,name="attr_name")
    private String attrName;

    @Column(length = 500)
    private String title;

    @Column(length = 1023)
    private String link;

    @Column(length = 5000)
    private String description;

    @Column(name = "pubDate")
    private LocalDate endDate;
}
