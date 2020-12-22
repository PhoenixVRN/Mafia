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

    private int streng;
    private int agility;
    private int mana;

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
