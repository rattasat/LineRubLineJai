package com.me.linerublinejai.services;

import com.me.linerublinejai.exceptions.CustomErrorException;
import com.me.linerublinejai.models.entities.Expenses;
import com.me.linerublinejai.models.entities.LineUser;
import com.me.linerublinejai.models.repositories.ExpensesRepository;
import com.me.linerublinejai.models.requests.ExpenseAddRequest;
import com.me.linerublinejai.models.responses.ErrorResponse;
import com.me.linerublinejai.types.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExpensesService {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private LineUserService lineUserService;

    public List<Expenses> getExpenses(String userId, Date start, Date finish) throws Exception {
        return expensesRepository.findByLineUserIdAndDateBetween(userId, start, finish);
    }

    public void saveExpenses(Expenses expenses) throws Exception {
        expensesRepository.save(expenses);
    }

    public void saveExpensesFromAPI(ExpenseAddRequest request) throws Exception {
        LineUser lineUser = lineUserService.getLineUser(request.getLineUserId());
        if (lineUser == null) {
            throw new CustomErrorException(new ErrorResponse(ErrorType.LINE_USER_NOT_FOUND.getCode(),
                    ErrorType.LINE_USER_NOT_FOUND.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        try {
            saveExpenses(new Expenses(request.getLineUserId(), request.getName(), new Date(), request.getPrice()));
        } catch (Exception ex) {
            throw new CustomErrorException(new ErrorResponse(ErrorType.SERVER_ERROR.getCode(),
                    ErrorType.SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
