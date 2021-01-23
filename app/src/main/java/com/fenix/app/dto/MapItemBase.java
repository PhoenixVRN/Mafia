package com.fenix.app.dto;

import com.fenix.app.dto.geo.GeoPointDto;
import com.google.android.gms.maps.model.LatLng;

/**
 * Implementation GeoJSON
 */
public abstract class MapItemBase {

    String name = null;
    LatLng location = null;
    GeoPointDto geoPoint = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    /**
     * Implementation GeoJSON
     */
    public void setLocation(LatLng location) {
        this.location = location;
        this.geoPoint = new GeoPointDto(location);
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract String getID();

    @Override
    public boolean equals(Object anObject) {
        if (anObject == null)
            return false;

        if (anObject instanceof MapItemBase) {
            MapItemBase anotherItem = (MapItemBase) anObject;

            if (this.getID() == anotherItem.getID())
                return true;

            if (this.getID() != null && this.getID().equals(anotherItem.getID()))
                return true;
        }

        return false;
    }

    public abstract void set(MapItemBase item);

}
