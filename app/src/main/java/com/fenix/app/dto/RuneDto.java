package com.fenix.app.dto;
import com.fenix.app.data.GenInf;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor



public class RuneDto {
    GenInf genInf = new GenInf(); // название, уровень, описание

    private String descriptionMutableRune; // Описание изменений
    private String mutableVariableRune; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVarRune; // значение для изменения переменной характеристик.

    private String descriptionMutableRune2; // Описание изменений
    private String mutableVariableRune2; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVarRune2; // значение для изменения переменной характеристик.

    private String descriptionMutableRune3; // Описание изменений
    private String mutableVariableRune3; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVarRune3; // значение для изменения переменной характеристик.
}
