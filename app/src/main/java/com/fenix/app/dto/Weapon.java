package com.fenix.app.dto;
import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor


public class Weapon {
    private String nameWeapon; // Название Оружия "Метла"
    private String classificationWeaponStrings; // Классификация оружия "Метла поганая двухручная"
    private int levelWeapon; //Уровень брони (обычная, клёвая, пиздатая, ахуенная) в зависимости будет название писатся разным цветом.
    private String descriptionWeapon; // Описание оружия "Метла из говна и палок, ахуенна против пауков и алкашей"
    private int classificationWeapon; // 1 - одноручное, 2 - двуручное,

    private int optimalDistance; //Оптимальная дистанция
    private int correktDistance;

    private int physicalDamage;
    private int magicaDamage;
    private int rof;
    private int accuracy;
    private int armorPenetration;
    private int critChance;
    private int critValue;

    private String descriptionMutable; // Описание изменений
    private String mutableVariable; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVar; // значение для изменения переменной характеристик.

    private String descriptionMutable2; // Описание изменений
    private String mutableVariable2; //Изменяемая переменная, например восстановления койнить херни.
    private int valueMutVar2; // значение для изменения переменной характеристик.

    private String descriptionMutable3; // Описание изменений
    private String mutableVariable3; //Изменяемая переменная, например понижения потенции.
    private int valueMutVar3; // значение для изменения переменной характеристик.

    private int slotsRunes; //количество слотов под руны

    Runes runes = new Runes();
    Runes runes2 = new Runes();
    Runes runes3 = new Runes();

}
