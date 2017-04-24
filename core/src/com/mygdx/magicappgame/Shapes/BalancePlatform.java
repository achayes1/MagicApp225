package com.mygdx.magicappgame.Shapes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.magicappgame.MyGdxGame;

/**
 * Created by Ansel Colby on 2/22/17.
 * This Class contains all of the parts that make up the moving platform
 */

public class BalancePlatform extends Sprite{

    private World world;
    public Body bod1; // The platform
    public Body bod2; // The Body the platform rotates around
    private Vector2[] vectorList;

    private Sprite platSprite;

    private static final float WIDTH = 80;
    private static final float HEIGHT = 10;

    /**
     * Initializes the world, creates the triangle's points, and calls
     * the other functions to set up the platform, triangle, and joint.
     * @param world is the world passed from PlayScreen
     */
    public BalancePlatform(World world){
        this.world = world;
        definePlatform(MyGdxGame.V_WIDTH / 2, 40);
        definePivot((MyGdxGame.V_WIDTH / 2), 25, 12, 5);
    }

    /**
     * Creates the platform
     * @param x coordinate for the platform's position on screen
     * @param y coordinate for the platform's position on screen
     * @return the Body of the platform
     */
    private Body definePlatform(float x, float y){
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.DynamicBody;
        def1.position.set(x, y);
        bod1 = world.createBody(def1);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH, HEIGHT);
        FixtureDef fdef = new FixtureDef();
        fdef.friction = .2f;
        fdef.shape = shape;
        fdef.density = 1.0f;
        bod1.createFixture(fdef);


        return bod1;
    }

    /**
     * Creates the triangle that the platform pivots around
     * @param x coordinate of the pivot's position on screen
     * @param y coordinate of the pivot's position on screen
     */
    private void definePivot(float x, float y, float width, float height) {
        BodyDef def2 = new BodyDef();
        def2.type = BodyDef.BodyType.StaticBody;
        def2.position.set(x, y);

        bod2 = world.createBody(def2);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);
        bod2.createFixture(shape, 1.0f);

    }

    public void hit(){
        System.out.println("I've been hit");
    }

    public void render(Batch batch) {
        platSprite.setPosition(bod1.getPosition().x - (HEIGHT/2), bod1.getPosition().y - (HEIGHT/2));
        platSprite.setRotation((float)Math.toDegrees(bod1.getAngle()));
        platSprite.setOriginCenter();
        platSprite.draw(batch);
    }

    public float getWidth(){
        return WIDTH;
    }

    public float getHeight(){
        return HEIGHT;
    }

}