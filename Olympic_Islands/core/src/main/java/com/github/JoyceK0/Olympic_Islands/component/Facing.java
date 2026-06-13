package com.github.JoyceK0.Olympic_Islands.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Facing implements Component { // used for the direction objects are facing
    public static final ComponentMapper<Facing> MAPPER = ComponentMapper.getFor(Facing.class);

    private FacingDirection direction;

    public Facing(FacingDirection direction) {
        this.direction = direction;
    }

    public FacingDirection getDirection() {
        return direction;
    }

    public void setDirection(FacingDirection direction) {
        this.direction = direction;
    }

    public enum FacingDirection {
        UP, DOWN, LEFT, RIGHT;

        private final String atlasKey; // string used in texture atlas to find correct animation

        FacingDirection() {
            this.atlasKey = name().toLowerCase(); // all png names are in lowercase
        }

        public String getAtlasKey() {
            return atlasKey;
        }

    }
}
