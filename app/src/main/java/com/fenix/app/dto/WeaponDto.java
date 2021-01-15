package com.fenix.app.dto;

import com.fenix.app.data.GeneralInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor


public class WeaponDto extends GeneralInfo {

//    private String nameWeapon; // Название Оружия "Метла"
//    private int levelWeapon; //Уровень брони (обычная, клёвая, пиздатая, ахуенная) в зависимости будет название писатся разным цветом.
//    private String descriptionWeapon; // Описание оружия "Метла из говна и палок, ахуенна против пауков и алкашей"

    private String classificationWeaponStrings; // Классификация оружия "Метла поганая двухручная"
    private int classificationWeapon; // 1 - одноручное, 2 - двуручное,

    private int optimalDistance; //Оптимальная дистанция
    private int correktDistance;

    private int physicalDamage = 50; // Физический урон
    private int magicaDamage; // Магический урон
    private int rof;   // Кулдаун (откат) в сек.
    private int accuracy; // шанс что удар попадёт в цель в %
    private int armorPenetration; // бронепробитие
    private int critChance; // Шанс критической атаки в %.
    private int critValue; // Коэфициент критической атаки (+ к основному урону или в %)

    private String descriptionMutable; // Описание изменений
    private String mutableVariable; //Изменяемая переменная, например восстановления здоровья.
    private int valueMutVar; // значение для изменения переменной характеристик.

    private String descriptionMutable2; // Описание изменений
    private String mutableVariable2; //Изменяемая переменная, например восстановления койнить херни.
    private int valueMutVar2; // значение для изменения переменной характеристик.

    private String descriptionMutable3; // Описание изменений
    private String mutableVariable3; //Изменяемая переменная( например усиленный реген ХП)
    private int valueMutVar3; // значение для изменения переменной характеристик. (+100 ХП в мин)

    private int slotsRunes; //количество слотов под руны

    RuneDto runeDto = null;
    RuneDto runeDto2 = null;
    RuneDto runeDto3 = null;

}
