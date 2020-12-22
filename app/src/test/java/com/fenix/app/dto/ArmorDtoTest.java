package com.fenix.app.dto;

import org.junit.Test;

import lombok.var;

public class ArmorDtoTest {

    @Test
    public void ArmorDtoTest_Create_setName_to_Null() {
        System.out.println("test ArmorDtoTest_Create_setName_to_Null");

        var armor = new ArmorDto();

        try {
            armor.setNameArmor(null);
            throw new RuntimeException("bad NullPointerException Not Found");
        } catch (NullPointerException ex) {
            System.out.println("good NullPointerException");
        }

        System.out.println("ok");
    }

    @Test
    public void ArmorDtoTest_Create_setClassificationArmorStrings_to_Null() {
        System.out.println("test ArmorDtoTest_Create_setClassificationArmorStrings_to_Null");

        var armor = new ArmorDto();

        try {
            armor.setClassificationArmorStrings(null);
            throw new RuntimeException("bad NullPointerException Not Found");
        } catch (NullPointerException ex) {
            System.out.println("good NullPointerException");
        }

        System.out.println("ok");
    }

}
