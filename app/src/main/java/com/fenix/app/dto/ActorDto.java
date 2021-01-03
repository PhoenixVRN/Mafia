package com.fenix.app.dto;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActorDto {

    @NotNull
    private String  name, email, pass, phone;

    private LatLng location = null;

    private PersonDto person = null;

    @Override
    public String toString() {
        return name;
    }

    public void set(ActorDto dto){
        // Local properties
        this.setName(dto.getName());
        this.setPass(dto.getPass());
        this.setPhone(dto.getPhone());

        // location
        this.setLocation(dto.getLocation());

        // Person profile
        this.setPerson(dto.getPerson());
    }

}
