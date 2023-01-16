package com.pasinski.sl.backend.diet.forms;

import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DietResponseForm {
    private Long idDiet;
    private List<FinalDayResponseForm> finalDays;
    private String dietFileUrl;
    private String shoppingListFileUrl;
}
