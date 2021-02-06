package com.fenix.app.service.entity;

import com.fenix.app.dto.ItemBox;
import com.fenix.app.dto.geo.GeoPointDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.JsonUtil;
import com.google.android.gms.maps.model.LatLng;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import lombok.var;

public class ItemService extends EntitySeriveBase<ItemBox> {

    private static final String COLLECTION_NAME = "items";
    private static final String KEY_FIELD = "itemID";

    @Override
    protected void initEntityClass() {
        super.entityClass = ItemBox.class;
        super.entityKeyField = KEY_FIELD;
    }

    public ItemService(MongoService service) {
        super(service, COLLECTION_NAME);
    }

    /**
     * Найти участников по позиции и расстоянию
     *
     * @param location - позиция
     * @param distance - максимальное расстоние от позиции до участника
     * @return - списко найденных
     */
    public List<ItemBox> findByGeoPoint(LatLng location, double distance) {
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
     *
     * @param entity   - сущность
     * @param location - новая позиция
     * @return - обновлённая сущность из БД
     */
    public ItemBox updateLocation(ItemBox entity, LatLng location) {
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

        throw new RuntimeException("updateLocation: ItemBox with " + entityKeyField + " = " + doc.get(entityKeyField) + " not found!");
    }

    /**
     * Очистить ящик
     */
    public ItemBox clear(ItemBox box) {

        // Строю фильтр ящика
        var filter = super.getOneFilter(box);

        // Строю update для всех нборов
        var update = Updates.combine(
                Updates.set("weaponItems", new ArrayList<>()),
                Updates.set("armorItems", new ArrayList<>()),
                Updates.set("objectsItems", new ArrayList<>())
        );

        // Делаю update и получаю результат из БД
        var result = collection.updateOne(filter, update);
        if (result.getModifiedCount() > 0)
            return super.read(filter); // Отдаю успешно обновленный dto

        // Если дошел до сюда, значит в БД нет такого участника
        throw new RuntimeException("clear: ItemBox with filter=" + filter.toString() + " not found!");
    }
}
