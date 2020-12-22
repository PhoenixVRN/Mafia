package com.fenix.app.dto;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor


public class Person {
    private String namePerson;
    private String idPerson;
    private String classPerson;
    private String rang;
    private String bio;

    private int streng;
    private int agility;
    private int mana;

    private LatLng location = null;

    Armor armorHead = new Armor();
    Armor armorTorso = new Armor();
    Armor armorLegs = new Armor();
    Armor armorBoots = new Armor();
    Armor armorGloves = new Armor();

    Weapon weaponHeadLeft = new Weapon();
    Weapon weaponHeadRight = new Weapon();





}
