package com.fenix.app.dto;
import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor



public class RuneDto {
    public String nameRune; // название руны
    public String descriptionRune; // описание руны
    private int levelRune; //уровень руны, чисто для визуализации цвета.

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
