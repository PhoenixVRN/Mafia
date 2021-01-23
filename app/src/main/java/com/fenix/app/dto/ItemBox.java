package com.fenix.app.dto;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.var;

@Data
@NoArgsConstructor
public class ItemBox extends MapItemBase {

    private String itemID;

    private String dropTime; // Дата появления итема

    private List<WeaponDto> weaponItems = new ArrayList<>();
    private List<ArmorDto> armorItems = new ArrayList<>();
    private List<Object> objectsItems = new ArrayList<>();

    @Override
    public String getID() {
        return itemID;
    }

    /**
     * Complete set state
     */
    @Override
    public void set(MapItemBase item) {
        var dto = (ItemBox)item;

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

}
