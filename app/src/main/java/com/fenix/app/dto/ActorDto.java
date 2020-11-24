package com.fenix.app.dto;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActorDto {

    @NotNull
    private String name = "";
    private LatLng location;

    public ActorDto(String john, Object o) {
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String toString) {
    }
}
