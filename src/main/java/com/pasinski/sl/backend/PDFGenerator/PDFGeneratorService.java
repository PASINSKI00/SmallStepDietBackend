package com.pasinski.sl.backend.PDFGenerator;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.diet.Diet;
import com.pasinski.sl.backend.diet.forms.Grocery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PDFGeneratorService {

    public String generateDietPDF(Diet diet) throws FileNotFoundException {
        Document document = new Document();
        String fileName = "Diet_" + UUID.randomUUID() + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(ApplicationConstants.PATH_TO_PDF_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName));

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD);
        Font fontDayAndMeal = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
        Font fontContent = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        Font fontContentBold = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);

        document.open();

        Paragraph title = new Paragraph("This is your Diet!", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        Integer[] i = {1};

        diet.getFinalDays().forEach(day -> {
            Paragraph dayTitle = new Paragraph("\n\nDay "+ i[0]++, fontDayAndMeal);
            dayTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(dayTitle);

            Paragraph dayInfo = new Paragraph("Calories: " + day.getCalories() + " kcal\n" + "Protein: " + day.getProtein() + " g\n" + "Carbs: " + day.getCarbs() + " g\n" + "Fats: " + day.getFats() + " g", fontContent);
            dayInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(dayInfo);
            Integer[] j = {1};
            day.getFinalMeals().forEach(meal -> {
                Paragraph mealTitle = new Paragraph("\n" + j[0]++ + ". " + meal.getMeal().getName(), fontDayAndMeal);
                document.add(mealTitle);

                Paragraph mealInfo = new Paragraph("Calories: " + meal.getCalories() + " kcal\n" + "Protein: " + meal.getProtein() + " g\n" + "Carbs: " + meal.getCarbs() + " g\n" + "Fats: " + meal.getFats() + " g\n", fontContent);
                document.add(mealInfo);

                Paragraph ingredientSectionTitle = new Paragraph("\nIngredients:", fontContentBold);
                document.add(ingredientSectionTitle);

                meal.getFinalIngredients().forEach(ingredient -> {
                    Paragraph ingredientInfo = new Paragraph(ingredient.getIngredient().getName() + " " + ingredient.getWeight() + " g", fontContent);
                    document.add(ingredientInfo);
                });

                Paragraph recipeSectionTitle = new Paragraph("\nRecipe:", fontContentBold);
                document.add(recipeSectionTitle);
                Paragraph mealRecipe = new Paragraph(meal.getMeal().getMealExtention().getRecipe(), fontContent);
                document.add(mealRecipe);
            });
        });

        document.close();

        return fileName;
    }

    public String generateGroceriesPDF(List<Grocery> groceries) throws FileNotFoundException {
        Document document = new Document();
        String fileName = "Groceries_" + UUID.randomUUID() + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(ApplicationConstants.PATH_TO_PDF_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName));

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD);
        Font fontContentBold = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

        document.open();

        Paragraph title = new Paragraph("This is your shopping list!", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        groceries.forEach(grocery -> {
            Paragraph groceryTitle = new Paragraph(grocery.getName() + " " + grocery.getWeight() + " g", fontContentBold);
            document.add(groceryTitle);
        });

        document.close();

        return fileName;
    }
}
