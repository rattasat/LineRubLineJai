package com.me.linerublinejai.models.requests;

import com.me.linerublinejai.utils.ValidationMessage;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
public class ExpenseAddRequest {

    @NotNull(message = ValidationMessage.LINE_USER_ID_REQUIRE)
    private String lineUserId;

    @NotNull(message = ValidationMessage.EXPENSES_NAME_REQUIRE)
    private String name;

    @NotNull(message = ValidationMessage.EXPENSES_PRICE_REQUIRE)
    @Range(min = 1, max = 99999, message = ValidationMessage.PRICE_RANGE)
    private Integer price;

}
