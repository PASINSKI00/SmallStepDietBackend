package com.pasinski.sl.backend.diet.PDFGenerator;

import com.pasinski.sl.backend.diet.DietRepository;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class PDFGeneratorServiceTest {
    private final PDFGeneratorService pdfGeneratorService = new PDFGeneratorService();
    @Test
    void generatePDF() throws FileNotFoundException {
        String fileName = pdfGeneratorService.generatePDF();
        assertNotNull(fileName);
    }
}