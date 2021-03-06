package com.mygdx.magicappgame.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.magicappgame.MyGdxGame;

/**
 * Created by ahayes on 4/16/17.
 */

public class Level4 extends Level{

    public Level4(MyGdxGame game) {
        super(game);
        initializeCoord();
    }

    /**
     * Sets up the heights and widths for the Bodies in
     * this level.
     */
    private void initializeCoord(){

        levelCoord.add(new Vector2(10, 10));
        levelCoord.add(new Vector2(20, 20));
        levelCoord.add(new Vector2(10, 30));
        levelCoord.add(new Vector2(100, 10));
        levelCoord.add(new Vector2(20, 20));
        levelCoord.add(new Vector2(20, 20));
        levelCoord.add(new Vector2(10, 50));
        levelCoord.add(new Vector2(10, 50));
        levelCoord.add(new Vector2(50, 10));
        levelCoord.add(new Vector2(50, 10));
    }
}
