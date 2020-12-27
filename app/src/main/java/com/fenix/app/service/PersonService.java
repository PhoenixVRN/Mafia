package com.fenix.app.service;

import com.fenix.app.dto.PersonDto;
import com.fenix.app.service.entity.EntitySeriveBase;
import com.mongodb.client.model.Filters;

public class PersonService extends EntitySeriveBase<PersonDto> {

    private static final String COLLECTION_NAME = "persons";
    private static final String KEY_FIELD = "namePerson";

    @Override
    protected void initEntityClass() {
        super.entityClass = PersonDto.class;
    }

    public PersonService(MongoService service) {
        super(service, COLLECTION_NAME);
    }

    public PersonDto loadByName(String personName) {
        return super.read(Filters.eq(KEY_FIELD, personName));
    }

    public void save(PersonDto dto) {
        super.update(dto, Filters.eq(KEY_FIELD, dto.getNamePerson()));
    }

    public void delete(PersonDto dto) {
        super.delete(Filters.eq(KEY_FIELD, dto.getNamePerson()));
    }
}
