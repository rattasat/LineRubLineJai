package com.me.linerublinejai.controllers;

import com.me.linerublinejai.models.requests.ExpenseAddRequest;
import com.me.linerublinejai.services.ExpensesService;
import com.me.linerublinejai.services.LineWebHookService;
import com.me.linerublinejai.utils.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping(Protocol.API_VERSION)
public class LineRubLineJaiController {

    @Autowired
    private ExpensesService expensesService;

    @Autowired
    private LineWebHookService lineWebHookService;

    @PostMapping(Protocol.EXPENSES)
    public ResponseEntity<?> saveExpenses(@RequestBody @Valid ExpenseAddRequest request) throws Exception {
        expensesService.saveExpensesFromAPI(request);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(Protocol.MULTICAST_ADD_EXPENSES)
    public ResponseEntity<?> multicastAddExpenses(@RequestParam(name = "time") Short time) throws Exception {
        lineWebHookService.multicastExpensesMessage(time);
        return new ResponseEntity(HttpStatus.OK);
    }

}
