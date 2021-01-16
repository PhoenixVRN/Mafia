package com.fenix.app.dto;

import com.fenix.app.dto.geo.GeoPointDto;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActorDto {

    @NotNull
    private String name, email, pass, phone;

    private LatLng location = null;
    private GeoPointDto geoPoint; // Implementation GeoJSON

    private long lastAccessTime; // Дата последнего обращения

    private PersonDto person = null;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject == null)
            return false;

        if (anObject instanceof ActorDto) {
            ActorDto anotherActor = (ActorDto) anObject;

            if (this.getEmail() == anotherActor.getEmail())
                return true;

            if (this.getEmail() != null && this.getEmail().equals(anotherActor.getEmail()))
                return true;
        }

        return false;
    }

    /**
     * Complete set state
     */
    public void set(ActorDto dto) {
        // Local properties
        this.setName(dto.getName());
        this.setPass(dto.getPass());
        this.setPhone(dto.getPhone());

        // location
        this.setLocation(dto.getLocation());

        // Person profile
        this.setPerson(dto.getPerson());
    }

    /**
     * Implementation GeoJSON
     */
    public void setLocation(LatLng location) {
        this.location = location;
        this.geoPoint = new GeoPointDto(location);
    }

}
