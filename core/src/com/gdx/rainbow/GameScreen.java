package com.gdx.rainbow;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.gdx.rainbow.objects.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gdx.rainbow.objects.Object;
import com.gdx.rainbow.particles.*;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.ArrayList;

/**
 * Created by WAM on 9/5/2016.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor {

    MyGdxGame game;

    OrthographicCamera guiCam;
    Viewport viewport;

    public static float UNIT_WIDTH = MyGdxGame.WIDTH/256f;
    public static float UNIT_HEIGHT = MyGdxGame.HEIGHT/256f;

    public static float ELAPSED_TIME = 0;

    public World world;
    public ArrayList<Object> objects, removedObjects;
    public ArrayList<Cloud> sunClouds;
    Player player;
    Sunbeam sunBeam;
    ParticleSystem rainParticles;

    float nextBeamLocation = 0;
    float beamTimer = 0;
    float nextBeamTime = 5;

    float cloudTimer = 0;
    float nextCloudTime = 5;

    Vector2 mouseLocation;
    private boolean inputHeld = false;
    private Cloud focusedCloud = null;

    public static float CLOUD_WIN_TIME = 10;

    public final float PLAYER_PUSH_RANGE = 1.5f;

    //Temporary Shape Renderer
    ShapeRenderer sr;

    public GameScreen(MyGdxGame game) {
        //http://gamedev.stackexchange.com/questions/93816/libgdx-box2d-and-screen-viewport-scaling
        this.game = game;
        Gdx.input.setInputProcessor(this);
        mouseLocation = new Vector2();
        guiCam = new OrthographicCamera();
        viewport = new FillViewport(UNIT_WIDTH, UNIT_HEIGHT, guiCam);

        viewport.apply();

        guiCam.position.set(0, 0, 0);

        //guiCam.zoom = 2;

        //UNIT_WIDTH *= guiCam.zoom;
        //UNIT_HEIGHT *= guiCam.zoom;

        world = new World(new Vector2(0, 0), true);

        objects = new ArrayList<Object>();
        removedObjects = new ArrayList<Object>();
        sunClouds = new ArrayList<Cloud>();

        player = (Player) Object.createObject(Object.PLAYER);
        player.set(this, 0, 0);

        sunBeam = new Sunbeam();

        sr = new ShapeRenderer();

        rainParticles = new ParticleSystem(.01f);

        Cloud c = (Cloud) Object.createObject(Object.CLOUD);
        c.set(this, 0, 0, new Vector2(0, 0));
    }

    public void spawnCloud() {

        float x = 0;
        float y = 0;

        Vector2 initialVel = new Vector2(0, 0);

        boolean left = MathUtils.randomBoolean();

        if (left) {
            x = -GameScreen.UNIT_WIDTH/2;
            initialVel.x = 1f;
        }
        if (!left) {
            x = GameScreen.UNIT_WIDTH/2;
            initialVel.x = -1f;
        }

        y = MathUtils.random(-GameScreen.UNIT_HEIGHT/2, GameScreen.UNIT_HEIGHT/2);

        Cloud c = (Cloud) Object.createObject(Object.CLOUD);
        c.set(this, x, y, initialVel);
    }


    public void update(float delta) {
        world.step(delta, 6, 2);
        GameScreen.ELAPSED_TIME += delta;
        if (inputHeld && focusedCloud == null) this.movePlayerTowards(mouseLocation);
        if (focusedCloud != null) this.pushCloudAwayFromPlayer(focusedCloud);
        if (focusedCloud != null) if (player.body.getPosition().dst(focusedCloud.body.getPosition()) > PLAYER_PUSH_RANGE) focusedCloud = null;
        if (focusedCloud != null) if (!focusedCloud.body.getFixtureList().first().testPoint(mouseLocation)) focusedCloud = null;
        this.handleSunbeam(delta);
        this.handleClouds(delta);

        //nextBeamLocation = player.body.getPosition().x;
        //System.out.println("Objects: " + objects.size() + " Objects Removed This Frame: " + removedObjects.size());
        //rainParticles.update(delta);

        for (Object o: objects) {
            applyDragForce(o);

            if (o instanceof Cloud) {

                ((Cloud) o).justSpawned -= delta;
                if (((Cloud) o).justSpawned < 0) ((Cloud) o).justSpawned = 0;

                if (((Cloud) o).justSpawned == 0) {
                    float adjst = 1.55f;
                    if (o.body.getPosition().x + Cloud.WIDTH  * adjst * Assets.CLOUD_IMAGE_SCALE/ 2 < -GameScreen.UNIT_WIDTH / 2 || o.body.getPosition().x - Cloud.WIDTH * adjst * Assets.CLOUD_IMAGE_SCALE/ 2 > GameScreen.UNIT_WIDTH / 2 ||
                            o.body.getPosition().y + Cloud.HEIGHT * adjst * Assets.CLOUD_IMAGE_SCALE/ 2 < -GameScreen.UNIT_HEIGHT / 2 || o.body.getPosition().y - Cloud.HEIGHT * adjst * Assets.CLOUD_IMAGE_SCALE/ 2 > GameScreen.UNIT_HEIGHT / 2) {
                        removedObjects.add(o);
                    }
                }

                if (sunClouds.contains(o)) {
                    //Multiply delta * sunClouds.size ??? this will make the timer go up faster for each cloud in sunbeam
                    //System.out.println(sunClouds.size() + " timer: " + ((Cloud) o).sunTimer);
                    ((Cloud) o).sunTimer += (delta);
                    if (((Cloud) o).sunTimer >= GameScreen.CLOUD_WIN_TIME) {
                        ((Cloud) o).sunTimer = GameScreen.CLOUD_WIN_TIME;
                    }
                }

                if (sunBeam.contains(o.body) && !sunClouds.contains(o)) {
                    sunClouds.add((Cloud) o);
                }
                else if (!sunBeam.contains(o.body)){
                    if (sunClouds.contains(o)) {
                        ((Cloud) o).sunTimer = 0;
                        sunClouds.remove(o);
                    }
                }
            }


        }

        for (Object o: removedObjects) {
            if (objects.contains(o)) {
                objects.remove(o);
            }
        }

        removedObjects.clear();

    }

    private void movePlayerTowards(Vector2 endLocation) {
        Vector2 force = new Vector2(0, 0);
        Vector2 startLocation = player.body.getPosition();
        //vector between desination and player;
        force.set(endLocation.x - startLocation.x, endLocation.y - startLocation.y);
        force.nor();
        //force.scl(.030f);
        force.scl(.015f);
        //player.body.applyForce(force, player.body.getPosition(), true);
        player.body.applyLinearImpulse(force, player.body.getPosition(), true);
        //System.out.println(player.body.getLinearVelocity().len());
    }

    private void pushCloudAwayFromPlayer(Cloud c) {
        Vector2 force = new Vector2(0, 0);
        Vector2 startLocation = c.body.getPosition();
        Vector2 endLocation = player.body.getPosition();
        //vector between desination and player;
        float dx = endLocation.x - startLocation.x;
        float dy = endLocation.y - startLocation.y;
        float xDir = 1;
        float yDir = 1;
        if (dx < 0) xDir = -1;
        if (dy < 0) yDir = -1;
        if (Math.abs(dx) < .1f) dx = .1f * xDir;
        if (Math.abs(dy) < .1f) dy = .1f * yDir;
        force.set(1/dx, 1/dy);
        System.out.println("Pushed with: " + 1/dx + ", " + 1/dy);
        force.scl(-.1f);
        c.body.applyForce(force, c.body.getPosition(), true);
    }

    private void handleSunbeam(float delta) {
        this.beamTimer += delta;
        if (beamTimer >= nextBeamTime) {
            beamTimer = 0;
            nextBeamLocation = MathUtils.random(-GameScreen.UNIT_WIDTH/2 + GameScreen.UNIT_WIDTH/4, GameScreen.UNIT_WIDTH/2 - GameScreen.UNIT_WIDTH/4);
            sunBeam.typeOfMovement = MathUtils.randomBoolean();
            //nextBeamLocation = MyGdxGame.WIDTH/2;
            //System.out.println(nextBeamLocation);
        }
        sunBeam.update(nextBeamLocation, delta);
    }

    private void handleClouds(float delta) {
        this.cloudTimer += delta;
        if (cloudTimer >= nextCloudTime) {
            cloudTimer = 0;
            //this.spawnCloud();
        }
    }

    private void applyDragForce(Object o) {
        Vector2 dragForce = new Vector2(0, 0);
        Vector2 v = o.body.getLinearVelocity();
        dragForce.set(-v.x, -v.y);
        //dragForce.scl(.32f);
        if (o instanceof Player) dragForce.scl(.16f);
        if (o instanceof Cloud) dragForce.scl(.10f);
        o.body.applyForce(dragForce, o.body.getPosition(), true);
    }

    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);

        game.batcher.begin();
        game.batcher.draw(Assets.background_image, -(UNIT_WIDTH)/2, -(UNIT_HEIGHT)/2, UNIT_WIDTH, UNIT_HEIGHT);
        float playerSclOff = .1f *(MathUtils.sin(GameScreen.ELAPSED_TIME * 1.25f));

        Assets.player_image.setScale(1 +  playerSclOff);
        for (Object o: objects) {
            if (o instanceof Player) Assets.player_image.draw(game.batcher, o.body);
            Assets.cloud_image.setAlpha(.8f);
            if (o instanceof Cloud) {
                if (o instanceof Cloud) ((Cloud) o).drawRainbow(game.batcher);
                float cloudYOff = .07f * (MathUtils.sin(GameScreen.ELAPSED_TIME  + o.timerOffset * .85f));
                Assets.cloud_image.draw(game.batcher, o.body.getPosition().x, o.body.getPosition().y + cloudYOff, Cloud.WIDTH * Assets.CLOUD_IMAGE_SCALE, Cloud.HEIGHT * Assets.CLOUD_IMAGE_SCALE, 0);
            }
            game.batcher.setColor(1, 1, 1, 1);
            Assets.cloud_image.setScale(Assets.CLOUD_IMAGE_SCALE + .05f *(MathUtils.sin((GameScreen.ELAPSED_TIME + o.timerOffset) * 1.25f)));
        }

        game.batcher.end();

        sr.setProjectionMatrix(guiCam.combined);

        sr.begin(ShapeRenderer.ShapeType.Line);
        sunBeam.draw(sr);
        sr.setColor(Color.BLACK);
        //sr.line(MyGdxGame.WIDTH/2 + player.body.getPosition().x, MyGdxGame.HEIGHT/2 + player.body.getPosition().y, MyGdxGame.WIDTH/2 + mouseLocation.x, MyGdxGame.HEIGHT/2 + mouseLocation.y);
        //box.setAsBox(.768f * .98f, .356f * .98f);
        sr.setColor(Color.ORANGE);

        /*
        for (Object o: objects) {
          if (o instanceof Cloud) {
              if (focusedCloud == o) sr.setColor(Color.GREEN);
              else sr.setColor(Color.ORANGE);
              float x = o.body.getPosition().x;
              float y = o.body.getPosition().y;
              float width =  Cloud.WIDTH * 1.25f;
              float height = Cloud.HEIGHT * 1.25f;
              sr.line(x - width/2, y - height/2, x - width/2, y + height/2);
              sr.line(x - width/2, y + height/2, x + width/2, y + height/2);
              sr.line(x + width/2, y + height/2, x + width/2, y - height/2);
              sr.line(x + width/2, y - height/2, x - width/2, y - height/2);
          }
        }
        */
        rainParticles.drawParticles(sr);
        sr.end();

        //box.setAsBox(256/5, 256/5);
       // sr.line(MyGdxGame.WIDTH/2 + player.body.getPosition().x - 256/20, MyGdxGame.HEIGHT/2 + player.body.getPosition().y - 256/20, MyGdxGame.WIDTH/2 + player.body.getPosition().x + 256/20, MyGdxGame.HEIGHT/2 + player.body.getPosition().y + 256/20);
        //for (Object o: objects) {
         //   if (o instanceof Cloud) {
                //sr.line(MyGdxGame.WIDTH/2 + o.body.getPosition().x - 768/16, MyGdxGame.HEIGHT/2 + o.body.getPosition().y - 356/16, MyGdxGame.WIDTH/2 + o.body.getPosition().x + 768/16, MyGdxGame.HEIGHT/2 + o.body.getPosition().y + 356/16);
         //   }
        //}
        game.batcher.begin();
        for (Cloud c: sunClouds) {
            c.drawTimer(game.batcher);
        }
        game.batcher.end();


    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void dispose() {
        world.dispose();
        sr.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (button == Input.Buttons.LEFT) {
            inputHeld = true;
            //mouseLocation.set((screenX - MyGdxGame.WIDTH/2)/GameScreen.UNIT_WIDTH, (-(screenY - MyGdxGame.HEIGHT/2)/GameScreen.UNIT_HEIGHT));
            //mouseLocation.set(((screenX - MyGdxGame.WIDTH/2) * (float)1/256), (-(screenY - MyGdxGame.HEIGHT/2)) * (float)1/256);
            System.out.println(mouseLocation);

            float pixX = (screenX - MyGdxGame.WIDTH/2);
            float pixY = (-(screenY - MyGdxGame.HEIGHT/2));
            mouseLocation.set(pixX/256.00f, pixY/256.00f);
        }

        for (Object o: objects) {
            if (o instanceof Cloud) {
                if (o.body.getPosition().dst(player.body.getPosition()) >= PLAYER_PUSH_RANGE) continue;
                if (o.body.getFixtureList().first().testPoint(mouseLocation)) {
                    System.out.println("touched!");
                    focusedCloud = (Cloud) o;
                }
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        inputHeld = false;
        focusedCloud = null;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        inputHeld = true;
        //mouseLocation.set(screenX - MyGdxGame.WIDTH/2, -(screenY - MyGdxGame.HEIGHT/2));
        //mouseLocation.set((screenX - MyGdxGame.WIDTH/2)/GameScreen.UNIT_WIDTH, (-(screenY - MyGdxGame.HEIGHT/2)/GameScreen.UNIT_HEIGHT));
        //mouseLocation.set(((screenX - MyGdxGame.WIDTH/2) * (float)1/256), (-(screenY - MyGdxGame.HEIGHT/2)) * (float)1/256);
        float pixX = (screenX - MyGdxGame.WIDTH/2);
        float pixY = (-(screenY - MyGdxGame.HEIGHT/2));
        mouseLocation.set(pixX/256.00f, pixY/256.00f);
        System.out.println(mouseLocation);

        for (Object o: objects) {
            if (o instanceof Cloud) {
                if (o.body.getPosition().dst(player.body.getPosition()) >= PLAYER_PUSH_RANGE) continue;
                if (o.body.getFixtureList().first().testPoint(mouseLocation)) {
                    System.out.println("touched!");
                    focusedCloud = (Cloud) o;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        float pixX = (screenX - MyGdxGame.WIDTH/2);
        float pixY = (-(screenY - MyGdxGame.HEIGHT/2));
        mouseLocation.set(pixX/256.00f, pixY/256.00f);

        for (Object o: objects) {
            if (o instanceof Cloud) {
                if (o.body.getFixtureList().first().testPoint(mouseLocation)) {

                }
            }
        }

        return false;}

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
