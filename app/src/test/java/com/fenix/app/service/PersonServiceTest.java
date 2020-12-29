package com.fenix.app.service;

import com.fenix.app.dto.PersonDto;
import com.fenix.app.service.entity.PersonService;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lombok.var;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersonServiceTest {

    MongoService mongoService = new MongoService("fenix");
    PersonService service = new PersonService(mongoService);

    @Test
    public void t001_InsertPerson() {
        System.out.println("test InsertPerson");

        var dto = new PersonDto();
        dto.setNamePerson("TestPersonName");

        service.save(dto);

        System.out.println("ok");
    }

    @Test
    public void t002_LoadPerson() {
        System.out.println("test LoadPerson");

        var dto = service.load("TestPersonName");

        System.out.println("ok " + dto.getNamePerson());
    }

    @Test
    public void t003_DeletePerson() {
        System.out.println("test DeletePerson");

        var dto = service.load("TestPersonName");

        service.delete(dto);

        System.out.println("ok");
    }

}
