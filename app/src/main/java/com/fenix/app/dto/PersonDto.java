package com.fenix.app.dto;
import com.google.android.gms.maps.model.LatLng;

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

    private ArmorDto armorDtoHead = new ArmorDto();
    private ArmorDto armorDtoTorso = new ArmorDto();
    private ArmorDto armorDtoLegs = new ArmorDto();
    private ArmorDto armorDtoBoots = new ArmorDto();
    private ArmorDto armorDtoGloves = new ArmorDto();

    private Weapon weaponHeadLeft = new Weapon();
    private Weapon weaponHeadRight = new Weapon();





}
