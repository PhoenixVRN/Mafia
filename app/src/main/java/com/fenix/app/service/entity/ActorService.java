package com.fenix.app.service.entity;

import android.util.Log;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.dto.geo.GeoPointDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.JsonUtil;
import com.fenix.app.util.ThreadUtil;
import com.google.android.gms.maps.model.LatLng;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;

import java.util.List;

import lombok.var;

public class ActorService extends EntitySeriveBase<ActorDto> {

    private static final String COLLECTION_NAME = "actors";
    private static final String KEY_FIELD = "email";

    @Override
    protected void initEntityClass() {
        super.entityClass = ActorDto.class;
        super.entityKeyField = KEY_FIELD;
    }

    public ActorService(MongoService service) {
        super(service, COLLECTION_NAME);
    }

    /**
     * Найти участников по позиции и расстоянию
     * @param location  - позиция
     * @param distance  - максимальное расстоние от позиции до участника
     * @return          - списко найденных
     */
    public List<ActorDto> findByGeoPoint(LatLng location, double distance) {
/*
    {
        geoPoint: {
            $near: {
                $geometry: {
                    type: "Point",
                    coordinates: [51.5866325, 38.9970941]
                },
                $minDistance: 0,
                $maxDistance: 400
            }
        }
    }
*/
        var point = new Point(new Position(location.latitude, location.longitude));
        return super.list(Filters.near("geoPoint", point, distance, 0d));
    }

    /**
     * Изменить позицию
     *  @param entity   - сущность
     * @param location  - новая позиция
     * @return          - обновлённая сущность из БД
     */
    public ActorDto updateLocation(ActorDto entity, LatLng location) {
        var json = JsonUtil.Serialize(entity);
        var doc = Document.parse(json);

        var jsonLocation = JsonUtil.Serialize(location);
        var docLocation = Document.parse(jsonLocation);

        var geoPoint = new GeoPointDto(location);
        var jsonGeoPoint = JsonUtil.Serialize(geoPoint);
        var docGeoPoint = Document.parse(jsonGeoPoint);
/*
    { $set:
        {
            location: {
                latitude: 51.586045,
                longitude: 39.0010533
            },
            geoPoint: {
                type: "Point",
                coordinates: [51.586045, 39.0010533]
            }
        }
    }
*/
        var filter = Filters.eq(entityKeyField, doc.get(entityKeyField));
        var update = Updates.combine(
                Updates.set("location", docLocation),
                Updates.set("geoPoint", docGeoPoint)
        );
        var result = collection.updateOne(filter, update);
        if (result.getModifiedCount() > 0)
            return super.read(filter);

        throw new RuntimeException("updateLocation: ActorDto with " + entityKeyField + " = " + doc.get(entityKeyField) + " not found!");
    }

    /**
     * Ударить
     *
     * @param alien
     */
    public ActorDto hit(ActorDto my, ActorDto alien) {
        //Сила удара
        int acc = my.getPerson().getWeaponHeadLeft().getPhysicalDamage();
/*
        int alienHP = alien.getPerson().getHp();
        int result = alienHP - acc;
        alien.getPerson().setHp(result);
*/
        // Строю фильтр участника
        var json = JsonUtil.Serialize(alien);
        var doc = Document.parse(json);
        var filter = Filters.eq(entityKeyField, doc.get(entityKeyField));

        // Строю update для поля person.hp
        var update = Updates.inc("person.hp", -acc);

        // Делаю update и получаю результат из БД
        var result = collection.updateOne(filter, update);
        if (result.getModifiedCount() > 0)
            return super.read(filter); // Отдаю успешно обновленной dto участника

        // Если дошел до сюда, значит в БД нет такого участника
        throw new RuntimeException("hit: ActorDto with " + entityKeyField + " = " + doc.get(entityKeyField) + " not found!");
    }
}
