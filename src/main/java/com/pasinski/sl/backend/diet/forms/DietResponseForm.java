package com.pasinski.sl.backend.diet.forms;

import com.pasinski.sl.backend.diet.Diet;
import com.pasinski.sl.backend.file.S3Service;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DietResponseForm {
    private Long idDiet;
    private List<FinalDayResponseForm> finalDays;
    private String dietFileUrl;
    private String shoppingListFileUrl;

    public DietResponseForm(Diet diet, S3Service s3Service) {
        this.idDiet = diet.getIdDiet();
        this.finalDays = new ArrayList<>();

        diet.getFinalDays().forEach(finalDay -> this.finalDays.add(new FinalDayResponseForm(finalDay, s3Service)));
        this.finalDays.sort(Comparator.comparing(FinalDayResponseForm::getIdFinalDay));
    }
}
