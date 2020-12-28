package com.fenix.app.service.entity;

import com.fenix.app.dto.PersonDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.service.entity.EntitySeriveBase;
import com.mongodb.client.model.Filters;

import org.bson.conversions.Bson;

public class PersonService extends EntitySeriveBase<PersonDto> {

    private static final String COLLECTION_NAME = "persons";
    private static final String KEY_FIELD = "namePerson";

    @Override
    protected void initEntityClass() {
        super.entityClass = PersonDto.class;
        super.entityKeyField = KEY_FIELD;
    }

    public PersonService(MongoService service) {
        super(service, COLLECTION_NAME);
    }

}
