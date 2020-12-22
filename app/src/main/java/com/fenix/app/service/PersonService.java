package com.fenix.app.service;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.dto.PersonDto;
import com.fenix.app.util.JsonUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import lombok.var;

public class PersonService {

    MongoService service;
    MongoCollection<Document> persons;

    public PersonService(MongoService service) {
        this.service = service;
        this.persons = service.getDocuments("persons");
    }

    public void save(PersonDto person) {
        var personJson = JsonUtil.Serialize(person);
        var personDoc = Document.parse(personJson);

        persons.insertOne(personDoc);
    }

    public PersonDto loadByName(String personName) {
        var doc = persons.find(Filters.eq("namePerson", personName)).limit(1).first();
        if(doc == null)
            return null;

        var personJson = doc.toJson();
        var personDto = JsonUtil.Parse(PersonDto.class, personJson);

        return personDto;
    }

    public void delete(PersonDto person) {
        persons.deleteOne(Filters.eq("namePerson", person.getNamePerson()));
    }
}
