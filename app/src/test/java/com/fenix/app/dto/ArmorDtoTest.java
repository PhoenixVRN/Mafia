package com.fenix.app.dto;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lombok.var;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArmorDtoTest {

    @Test
    public void t001_ArmorDtoTest_Create_setName_to_Null() {
        System.out.println("test ArmorDtoTest_Create_setName_to_Null");

        var armor = new ArmorDto();

        try {
            armor.setName(null);
            throw new RuntimeException("bad NullPointerException Not Found");
        } catch (NullPointerException ex) {
            System.out.println("good NullPointerException");
        }

        System.out.println("ok");
    }

    @Test
    public void t002_ArmorDtoTest_Create_setClassificationArmorStrings_to_Null() {
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
