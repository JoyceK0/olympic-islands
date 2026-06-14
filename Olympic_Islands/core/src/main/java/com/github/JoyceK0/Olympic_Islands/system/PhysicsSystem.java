package com.github.JoyceK0.Olympic_Islands.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.github.JoyceK0.Olympic_Islands.component.Physic;
import com.github.JoyceK0.Olympic_Islands.component.Transform;

public class PhysicsSystem extends IteratingSystem implements EntityListener {

    private  final World world;
    private final float interval; // fixed time step value to prevent physics errors, used for physics step call
    private float accumulator; // sums up the variable delta times per frames, and if greater than a set value, then the physics world step function is called

    public PhysicsSystem(World world, float interval) {
        super(Family.all(Physic.class, Transform.class).get());
        this.world = world;
        this.interval = interval;
        this.accumulator = 0f;
    }


    // added to and removed from engine help manage when an entity gets removed (for example it died) then you need to remove it from the physics world
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) { // nothing much to do when added
    }

    @Override
    public void entityRemoved(Entity entity) {
        // when removed, remove from physics engine
        Physic physic = Physic.MAPPER.get(entity);
        if(physic!=null) {
            this.world.destroyBody(physic.getBody());
        }
        // downside is no dynamic adding and removing entities/physical components, since it gets completely destroyed when removed
    }

    @Override // this only updates entities in the Ashley system, but we also want to manage delta time and the accumulator
    public void update(float deltaTime) {

        this.accumulator += deltaTime; // sums up delta time

        while(this.accumulator >= this.interval) {
            this.accumulator -= this.interval;
            super.update(deltaTime);
            this.world.step(interval, 6, 2); // pass in fixed timestamp, velocity iterations and position iterations. The iterations tell the physics engine how many times it must perform calculations for velocity and positioning of the body. 6 and 2 are ideal and standard
        }
        world.clearForces(); // after doing the physics in the correct time interval, clear forces for next interval, auto clear is set as false to prevent interval and timing errors in GdxGame

        //Down here comes the interpolation. Since we use a fixed interval timing for physics calculations,
        // sometimes with higher or lower frame rates your object can glitch around the map due to the
        // interval being different from the refresh rate. To fix this, interpolation calculates the
        // difference between the physics timing and the game timing, and smoothens object motion by
        // blending new and old positions, therefore creating an illusion of a smooth and continuous
        // entity trajectory and interaction. (kind of how human brains interpolate movements)

        float alpha = this.accumulator / this.interval; // find the difference between physics and game times, the closer to 0 alpha is the more in sync the times are, and the closer to 1 it is, the more unsynced the times are
        for(int i = 0; i < getEntities().size(); ++i) {
            this.interpolateEntity(getEntities().get(i), alpha);
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) { // for all entities with physics entity
        Physic physic = Physic.MAPPER.get(entity);
        physic.getPrevPosition().set(physic.getBody().getPosition()); // set prev position as current position

    }

    private void interpolateEntity(Entity entity, float alpha) {

        Transform transform = Transform.MAPPER.get(entity);
        Physic physic = Physic.MAPPER.get(entity);

        transform.getPosition().set( // automatically changes the transform component for physic objects according to linear interpolation
            //lerp is the linear interpolation function, takes prev value, final value, and the progress or alpha/change value
            MathUtils.lerp(physic.getPrevPosition().x, physic.getBody().getPosition().x, alpha),
            MathUtils.lerp(physic.getPrevPosition().y, physic.getBody().getPosition().y, alpha)
        );
    }
}
