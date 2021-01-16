package com.fenix.app.dto;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonDto {
    private String namePerson;
    private String idPerson;
    private Enum classPerson;
    private String rang;
    private String bio;

    private int hp = 1000; // Текущее количество Хитпоинтов
    private int maxhp = 1000; // Максимальное количество Хитпоинтов
    private int streng = 100; // а если задонатите на карту 6762 8013 9005 0249 00  я накину ещё Хилсов)
    private int agility = 100;
    private int mana = 100;


    private int AllPhDefenc = 10;
    private int AllMagefenc = 10;
    private int RegHp = 5;    //  регенирация Хитпоинтов в 1 сек

        private LatLng location = null;

    /**
     * Голова
     */
    private ArmorDto armorHead = new ArmorDto();

    /**
     * Тело
     */
    private ArmorDto armorTorso = new ArmorDto();

    /**
     * Ноги
     */
    private ArmorDto armorLegs = new ArmorDto();

    /**
     * Обувь
     */
    private ArmorDto armorBoots = new ArmorDto();

    /**
     * Перчатки
     */
    private ArmorDto armorGloves = new ArmorDto();

    /**
     * Оружие в левой руке
     */
    private WeaponDto weaponHeadLeft = new WeaponDto();

    /**
     * Оружие в левой руке
     */
    private WeaponDto weaponHeadRight = new WeaponDto();


    /**
     * Мешок со ВСЕЙ хернёй
     */
    private List<Object> bag = new ArrayList<>();

}
