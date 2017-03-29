package com.mygdx.magicappgame.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.magicappgame.MyGdxGame;
import com.mygdx.magicappgame.Scenes.Hud;
import com.mygdx.magicappgame.Shapes.BalancePlatform;
import com.mygdx.magicappgame.Tools.WorldContactListener;
import com.mygdx.magicappgame.levels.Level;
import com.mygdx.magicappgame.levels.Level1;
import com.mygdx.magicappgame.levels.Level2;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jiayin Qu on 2017/2/11.
 * This PlayScreen class is passed the Gdx game and is in control when the game is in "play."
 */
public class PlayScreen implements Screen{
    private MyGdxGame game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    private Stage stage;

    // Backgrounds
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private ShapeRenderer shapeRenderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Long lifeTime;
    private Long delay = 2000L;

    private BalancePlatform plat;
    private Vector2 screenPos;

    // The ArrayLists that contain Bodies and matching Sprites
    private ArrayList<Body> bodyList;
    private ArrayList<Sprite> squareTexList;

    // Level setups
    private ArrayList<Level> levels;
    private Level currentLevel;
    private Level1 level1;
    private Level2 level2;
    private int levelCount;

    private Boolean somethingOnScreen;

    /**
     * This constructor simply initializes everything.
     * @param game the main game
     */
    public PlayScreen(MyGdxGame game) {
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT, gamecam);
        hud = new Hud(game.batch);

        maploader = new TmxMapLoader();
        //map = maploader.load("SkyBackG.tmx");
        //renderer = new OrthogonalTiledMapRenderer(map);
        shapeRenderer = new ShapeRenderer();
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -40f), true); // The game's gravity
        b2dr = new Box2DDebugRenderer();

        world.setContactListener(new WorldContactListener());

        plat = new BalancePlatform(world);

        screenPos = new Vector2(104, gamePort.getWorldHeight());
        bodyList = new ArrayList<Body>();
        squareTexList = new ArrayList<Sprite>();

        // Setup all of the levels and the array of levels
        levelCount = 1;
        setUpLevels();

        somethingOnScreen = false;
        lifeTime = System.currentTimeMillis();

    }

    @Override
    public void show() {

    }

    /**
     * Handles all of the input for the game
     * @param dt delta time
     */
    private void handleInput(float dt){
        // Moves the camera up - just for testing
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            screenPos.add(0, 1.75f);
            gamecam.position.y += 100 * dt;
        }
        // Moves the camera down - just for testing
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            gamecam.position.y -= 100 * dt;
        }

        // Draws the next shape when 1 is pressed, if all the shapes have been
        // used it calls endGame();
        //if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            //squareTexList.add(drawSquareTex());&& objectContactListener.Contacted
            //if(!currentLevel.levelComplete){
                //somethingOnScreen = true;
                //bodyList.add(currentLevel.getNextBod());
                //hud.addScore(200);
            //}
        //}


        // Moves the current falling shape to the left
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)&& somethingOnScreen){
            Body moveBod = bodyList.get(bodyList.size()-1);
            moveBod.applyForce(new Vector2(-300000f, 0), moveBod.getWorldCenter(), true);
        }
        // Moves the current falling shape to the right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)&& somethingOnScreen){
            Body moveBod = bodyList.get(bodyList.size()-1);
            moveBod.applyForce(new Vector2(300000f, 0), moveBod.getWorldCenter(), true);
        }


        if (currentLevel.levelComplete && Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            currentLevel.clearLevel();
            levelCount++;
            hud.addLevel();
            currentLevel = levels.get(levelCount - 1);
            refreshBodies();
            game.setScreen(new MainMenu(game));
        }
    }



    /**
     * Removes the bodies from bodyList from the World,
     * clears bodyList,
     * updates the current level,
     * removes the bodies created by the BalancePlatform,
     * and makes a new BalancePlatform
     */
    private void refreshBodies() {
        for (Body body :bodyList) {
            world.destroyBody(body);
        }
        bodyList.clear();
        currentLevel = levels.get(levelCount-1);
        world.destroyBody(plat.bod1);
        world.destroyBody(plat.bod2);
        plat = new BalancePlatform(world);

    }

    /**
     * Updating the game and "stepping"
     * @param dt delta time
     */
    private void update(float dt){
        handleInput(dt);
        world.step(1/60f, 6, 2);

        //checkEndGame(); // Checks if the game is over

        gamecam.update();
        //renderer.setView(gamecam); // used for the background
        b2dr.render(world, gamecam.combined);

    }

    /**
     * Clears the screen and then draws everything again
     * @param delta delta time
     */
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //renderer.render();
        b2dr.render(world,gamecam.combined);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        lifeTime += (long) Gdx.graphics.getDeltaTime();
        if (lifeTime > delay) {
            if(!currentLevel.levelComplete){
                if(bodyList.size() == 0 || currentLevel.Contacted){
                    System.out.print(currentLevel.Contacted);
                    somethingOnScreen = true;
                    bodyList.add(currentLevel.getNextBod());
                    hud.addScore(200);
                    currentLevel.Contacted = false;
                    System.out.print(currentLevel.Contacted);
                }
            }
        }

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }


    /**
     * The function that ends the current game and brings up the exit screen
     */
    private boolean gameOver(){
        for(Body aBodyList : bodyList) {
            if (aBodyList.getWorldCenter().y < plat.bod2.getWorldCenter().y - 60)
                return true;
        }
        return false;
    }


    /**
     * Puts a Texture with a Sprite
     * @return the Sprite
     */
    public Sprite drawSquareTex(){
        Texture squareTex = new Texture("StoneSquare.png");
        Sprite squareSprite = new Sprite(squareTex);

        return squareSprite;
    }



    private void setUpLevels() {
        levels = new ArrayList<Level>();

        level1 = new Level1(world, screenPos);
        level2 = new Level2(world, screenPos);

        levels.add(level1);
        levels.add(level2);

        currentLevel = level1;
    }

    /**
     * Resizes the window
     * @param width of the screen
     * @param height of the screen
     */
    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
