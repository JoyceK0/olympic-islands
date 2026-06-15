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

    // This class manages the physics world and all the entities, bodies, creation, assignment, and more. It extends
    // the iterating system for faster access to required entities and implements entity listener which provides
    // us with a few key functions of entities in the box 2D collision and world physic engine.

    private final World world; // get the physic world
    private final float interval; // fixed time step value to prevent physics errors, used for physics step call (ensures consistent timing for physics calculations)
    private float accumulator; // sums up the variable delta times over frames, and if greater than a set value, then the physics world step function is called (see below)

    // Constructor
    public PhysicsSystem(World world, float interval) {
        super(Family.all(Physic.class, Transform.class).get()); // grab all entites with the physic class and the transform class (movable and physics applies)
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
    public void entityAdded(Entity entity) { // nothing much to do when the entity is added, internally handled
    }

    @Override
    public void entityRemoved(Entity entity) {
        // when removed, remove from physics engine
        Physic physic = Physic.MAPPER.get(entity); // get physic component of the entity
        if(physic!=null) {
            this.world.destroyBody(physic.getBody()); // destroy the physic component and the body in the physic world
        }
        // downside to removing in this way is that no dynamic adding and removing entities is possible, since it gets completely destroyed when removed
        // does not work if the Physic component gets removed from an entity because the component is no longer accessible here through the physic mapper
        // ONLY works when an entity with a Physic component gets removed ENTIRELY from the engine
    }

    @Override // normally update() only updates entities in the Ashley system, but we also want to manage delta time and the accumulator for the physics system
    public void update(float deltaTime) {

        this.accumulator += deltaTime; // sums up delta time over the game frames

        // when the accumulator value is higher than the interval, then perform the physics calculations and increase the frame in the physics world
        // this helps ensure that physics calculations are performed at regular intervals to maintain processing speed and prevent glitching in the physic world
        while(this.accumulator >= this.interval) {
            this.accumulator -= this.interval; // remove the interval from the accumulator showing that the physics world has incremented one step
            super.update(interval); // run the classic Ashley update to update the entities using the physic time interval
            this.world.step(interval, 6, 2);
            // The world.step is the physics calculations part where we pass in fixed timestamp, velocity iterations and position iterations.
            // The iterations tell the physics engine how many times it must perform calculations for velocity and positioning of the body.
            // 6 and 2 are ideal and standard for the iterations.
        }
        world.clearForces(); // after doing the physics in the correct time interval, clear forces for next interval, auto clear is set as false to prevent interval and timing errors in GdxGame

        // INTERPOLATION
        //Down here comes the interpolation. Since we use a fixed interval timing for physics calculations,
        // sometimes with higher or lower frame rates your object can glitch around the map due to the
        // interval being different from the refresh rate of the game itself. To fix this, interpolation calculates the
        // difference between the physics timing and the game timing, and smoothens object motion by
        // blending new and old positions, therefore creating an illusion of a smooth and continuous
        // entity trajectory and interaction. (kind of how human brains interpolate movements)

        float alpha = this.accumulator / this.interval; // find the difference between physics and game times, the closer to 0 alpha is the more in sync the times are, and the closer to 1 it is, the more unsynced the times are
        for(int i = 0; i < getEntities().size(); ++i) {
            this.interpolateEntity(getEntities().get(i), alpha); // interpolate (method below), alpha being the difference as stated above
        }

    }


    // process the entity and its attributes
    @Override
    protected void processEntity(Entity entity, float deltaTime) { // for all entities with physics entity
        Physic physic = Physic.MAPPER.get(entity); // get all entities with the physic component
        physic.getPrevPosition().set(physic.getBody().getPosition()); // set prev position as current position
    }

    // interpolate the entity's movement
    private void interpolateEntity(Entity entity, float alpha) {

        Transform transform = Transform.MAPPER.get(entity); // get all entities with the transform component
        Physic physic = Physic.MAPPER.get(entity); // get all entities with the physic component

        transform.getPosition().set( // automatically changes the transform component for physic objects according to linear interpolation
            //lerp is the linear interpolation function, takes prev value, final value, and the progress or alpha/change value
            MathUtils.lerp(physic.getPrevPosition().x, physic.getBody().getPosition().x, alpha),
            MathUtils.lerp(physic.getPrevPosition().y, physic.getBody().getPosition().y, alpha)
        );
    }
}
