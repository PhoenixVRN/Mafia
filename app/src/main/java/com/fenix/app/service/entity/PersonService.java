package com.fenix.app.service.entity;

import com.fenix.app.dto.PersonDto;
import com.fenix.app.service.MongoService;

public class PersonService extends EntityServiceBase<PersonDto> {

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
