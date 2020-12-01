package com.fenix.app.dto;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ActorDto {

    @NotNull

    private String  id, name, email, pass, phone;;
    private LatLng location;
    private int hp, endurance, moral;
    private int tipClass; // 1- вампир, 2- оборотень, 3 человек.

    public void setSetClass(int i) {
        this.tipClass = i;
    }


    //   public ActorDto(String john, Object o) {
  //  }

//    @Override
 //   public String toString() {
 //       return name;
//    }

 //   public void setName(String toString) {
//    }
}
