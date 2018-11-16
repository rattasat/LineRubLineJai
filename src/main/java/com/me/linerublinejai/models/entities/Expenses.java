package com.me.linerublinejai.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Expenses {
    @Id
    private String id;

    private String lineUserId;

    private String name;

    private Date date;

    private Integer price;

    public Expenses() {}

    public Expenses(String lineUserId, String name, Date date, Integer price) {
        this.lineUserId = lineUserId;
        this.name = name;
        this.date = date;
        this.price = price;
    }
}
