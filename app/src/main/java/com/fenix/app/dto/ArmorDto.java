package com.fenix.app.dto;

import com.fenix.app.data.GeneralInfo;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArmorDto extends GeneralInfo {

    @NotNull
    private String classificationArmorStrings; // Классификация брони "лёгкая броня"
//    private int levelArmor; //Уровень брони (обычная, клёвая, пиздатая, ахуенная) в зависимости будет название писатся разным цветом.

    //    private String descriptionArmor; // Описание брони "Броня из кожи девственниц отлично подходит для похода в Ашан"
    private int classificationArmor; // 1 - Легкая, 2 - средняя, 3 тяжёлая
    private int affiliationArmor; // 1 - голова, 2 - грудь, 3 ноги, 4- руки, 5 - обувь.

    private int physicalResist; // Физический резист. Влияет на принимаемый урон.
    private int magicalResist; // Магический резист. Влияет на принимаемый магический урон.
    private int adjustmentSTR; // Корректировка статов Силы
    private int adjustmentAGIL;// Корректировка статов Ловкости
    private int adjustmentMANA;// Корректировка статов Маны

    private String descriptionMutable1; // Описание изменений
    private String mutableVariable1; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVar1; // значение для изменения переменной характеристик.

    private String descriptionMutable2; // Описание изменений
    private String mutableVariable2; //Изменяемая переменная, например восстановления койнить херни.
    private int valueMutVar2; // значение для изменения переменной характеристик.

    private String descriptionMutable3; // Описание изменений
    private String mutableVariable3; //Изменяемая переменная, например понижения потенции.
    private int valueMutVar3; // значение для изменения переменной характеристик.

    private int slotsRunes; //количество слотов под руны

    RuneDto rune1 = new RuneDto();
    RuneDto rune2 = new RuneDto();
    RuneDto rune3 = new RuneDto();


}
