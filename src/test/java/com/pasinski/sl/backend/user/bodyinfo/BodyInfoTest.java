package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import com.pasinski.sl.backend.user.bodyinfo.forms.Goals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BodyInfoTest {

    @ParameterizedTest
    @MethodSource("provideBodyInfoFormsForBee")
    void getBEE_isCalculatedCorrectly_true(BodyInfoForm bodyInfoForm, Double expectedBEE) {
        // 10w(kg) + 6.25h(cm) - 5a(years) + s
        // Given

        // When
        BodyInfo bodyInfo = new BodyInfo(bodyInfoForm, null);

        // Then
        assertEquals(expectedBEE, bodyInfo.getBEE());
    }

    @ParameterizedTest
    @MethodSource("provideBodyInfoFormsForTDEE")
    void getTDEE_isCalculatedCorrectly_true(BodyInfoForm bodyInfoForm, Double expectedBEE) {
        // BEE * PAL
        // Given

        // When
        BodyInfo bodyInfo = new BodyInfo(bodyInfoForm, null);

        // Then
        assertEquals(expectedBEE, bodyInfo.getTDEE());
    }


    @ParameterizedTest
    @MethodSource("provideBodyInfoFormsForCaloriesGoal")
    void getCaloriesGoal_isCalculatedCorrectly_true(BodyInfoForm bodyInfoForm, Integer expectedCaloriesGoal) {
        // TDEE * goalMultiplier + additional calories
        // Given

        // When
        BodyInfo bodyInfo = new BodyInfo(bodyInfoForm, null);

        // Then
        assertEquals(expectedCaloriesGoal, bodyInfo.getCaloriesGoal());
    }

    static Stream<Arguments> provideBodyInfoFormsForBee() {
        return Stream.of(
                Arguments.of(new BodyInfoForm(Goals.LOSE_WEIGHT, 171, 60, 24, Gender.MALE, 1.4F, 0), 1553.75),
                Arguments.of(new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 180,54,44,Gender.MALE,1.6F,0), 1450.0),
                Arguments.of(new BodyInfoForm(Goals.GAIN_WEIGHT, 143, 80, 32, Gender.MALE, 2.3F, 0), 1538.75),
                Arguments.of(new BodyInfoForm(Goals.LOSE_WEIGHT, 171, 60, 24, Gender.FEMALE, 1.4F, 0), 1387.75),
                Arguments.of(new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 180,54,44,Gender.FEMALE,1.6F,0), 1284.0),
                Arguments.of(new BodyInfoForm(Goals.GAIN_WEIGHT, 143, 80, 32, Gender.FEMALE, 2.3F, 0), 1372.75)
        );
    }

    static Stream<Arguments> provideBodyInfoFormsForTDEE() {
        return Stream.of(
                Arguments.of(new BodyInfoForm(Goals.LOSE_WEIGHT, 171, 60, 24, Gender.MALE, 1.4F, 0), 2175.25),
                Arguments.of(new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 180,54,44,Gender.MALE,1.6F,0), 2320.0),
                Arguments.of(new BodyInfoForm(Goals.GAIN_WEIGHT, 143, 80, 32, Gender.MALE, 2.3F, 0), 3539.125),
                Arguments.of(new BodyInfoForm(Goals.LOSE_WEIGHT, 171, 60, 24, Gender.FEMALE, 1.4F, 0), 1942.85),
                Arguments.of(new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 180,54,44,Gender.FEMALE,1.6F,0), 2054.4),
                Arguments.of(new BodyInfoForm(Goals.GAIN_WEIGHT, 143, 80, 32, Gender.FEMALE, 2.3F, 0), 3157.325)
        );
    }

    static Stream<Arguments> provideBodyInfoFormsForCaloriesGoal() {
        return Stream.of(
                Arguments.of(new BodyInfoForm(Goals.LOSE_WEIGHT, 171, 60, 24, Gender.MALE, 1.4F, 0), 1957),
                Arguments.of(new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 180,54,44,Gender.MALE,1.6F,123), 2443),
                Arguments.of(new BodyInfoForm(Goals.GAIN_WEIGHT, 143, 80, 32, Gender.MALE, 2.3F, -145), 3748),
                Arguments.of(new BodyInfoForm(Goals.LOSE_WEIGHT, 171, 60, 24, Gender.FEMALE, 1.4F, 0), 1748),
                Arguments.of(new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 180,54,44,Gender.FEMALE,1.6F,123), 2177),
                Arguments.of(new BodyInfoForm(Goals.GAIN_WEIGHT, 143, 80, 32, Gender.FEMALE, 2.3F, -145), 3328)
        );
    }
}