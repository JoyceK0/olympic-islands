package com.github.JoyceK0.Olympic_Islands.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.JoyceK0.Olympic_Islands.asset.AtlasAsset;
import com.github.JoyceK0.Olympic_Islands.component.Facing.FacingDirection;

public class Animation2D implements Component {
    public static final ComponentMapper<Animation2D> MAPPER = ComponentMapper.getFor(Animation2D.class);

    private final AtlasAsset atlasAsset; // needs access to AtlasAsset to get correct animation frames for entities
    private final String atlasKey;
    private AnimationType type;
    private FacingDirection direction; // current direction of animation being played
    private PlayMode playMode; // how the animation is being played. ex: normal, reversed, loop, etc.
    private float speed; // how fast the animation is played
    private float stateTime; // helps with calculating current frame of an animation
    private Animation<TextureRegion> animation;
    private boolean dirty; // whenever an attribute that impacts the animation changes, dirty is set to true to let the animation system know the animation needs to be recalculated

    public Animation2D(AtlasAsset atlasAsset,
                       String atlasKey,
                       AnimationType type,
                       PlayMode playMode,
                       float speed) {
        this.atlasAsset = atlasAsset;
        this.atlasKey = atlasKey;
        this.type = type;
        this.direction = null;
        this.playMode = playMode;
        this.speed = speed;
        this.stateTime = 0f;
        this.animation = null;
    }

    public void setAnimation(Animation<TextureRegion> animation, FacingDirection direction) {
        this.animation = animation;
        this.direction = direction;
        this.stateTime = 0f;
        this.dirty = false;
    }

    public FacingDirection getDirection() {
        return direction;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public AtlasAsset getAtlasAsset() {
        return atlasAsset;
    }

    public String getAtlasKey() {
        return atlasKey;
    }

    public void setType(AnimationType type) {
        this.type = type;
        this.dirty = true;
    }

    public AnimationType getType() {
        return type;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public boolean isDirty() {
        return dirty;
    }

    public  boolean isFinished() {
        return animation.isAnimationFinished(stateTime); // when at last frame of the animation, isAnimationFinished will return true
    }

    public float incAndGetStateTime(float deltaTime) { // helper method
        this.stateTime += deltaTime * speed;
        return this.stateTime;
    }

    public enum AnimationType { // different animations available
        IDLE, WALK;

        private final String atlasKey;

        AnimationType() {
            this.atlasKey = name().toLowerCase();
        }

        public String getAtlasKey() {
            return atlasKey;
        }
    }
}
