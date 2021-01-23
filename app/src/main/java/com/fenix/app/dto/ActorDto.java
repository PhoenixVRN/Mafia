package com.fenix.app.dto;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.var;

@Data
@NoArgsConstructor
public class ActorDto extends MapItemBase {

    @NotNull
    private String email, pass, phone;

    private String lastAccessTime; // Дата последнего обращения

    private PersonDto person = null;

    @Override
    public String getID() {
        return email;
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
    @Override
    public void set(MapItemBase item) {
        var dto = (ActorDto)item;

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
