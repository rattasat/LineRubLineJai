package com.me.linerublinejai.models.repositories;

import com.me.linerublinejai.models.entities.Expenditure;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ExpenditureRepository extends MongoRepository<Expenditure, String> {
    List<Expenditure> findByLineUserIdAndDateBetween(String id, Date start, Date finish);
}
