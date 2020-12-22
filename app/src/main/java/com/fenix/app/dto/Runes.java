package com.fenix.app.dto;
import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor



public class Runes {
    public String nameRunes; // название руны
    public String descriptionRunes; // описание руны
    private int levelRunes; //уровень руны, чисто для визуализации цвета.

    private String descriptionMutableRunes; // Описание изменений
    private String mutableVariableRunes; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVarRunes; // значение для изменения переменной характеристик.

    private String descriptionMutableRunes2; // Описание изменений
    private String mutableVariableRunes2; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVarRunes2; // значение для изменения переменной характеристик.

    private String descriptionMutableRunes3; // Описание изменений
    private String mutableVariableRunes3; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVarRunes3; // значение для изменения переменной характеристик.
}
