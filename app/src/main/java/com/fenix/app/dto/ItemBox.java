package com.fenix.app.dto;
import com.fenix.app.dto.geo.GeoPointDto;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemBox {
    private String itemID;
    private String name;

    private LatLng location = null;
    private GeoPointDto geoPoint; // Implementation GeoJSON

    private String dropTime; // Дата появления итема

    private List<WeaponDto> weaponItems = new ArrayList<>();
    private List<ArmorDto> armorItems = new ArrayList<>();
    private List<Object> objectsItems = new ArrayList<>();


    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject == null)
            return false;

        if (anObject instanceof ItemBox) {
            ItemBox anotherItem = (ItemBox) anObject;

            if (this.getItemID() == anotherItem.getItemID())
                return true;

            if (this.getItemID() != null && this.getItemID().equals(anotherItem.getItemID()))
                return true;
        }

        return false;
    }

    /**
     * Complete set state
     */
    public void set(ItemBox dto) {
        // Local properties
        this.setName(dto.getName());
        this.setDropTime(dto.getDropTime());

        // location
        this.setLocation(dto.getLocation());

        // Items
        this.setWeaponItems(dto.getWeaponItems());
        this.setArmorItems(dto.getArmorItems());
        this.setObjectsItems(dto.getObjectsItems());
    }

    /**
     * Implementation GeoJSON
     */
    public void setLocation(LatLng location) {
        this.location = location;
        this.geoPoint = new GeoPointDto(location);
    }
}
