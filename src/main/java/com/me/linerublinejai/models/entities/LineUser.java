package com.me.linerublinejai.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class LineUser {
    @Id
    private String id;

    private String lineUserId;


    public LineUser(String id, String lineUserId) {
        this.id = id;
        this.lineUserId = lineUserId;
    }

    public LineUser(String lineUserId) {
        this.lineUserId = lineUserId;
    }

    public LineUser() {}
}
