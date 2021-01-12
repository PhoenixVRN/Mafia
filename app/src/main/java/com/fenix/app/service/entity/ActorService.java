package com.fenix.app.service.entity;

import android.util.Log;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.ThreadUtil;
import com.google.android.gms.maps.model.LatLng;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

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
     * Ударить
     * @param alien
     */
    public void hit(ActorDto my, ActorDto alien) {
        //TODO Тут логика удара
        int alienHP = alien.getPerson().getHp();
        int acc = my.getPerson().getWeaponHeadLeft().getPhysicalDamage();
        int result = alienHP - acc;
        alien.getPerson().setHp(result);
        ThreadUtil.Do(()->this.save(alien)).error(ex->{
            Log.e("actor sevis",ex.toString());
        });
    }
}
