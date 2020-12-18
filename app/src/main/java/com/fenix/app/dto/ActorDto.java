package com.fenix.app.dto;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActorDto {

    @NotNull
    private String  name, email, pass, phone;

    private LatLng location = null;

    @Override
    public String toString() {
        return name;
    }


}
