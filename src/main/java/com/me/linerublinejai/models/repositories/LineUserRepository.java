package com.me.linerublinejai.models.repositories;

import com.me.linerublinejai.models.entities.LineUser;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface LineUserRepository extends MongoRepository<LineUser, String> {
    LineUser findByLineUserId(String lineUserId);
}
