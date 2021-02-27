package com.fenix.app.dto;
import com.google.android.gms.maps.model.LatLng;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonDto {
    private LatLng location = null;// позиция на карте
    private String namePerson; // имя персонажа
    private String idPerson; // хз зачем но пусть будет, вдруг пригодится
    private Enum classPerson; // специализация, бандюк, мент или комерс
    private String bio; // о себе пару слов

    private int rang; // авторитет 1-100
    private int cash; // деньги
    private Security security = null; // количество и качество охраны у перса

//    private int hp = 1000; // Текущее количество Хитпоинтов
//    private int maxhp = 1000; // Максимальное количество Хитпоинтов
//    private int streng = 100; // а если задонатите на карту 6762 8013 9005 0249 00  я накину ещё Хилсов)
//    private int agility = 100;
//    private int mana = 100;


//    private int AllPhDefenc = 10;
//    private int AllMagefenc = 10;
//    private int RegHp = 5;    //  регенирация Хитпоинтов в 1 сек


        
}
