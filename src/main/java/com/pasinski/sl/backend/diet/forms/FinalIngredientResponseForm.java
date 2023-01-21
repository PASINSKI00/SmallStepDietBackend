package com.pasinski.sl.backend.diet.forms;

import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalIngredientResponseForm {
    private Long idFinalIngredient;
    private String name;
    private Integer weight;

    public FinalIngredientResponseForm(FinalIngredient finalIngredient) {
        this.idFinalIngredient = finalIngredient.getIdFinalIngredient();
        this.name = finalIngredient.getIngredient().getName();
        this.weight = finalIngredient.getWeight();
    }
}
