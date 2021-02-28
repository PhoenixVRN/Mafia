package com.fenix.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComObject {
    private String nameObject; // название объекта
    private String idObject; // хз зачем но пусть будет, вдруг пригодится
    private Enum classObject; // специализация, кафе , магазин и пр. хня
    private String bio; // в двух словах о обьекте

    private String owner; // кто владелец
    private String level; // уровень развития
    private String legality; // уровень легальности
    private String roof; // кто крышует
    private int quantityStaff; // количество возможного персонала
    private int income; // доход в час
    private int cash; // наличка


    private Security securityObject = new Security(); // количество и качество охраны у объекта
    private Staff staff = new Staff(); // количество и качество охраны у объекта

}
