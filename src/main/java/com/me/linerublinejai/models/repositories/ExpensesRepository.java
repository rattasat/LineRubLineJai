package com.me.linerublinejai.models.repositories;

import com.me.linerublinejai.models.entities.Expenses;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ExpensesRepository extends MongoRepository<Expenses, String> {
    List<Expenses> findByLineUserIdAndDateBetween(String id, Date start, Date finish);
}
