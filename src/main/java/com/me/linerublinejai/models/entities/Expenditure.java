package com.me.linerublinejai.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Expenditure {
    @Id
    private String id;

    private String lineUserId;

    private String activity;

    private Date date;

    private Integer price;

    public Expenditure() {}

    public Expenditure(String lineUserId, String activity, Date date, Integer price) {
        this.lineUserId = lineUserId;
        this.activity = activity;
        this.date = date;
        this.price = price;
    }
}
