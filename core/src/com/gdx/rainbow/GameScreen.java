package com.gdx.rainbow;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.gdx.rainbow.objects.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gdx.rainbow.objects.Object;
import com.gdx.rainbow.particles.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;


import java.util.ArrayList;

/**
 * Created by WAM on 9/5/2016.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor {

    MyGdxGame game;

    OrthographicCamera guiCam;
    Viewport viewport;

    public static boolean TESTING = false;

    public static float UNIT_WIDTH = MyGdxGame.WIDTH/256f;
    public static float UNIT_HEIGHT = MyGdxGame.HEIGHT/256f;

    public static float ELAPSED_TIME = 0;

    public World world;
    public ArrayList<Object> objects, removedObjects;
    public ArrayList<Cloud> sunClouds;
    Player player;
    Sunbeam sunBeam;
    ParticleSystem rainParticles;

    float newBeamLocation = 0;

    float beamTimer = 0;
    float nextBeamTime = 5;

    float cloudTimer = 0;
    float nextCloudTime = 5;

    float windTimer = 0;
    float windTime = 3;

    float loseTimer = 0;
    float wonTimer = 0;

    Vector2 mouseLocation;
    float playerAngleToFocusedCloudInDegrees = 0;
    float playerRotation = 0;
    private boolean inputHeld = false;
    private Cloud focusedCloud = null;
    private Cloud winCloud = null;

    public static float WIN_TIME = 10;
    public static float LOSE_TIME = 10 * 5;


    public static float RAINBOW_DRAW_TIME = 2;
    public static float WON_TIME = RAINBOW_DRAW_TIME + 5;

    public final float PLAYER_PUSH_RANGE = 1.5f;

    public Vector2 windForce;

    //Temporary Shape Renderer
    ShapeRenderer sr;

    public GameScreen(MyGdxGame game) {

        this.game = game;
        sr = new ShapeRenderer();
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
        rainParticles = new ParticleSystem(.001f, 2);
        objects = new ArrayList<Object>();
        removedObjects = new ArrayList<Object>();
        sunClouds = new ArrayList<Cloud>();

        windForce = new Vector2();

        if (GameScreen.TESTING) System.out.println("TESTING MODE ENABLED");

        startLevel();


        Cloud c = (Cloud) Object.createObject(Object.CLOUD);
        c.set(this, 0, 0, new Vector2(0, 0));
    }

    public void update(float delta) {
        world.step(delta, 6, 2);
        //System.out.println("Objects: " + objects.size() + " Objects Removed This Frame: " + removedObjects.size());
        //System.out.println("SunClouds: " + sunClouds.size());

        this.handleTimers(delta);
        this.handlePlayerInput(delta);

        rainParticles.update(delta);
        sunBeam.update(newBeamLocation, delta);

        this.handlePlayerAnimations();

        for (Object o: objects) {
            applyDragForce(o);
            if (o instanceof Cloud) {
                Cloud c = ((Cloud) o);
                this.handleCloud(c, delta);
            }
        }


        this.handleRemovedObjects();

    }

    private void handleTimers(float delta) {
        GameScreen.ELAPSED_TIME += delta;
        loseTimer += delta;

        windTimer += delta;
        if (windTimer > windTime) {
            pickNewWindForce();
            windTimer = 0;
        }

        if (wonTimer >= WON_TIME) {
            startLevel();
        }

        if (loseTimer >= GameScreen.LOSE_TIME) {
            this.loseLevel(delta);
        }

        this.cloudTimer += delta;
        if (cloudTimer >= nextCloudTime) {
            cloudTimer = 0;
            this.spawnCloud();
        }

        this.beamTimer += delta;
        if (beamTimer >= nextBeamTime) {
            beamTimer = 0;
            sunBeam.nextXDest = MathUtils.random(-GameScreen.UNIT_WIDTH/2 + GameScreen.UNIT_WIDTH/4, GameScreen.UNIT_WIDTH/2 - GameScreen.UNIT_WIDTH/4);
            if (sunBeam.readyForNextLocation()) newBeamLocation = sunBeam.nextXDest;
            sunBeam.typeOfMovement = MathUtils.randomBoolean();
        }

    }

    private void handleCloud(Cloud c, float delta) {

        c.resetPushedTimer(delta);
        c.handleJustSpawnedTimer(delta);

        if (!GameScreen.TESTING) c.body.applyForce(windForce.x, windForce.y, c.body.getPosition().x, c.body.getPosition().y, false);

        boolean shouldBeRemoved = !c.justSpawned() && c.offScreen() && (c != winCloud);

        if (shouldBeRemoved)  removedObjects.add(c);

        //Increments timer of clouds withing beam and checks for win condition
        if (sunClouds.contains(c)) {
            //Multiply delta * sunClouds.size ??? this will make the timer go up faster for each cloud in sunbeam
                c.handleSunTimer(delta);
                if (c.getWinPercent() == 1) this.winLevel(c, delta);
        }

        //Keeps a list of clouds that were in the sunbeam
        if (sunBeam.contains(c.body) && !sunClouds.contains(c)) {
            sunClouds.add(c);
        }

        //If cloud was contained by sunbeam but is no longer, then reset its suntimer
        else if (!sunBeam.contains(c.body)){
            if (sunClouds.contains(c)) {
                if (c != winCloud) {
                    c.sunTimer = 0;
                    sunClouds.remove(c);
                }
            }
        }


    }

    private void winLevel(Cloud c, float delta) {
        if (wonTimer >= WON_TIME) return;
        wonTimer += delta;
        if (winCloud == null) winCloud = c;
    }

    private void loseLevel(float delta) {

    }

    private void startLevel() {
        wonTimer = 0;
        loseTimer = 0;
        beamTimer = 0;
        cloudTimer = 0;
        windTimer = 0;
        windForce.set(0, 0);
        winCloud = null;
        focusedCloud = null;
        sunBeam = new Sunbeam();
        removedObjects.addAll(objects);
        objects.clear();
        sunClouds.clear();
        Assets.player_win_animation.setTime(0);
        player = (Player) Object.createObject(Object.PLAYER);
        player.set(this, 0, 0);
    }

    private void movePlayerTowards(Vector2 endLocation) {
        Vector2 force = new Vector2(0, 0);
        Vector2 startLocation = player.body.getPosition();
        //vector between desination and player;
        force.set(endLocation.x - startLocation.x, endLocation.y - startLocation.y);
        force.nor();
        //force.scl(.030f);
        //force.scl(.015f);
        force.scl(.020f);
        //force.scl(0);
        //player.body.applyForce(force, player.body.getPosition(), true);
        player.body.applyLinearImpulse(force, player.body.getPosition(), true);
        //System.out.println(player.body.getLinearVelocity().len());
    }

    private void applyDragForce(Object o) {
        Vector2 dragForce = new Vector2(0, 0);
        Vector2 v = o.body.getLinearVelocity();
        dragForce.set(-v.x, -v.y);
        //dragForce.scl(.32f);
        if (o instanceof Player) dragForce.scl(.35f);
        if (o instanceof Cloud) dragForce.scl(.10f);
        o.body.applyForce(dragForce, o.body.getPosition(), true);
    }

    private void spawnCloud() {

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
        if (Math.abs(dx) < .4f) dx = .4f * xDir;
        if (Math.abs(dy) < .4f) dy = .4f * yDir;
        force.set(1/dx, 1/dy);
        //System.out.println("Pushed with: " + 1/dx + ", " + 1/dy);
        force.scl(-.2f);
        //temp testing below
        force.scl(.4f);
        force.scl(1, .5f);
        c.body.applyForce(force, c.body.getPosition(), true);
    }

    private void pickNewWindForce() {
        float xDir = 1;
        float yDir = 1;
        if (MathUtils.randomBoolean()) xDir = -1;
        if (MathUtils.randomBoolean()) yDir = -1;

        float x = MathUtils.random(0, 1f);
        float y = MathUtils.random(0, 1f);
        windForce.set(x * xDir, y * yDir);
        float minForce = 0;
        float maxForce = .1f;
        windForce.scl(MathUtils.random(minForce, maxForce));
    }

    private void handlePlayerInput(float delta) {
        //If not blowing then move player towards tapped location
        if (inputHeld && focusedCloud == null) this.movePlayerTowards(mouseLocation);
        if (focusedCloud != null) {
            //Cloud gets bigger fastest at the start to compensate for light taps
            focusedCloud.pushedTimer += .65f*delta;
            focusedCloud.pushedTimer += delta * .05f * (1/focusedCloud.pushedTimer);
            if (focusedCloud.pushedTimer >= Cloud.PUSH_TIME) focusedCloud.pushedTimer = Cloud.PUSH_TIME;
            //

            this.pushCloudAwayFromPlayer(focusedCloud);

            // player stops pushing if cloud is out of range or no longer tap held
            if (player.body.getPosition().dst(focusedCloud.body.getPosition()) > PLAYER_PUSH_RANGE) focusedCloud = null;
            else if (!focusedCloud.body.getFixtureList().first().testPoint(mouseLocation)) focusedCloud = null;
        }

        //Tilt player towards direction of any tap within Threshold
        if (inputHeld) {
            Vector2 m = mouseLocation;
            Vector2 p = player.body.getPosition();
            Vector2 v = new Vector2(p.x - m.x, p.y - m.y);
            v.nor();
            playerAngleToFocusedCloudInDegrees = (180 / MathUtils.PI) * (float) Math.acos(v.dot(0, 1)) - 90;
            if (mouseLocation.x < player.body.getPosition().x) playerAngleToFocusedCloudInDegrees = -playerAngleToFocusedCloudInDegrees;
            //System.out.println(playerAngleToFocusedCloudInDegrees);
        }

        float targetAngle = 0;

        boolean angleLessThan45 = Math.abs(mouseLocation.x - player.body.getPosition().x) > Math.abs(mouseLocation.y - player.body.getPosition().y);
        if (inputHeld && angleLessThan45 || (focusedCloud != null)) targetAngle = playerAngleToFocusedCloudInDegrees;

        playerRotation +=  1.4f * (targetAngle - playerRotation) * delta;

    }

    private void handlePlayerAnimations() {
        //player animations
        if (focusedCloud != null) {
            if (focusedCloud.body.getPosition().x > player.body.getPosition().x) player.setSprite(Assets.player_blowing_animation);
            else {
                player.setSprite(Assets.player_blowing_animation_flipped);
            }
            //if (focusedCloud.body.getPosition().x < player.body.getPosition().x) player.getSprite().setFlip(true, false);
        }

        else if (wonTimer / WON_TIME >= .3f) {
            player.setSprite(Assets.player_win_animation);
        }

        else {
            if (player.getSprite() instanceof AnimatedBox2DSprite) {
                AnimatedBox2DSprite a = ((AnimatedBox2DSprite) player.getSprite());
                if (a.isAnimationFinished()) player.setSprite(Assets.player_image);
            }

            else {
                player.setSprite(Assets.player_image);
                //Ensure next animation beginds on first frame
                //Perhaps store last animation used and set its time to zero to be more general
                Assets.player_blowing_animation.setTime(0);
                Assets.player_blowing_animation_flipped.setTime(0);
            }
        }

        //End of player animations
    }

    private void handleRemovedObjects() {
        for (Object o: removedObjects) {
            if (objects.contains(o)) {
                objects.remove(o);
            }
        }

        removedObjects.clear();
    }


    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);

        game.batcher.begin();
        game.batcher.draw(Assets.background_image, -(UNIT_WIDTH)/2, -(UNIT_HEIGHT)/2, UNIT_WIDTH, UNIT_HEIGHT);
        float playerSclOff = .1f *(MathUtils.sin(GameScreen.ELAPSED_TIME * 1.25f));

        //repeat animation
        if (player.getSprite() instanceof AnimatedBox2DSprite) {
            AnimatedBox2DSprite a = ((AnimatedBox2DSprite) player.getSprite());
            if (a == Assets.player_blowing_animation || a == Assets.player_blowing_animation_flipped) {
                //start animation loop on 2nd frame
                if (focusedCloud != null) if (a.isAnimationFinished()) a.setTime(.20f * 1);
            }
        }

        player.getSprite().setScale(1 +  playerSclOff);
        player.getSprite().setRotation(playerRotation);
        for (Object o: objects) {
            if (o instanceof Player) player.getSprite().draw(game.batcher, o.body);
            Assets.cloud_image.setAlpha(Assets.CLOUD_ALPHA);
            if (o instanceof Cloud) {
                float pushedTimer = ((Cloud) o).pushedTimer;
                float scale = Assets.CLOUD_IMAGE_SCALE;
                if (pushedTimer > 0) {
                    scale += pushedTimer;
                    float alpha = Assets.CLOUD_ALPHA - pushedTimer;
                    if (scale > 2f) scale = 2f;
                    if (alpha < .4f) alpha = .4f;
                    o.getSprite().setAlpha(alpha);
                }
                o.getSprite().setScale(scale + (.14f * MathUtils.sin(GameScreen.ELAPSED_TIME + o.timerOffset)));
                if (o == winCloud) winCloud.drawRainbow(game.batcher, (wonTimer/RAINBOW_DRAW_TIME));
                float cloudYOff = .07f * (MathUtils.sin(GameScreen.ELAPSED_TIME  + o.timerOffset * .85f));
                Assets.cloud_image.draw(game.batcher, o.body.getPosition().x, o.body.getPosition().y + cloudYOff, Cloud.WIDTH * Assets.CLOUD_IMAGE_SCALE, Cloud.HEIGHT * Assets.CLOUD_IMAGE_SCALE, 0);
                Assets.cloud_image.setScale(Assets.CLOUD_IMAGE_SCALE);
                Assets.cloud_image.setAlpha(Assets.CLOUD_ALPHA);
            }
            game.batcher.setColor(1, 1, 1, 1);
        }


        sunBeam.draw(game.batcher);
        game.batcher.end();

        sr.setProjectionMatrix(guiCam.combined);

        sr.begin(ShapeRenderer.ShapeType.Line);
        //sunBeam.drawBounds(sr);
        sr.setColor(Color.BLACK);
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
        //rainParticles.drawParticlesTwo(sr);
        sr.end();


        game.batcher.begin();
        rainParticles.drawParticles(game.batcher);

        //storm cloud that displayslose timer
        /*
        for (int i = 0; i < 20; i++) {
            game.batcher.setColor(.5f, .5f, .5f, .8f - (.025f * i));
            float dir = 1;
            if (i % 2 == 0) dir = -1;
            float width = .4f * 4;
            float height = .4f * 2;
            game.batcher.draw(Assets.storm_cloud_particle, 0 + .1f * dir * i, GameScreen.UNIT_HEIGHT/2 - (height/2), width, height);
            game.batcher.setColor(1, 1, 1, 1);
        }
        */

        for (Cloud c: sunClouds) {
            c.drawTimer(game.batcher, sunBeam.nextXDest);
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
            //System.out.println(mouseLocation);

            float pixX = (screenX - MyGdxGame.WIDTH/2);
            float pixY = (-(screenY - MyGdxGame.HEIGHT/2));
            mouseLocation.set(pixX/256.00f, pixY/256.00f);
        }

        for (Object o: objects) {
            if (o instanceof Cloud) {
                if (o.body.getPosition().dst(player.body.getPosition()) >= PLAYER_PUSH_RANGE) continue;
                if (o.body.getFixtureList().first().testPoint(mouseLocation)) {
                    //System.out.println("touched!");
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
                    //System.out.println("touched!");
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

        //nextBeamLocation = mouseLocation.x;

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
