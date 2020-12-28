package com.fenix.app.service.entity;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.MongoService;

public class ActorService extends EntitySeriveBase<ActorDto> {

    private static final String COLLECTION_NAME = "actors";

    @Override
    protected void initEntityClass() {
        super.entityClass = ActorDto.class;
    }

    public ActorService(MongoService service) {
        super(service, COLLECTION_NAME);
    }

}
