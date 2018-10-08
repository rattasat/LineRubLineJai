package com.me.linerublinejai.services;

import com.me.linerublinejai.models.entities.Expenditure;
import com.me.linerublinejai.models.repositories.ExpenditureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExpenditureService {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    public List<Expenditure> getExpenditure(String userId, Date start, Date finish) throws Exception {
        return expenditureRepository.findByLineUserIdAndDateBetween(userId, start, finish);
    }

    public void saveExpenditure(Expenditure expenditure) throws Exception {
        expenditureRepository.save(expenditure);
    }
}
