package com.pasinski.sl.backend.diet.forms;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.diet.Diet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DietResponseForm {
    private Long idDiet;
    private List<FinalDayResponseForm> finalDays;
    private String dietFileUrl;
    private String shoppingListFileUrl;

    public DietResponseForm(Diet diet) {
        this.idDiet = diet.getIdDiet();
        this.finalDays = new ArrayList<>();
        this.dietFileUrl = ApplicationConstants.DEFAULT_DIET_PDF_URL_WITH_PARAMETER + diet.getIdDiet();
        this.shoppingListFileUrl = ApplicationConstants.DEFAULT_GROCERIES_PDF_URL_WITH_PARAMETER + diet.getIdDiet();

        diet.getFinalDays().forEach(finalDay -> this.finalDays.add(new FinalDayResponseForm(finalDay)));
    }
}
