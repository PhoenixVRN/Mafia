package com.fenix.app.service.entity;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.dto.geo.GeoPointDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.DateUtil;
import com.fenix.app.util.JsonUtil;
import com.google.android.gms.maps.model.LatLng;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import lombok.var;

@RequiresApi(api = Build.VERSION_CODES.O)
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
     *
     * @param location - позиция
     * @param distance - максимальное расстоние от позиции до участника
     * @return - списко найденных
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
        var filter = Filters.and(
                Filters.near("geoPoint", point, distance, 0d),
                Filters.gte("lastAccessTime", DateUtil.toISO(
                        DateUtil.addMinutes(new Date(), -2)
                ))
        );
        return super.list(filter);
    }

    /**
     * Изменить позицию
     *
     * @param entity   - сущность
     * @param location - новая позиция
     * @return - обновлённая сущность из БД
     */
    public ActorDto updateLocation(ActorDto entity, LatLng location) {
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
        var filter = super.getOneFilter(entity);
        var update = Updates.combine(
                Updates.set("location", docLocation),
                Updates.set("geoPoint", docGeoPoint)
        );
        var result = collection.updateOne(filter, update);
        if (result.getModifiedCount() > 0)
            return super.read(filter);

        throw new RuntimeException("updateLocation: ActorDto with filter=" + filter.toString() + " not found!");
    }

    /**
     * Изменить позицию
     *
     * @param entity - сущность
     * @return - обновлённая сущность из БД
     */
    public ActorDto updateAccessTime(ActorDto entity) {
/*
    { $set:
        {
            lastAccessTime: 51586045
        }
    }
*/
        var filter = super.getOneFilter(entity);
        var update = Updates.set("lastAccessTime", DateUtil.toISO(new Date()));
        var result = collection.updateOne(filter, update);
        if (result.getModifiedCount() > 0)
            return super.read(filter);

        throw new RuntimeException("updateAccessTime: ActorDto with filter=" + filter.toString() + " not found!");
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
        var filter = super.getOneFilter(alien);

        // Строю update для поля person.hp
        var update = Updates.inc("person.hp", -acc);

        // Делаю update и получаю результат из БД
        var result = collection.updateOne(filter, update);
        if (result.getModifiedCount() > 0)
            return super.read(filter); // Отдаю успешно обновленной dto участника

        // Если дошел до сюда, значит в БД нет такого участника
        throw new RuntimeException("hit: ActorDto with filter=" + filter.toString() + " not found!");
    }
}
