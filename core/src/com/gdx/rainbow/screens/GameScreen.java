package com.gdx.rainbow.screens;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.gdx.rainbow.*;
import com.gdx.rainbow.screens.upgrade.stats.Stats;
import com.gdx.rainbow.objects.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gdx.rainbow.objects.Object;
import com.gdx.rainbow.particles.*;
import com.gdx.rainbow.screens.menus.utils.Button;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;


import java.util.ArrayList;

/**
 * Created by WAM on 9/5/2016.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor {

    MyGdxGame game;

    public static Selectable MODE = Selectable.MODE_DEFUALT;

    private Viewport viewport;
    private OrthographicCamera guiCam;

    Vector2 mouseLocation;

    private boolean inputHeld = false;

    public static float UNIT_WIDTH = MyGdxGame.WIDTH/256f;
    public static float UNIT_HEIGHT = MyGdxGame.HEIGHT/256f;

    public static float ELAPSED_TIME = 0;

    public Score score;

    public World world;

    private Player player;

    public Selectable SELECTED_CHARACTER;

    public ArrayList<Object> objects, removedObjects;
    private ArrayList<Cloud> sunClouds;

    private boolean rareCloudAlreadySpawnedThisLevel;
    private float chanceOfRareCloud;

    private Cloud focusedCloud = null;
    private Cloud winCloud = null;

    //Hidden cloud for clear skies mode that is never drawn and always is placed at center of beam
    //used entirely for timer purposes in new game mode
    private ClearSkiesCloud clearSkiesCloud;

    public Stats stats = null;
    private Stats prePowerUpStats = null;

    public int points;

    public int level;

    public Vector2 windForce;

    private ParticleSystem rainParticles;

    private int numOfRainParticlesPerFrame = 2;

    private ParticleSystem blowParticles;
    private ParticleSystem stormCloudParticles;
    private ParticleSystem lightningTrail;
    private ParticleSystem rainbowTrail;
    private Lightning lightning;

    private Sunbeam sunBeam;

    private float newBeamLocation = 0;
    private float beamTimer = 0;
    private float nextBeamTime = 5;

    private float cloudTimer = 0;
    private float nextCloudTime = 5;

    private float windTimer = 0;
    private float windTime = 3;
    private float maxWindForce;

    private float powerUpTimer = 0;
    public static float POWER_UP_TIME = 10;

    private float loseTimer = 0;
    public static float LOSE_TIME = 10 * 5;

    //timer which manages how long the player should get a drag force when returning from a powerup. Without a drag force the player goes flying from the change in accleration
    //but without a timer the drag force is applied all the time
    private float playerDragTimer = 0;

    private float playerAngleToFocusedCloudInDegrees = 0;
    private float playerRotation = 0;

    public static float WIN_TIME = 10;

    private float wonTimer = 0;
    public static float RAINBOW_DRAW_TIME = 2;
    public static float WON_TIME = .5f;//RAINBOW_DRAW_TIME + 5;

    private Button help_button;

    //Temporary Shape Renderer
    ShapeRenderer sr;

    public GameScreen(MyGdxGame game) {

        this.game = game;
        //sr = new ShapeRenderer();
        mouseLocation = new Vector2();
        guiCam = new OrthographicCamera();
        viewport = new FillViewport(UNIT_WIDTH, UNIT_HEIGHT, guiCam);

        viewport.apply();

        guiCam.position.set(0, 0, 0);

        //guiCam.zoom = 2;

        world = new World(Vector2.Zero, true);

        score = new Score();

        help_button = new Button(-GameScreen.UNIT_WIDTH/2 + .5f, -GameScreen.UNIT_HEIGHT/2 + .5f, .001f, Assets.help_button);

        this.createParticleSystems();

        objects = new ArrayList<Object>();
        removedObjects = new ArrayList<Object>();
        sunClouds = new ArrayList<Cloud>();

        windForce = new Vector2();

        if (MODE == Selectable.MODE_CLEAR_SKIES) {
            nextCloudTime = 2.0f;
            WIN_TIME *= 1.5f;
        }

        resetPlayThrough();
        startLevel();
    }

    private void createParticleSystems() {

        rainParticles = new ParticleSystem(ParticleSystemInfo.RAIN);
        blowParticles = new ParticleSystem(ParticleSystemInfo.BLOW);
        blowParticles.info.fadeIn = true;
        stormCloudParticles = new ParticleSystem(ParticleSystemInfo.STORM_CLOUDS);
        stormCloudParticles.info.xMin = (-4) * 1.3f;
        stormCloudParticles.info.xMax = (4) * 1;
        stormCloudParticles.info.initialVelocity = new Vector2(.125f * .5f, 0);
        stormCloudParticles.position.set(0, GameScreen.UNIT_HEIGHT/2);
        stormCloudParticles.nextParticleTimer = stormCloudParticles.info.nextParticleTime;

        lightningTrail = new ParticleSystem(ParticleSystemInfo.PLAYER_LIGHTNING_TRAIL);
        rainbowTrail = new ParticleSystem(ParticleSystemInfo.PLAYER_RAINBOW_TRAIL);

        lightning = new Lightning();

    }

    public void update(float delta) {
        world.step(delta, 6, 2);

        if (help_button.isPressed()) {
            game.set(game.helpScreen);
        }

        if (MODE != Selectable.MODE_CLEAR_SKIES) Assets.playMusic(Assets.rain_sound);

        if (MODE == Selectable.MODE_CLEAR_SKIES) {
            clearSkiesCloud.body.setTransform((sunBeam.xCenterTop + sunBeam.xCenterBottom) * .5f, 0, 0);
        }

        score.percentOfTimeLeft = (1f - loseTimer/GameScreen.LOSE_TIME);

        //YOU STILL HAVE TO CLEAN DRAW FUNCTION

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            Cloud c = (Cloud) Object.createObject(Object.CLOUD);
            c.set(this, 0, 0, new Vector2(0, 0));
            winLevel((Cloud) objects.get(1), delta * 30);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cloudTimer = nextCloudTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            powerUpTimer = GameScreen.POWER_UP_TIME;
        }


        this.handleTimers(delta);
        this.handlePlayerInput(delta);


        //Player powerup
        if (powerUpTimer > 0) {
            player.stats = Stats.END_STATS;
            playerDragTimer = 0;
        }
        if (powerUpTimer <= 0 && wonTimer <= 0) {
            player.stats = prePowerUpStats;

            playerDragTimer += delta;

            //ensures player doesnt fly off screen from such rapid change in forces
            if (playerDragTimer < .4f) {
                this.applyDragForce(player);
                this.applyDragForce(player);
                this.applyDragForce(player);
            }
        }

        this.handleParticles(delta);

        sunBeam.update(newBeamLocation, delta);

        this.handlePlayerAnimations();

        for (Object o: objects) {
            applyDragForce(o);
            if (o != player && o != winCloud) o.body.applyForce(windForce.x, windForce.y, o.body.getPosition().x, o.body.getPosition().y, false);
            if (o == player) o.body.applyForce(windForce.x * player.stats.windResist, windForce.y * player.stats.windResist, o.body.getPosition().x, o.body.getPosition().y, false);
            if (o instanceof Cloud) {
                Cloud c = ((Cloud) o);
                this.handleCloud(c, delta);
            }
        }


        this.handleRemovedObjects();

    }

    private void handleParticles(float delta) {

        if (MODE != Selectable.MODE_CLEAR_SKIES) {
            lightning.update(delta);
            rainParticles.update(delta, numOfRainParticlesPerFrame);
        }
        blowParticles.update(delta, 1);
        blowParticles.position = player.body.getPosition();

        if (Math.abs(powerUpTimer - GameScreen.POWER_UP_TIME) < .02f) {
            lightning.createLightning();
        }

        if (focusedCloud instanceof ClearSkiesCloud) focusedCloud = null;

        if (focusedCloud != null) {
            Vector2 blowVel = new Vector2();
            float x = focusedCloud.body.getPosition().x - player.body.getPosition().x;
            float y = focusedCloud.body.getPosition().y - player.body.getPosition().y;
            blowVel.set(x, y);

            blowVel.nor();
            float f = .4f + (.6f - .4f) * (player.stats.pushStrength/ Stats.PUSH_STRENGTH.endVal);
            float xDir = 1;
            float yDir = 1;
            if (blowVel.x < 0) xDir = -1;
            if (blowVel.y < 0) yDir = -1;

            blowVel.add(xDir * MathUtils.random(0, f), yDir * MathUtils.random(0, f));

            blowVel.scl(1f + .75f * (player.stats.pushRange/ Stats.PUSH_RANGE.endVal));
            Vector2 offset = blowVel.cpy().nor().scl(.1f);
            Vector2 accel = blowVel.cpy().nor().scl(-.002f);

            if (SELECTED_CHARACTER == Selectable.CHARACTER_STORM) {
                blowVel.scl(-1);
                offset.scl(30);
            }

            float cO = MathUtils.random(.86f, 1f);

            float cR = cO - .1f;
            if (cR < 0) cR = 0;
            float cG = cO;
            float cB = cO + .1f;
            if (cB > 1f) cB = 1;

            Color colorOffset = new Color(cR, cG, cB, .45f);

            float delay = .05f + (.0000001f - .05f) * (player.stats.pushStrength/ Stats.PUSH_STRENGTH.endVal);

            blowParticles.info.endSize = 3.3f * (1 + .5f * (player.stats.pushStrength/ Stats.PUSH_STRENGTH.endVal));
            blowParticles.createParticleWithDelay(delay, offset, colorOffset, blowVel, accel);

        }

        stormCloudParticles.update(delta, (int) (1));//(loseTimer/GameScreen.LOSE_TIME)));

        float cO = MathUtils.random(.75f * (1f - 2f*(loseTimer/GameScreen.LOSE_TIME)), 1f);

        float cB = cO + .01f;
        if (cB > 1) cB = 1f;

        //NOT FUNCTIONINNG CORRECTLY
        //Clouds fade away as you win in clear skies mode
        float m = 1;
        if (MODE == Selectable.MODE_CLEAR_SKIES) {
            m = (1f - (wonTimer*3f/WON_TIME));
            if (m < 0) m = 0;
            stormCloudParticles.info.color.a *= m;
        }


        stormCloudParticles.colorOffset = new Color(cO, cO, cB, .5f);

        //the particles for the bottom wall of clouds powerup
        int particlesToSpawnPerFrame = 0;
        if (powerUpTimer > 0) particlesToSpawnPerFrame = 1;

        rainbowTrail.update(delta, 0);
        rainbowTrail.position = player.body.getPosition();

        if (wonTimer > 0) {
            float angle = 180 - (180 / MathUtils.PI) * (float) Math.acos(player.body.getLinearVelocity().cpy().nor().dot(1, 0));
            if (player.body.getLinearVelocity().y < 0) angle = -angle;
            rainbowTrail.info.angleOffset = 270 - angle;
            rainbowTrail.createParticleWithDelay(.00001f, Vector2.Zero, Vector2.Zero, Vector2.Zero);

        }

        lightningTrail.update(delta, 0);


        if (SELECTED_CHARACTER == Selectable.CHARACTER_STORM) {
            float r1 = MathUtils.random(-1f, 1f);
            float r2 = MathUtils.random(-1f, 1f);
            float angle = 180 - (180 / MathUtils.PI) * (float) Math.acos(player.body.getLinearVelocity().cpy().add(r1, r2).nor().dot(1, 0));
            if (player.body.getLinearVelocity().y > 0) angle = -angle;
            Vector2 v = new Vector2();
            float f = .125f * MathUtils.sin(GameScreen.ELAPSED_TIME * 30);

            v.set(f * MathUtils.cosDeg(angle), f * MathUtils.sinDeg(angle));
            lightningTrail.info.angleOffset = angle + f + 15;
            lightningTrail.position.set(player.body.getPosition().cpy());
            Vector2 offset = new Vector2();
            offset.set(player.body.getLinearVelocity().nor());
            offset.scl(-.2f);
            offset.add(v);
            if (player.body.getLinearVelocity().len() > .5f * player.stats.speed) {
                lightningTrail.createParticleWithDelay(.01f, offset, Vector2.Zero, Vector2.Zero);
                if (!Assets.lightning_particle_sound.isPlaying()) Assets.lightning_particle_sound.play();
            } else {
                lightningTrail.clear();
                if (Assets.lightning_particle_sound.isPlaying()) Assets.lightning_particle_sound.pause();
            }
        }
        else {
            lightningTrail.clear();
            if (Assets.lightning_particle_sound.isPlaying()) Assets.lightning_particle_sound.pause();
        }
    }

    private void handleTimers(float delta) {
        GameScreen.ELAPSED_TIME += delta;
        if (MODE != Selectable.MODE_ZEN) loseTimer += delta;

        windTimer += delta;
        if (windTimer > windTime) {
            pickNewWindForce();
            windTimer = 0;
        }

        if (wonTimer >= WON_TIME) {
            int pointsAdded = 1;
            if (score.wonWithRareCloud) pointsAdded += 1;
            points += pointsAdded;
            level += 1;
            game.winScreen.pointsAdded = pointsAdded;
            game.set(game.winScreen);
            score.compoundedScore += score.generateScore();
            //game.set(MyGdxGame.UPGRADE_SCREEN);
        }

        if (loseTimer >= GameScreen.LOSE_TIME) {
            loseTimer = GameScreen.LOSE_TIME;

            if (sunClouds.size() != 1) this.loseLevel(delta);
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

        if (powerUpTimer > 0) {
            this.powerUpTimer -= delta;
        }

    }

    private void handleCloud(Cloud c, float delta) {

        c.resetPushedTimer(delta);
        c.handleJustSpawnedTimer(delta);

        boolean shouldBeRemoved = !c.justSpawned() && c.offScreen() && (c != winCloud);

        if (shouldBeRemoved) removedObjects.add(c);

        if (c.getWinPercent() > 0) {
            if (!sunClouds.contains(c)) {
                c.resetSunTimer(player.stats.sunReset, delta);
            }
        }

        //Increments timer of clouds withing beam and checks for win condition
        if (sunClouds.contains(c)) {
                //Multiply delta * 1/sunClouds.size ??? this will make the timer go up slower for each cloud in sunbeam?
                float timeMod = 1;
                if (MODE != Selectable.MODE_CLEAR_SKIES) {
                    if (player.stats.sunCloudSizeMod != 0)
                        timeMod = 1f / ((player.stats.sunCloudSizeMod * sunClouds.size()));
                    if (timeMod > 1) timeMod = 1;
                }

                if (MODE == Selectable.MODE_CLEAR_SKIES) {
                    if (!(c instanceof ClearSkiesCloud)) timeMod = 0;
                    if (wonTimer <= 0 && sunClouds.size() > 1) {
                        timeMod = -sunClouds.size();
                        //System.out.println(timeMod);
                        //timeMod *= (1f - (player.stats.sunCloudSizeMod/Stats.SUN_CLOUD_SIZE_MOD.endVal));
                    }
                    if (sunClouds.size() == 1) timeMod = 1;
                }

                c.handleSunTimer(delta * timeMod);
                if (c.getWinPercent() == 1) {
                    powerUpTimer = 0;
                    this.winLevel(c, delta);
                }
        }

        //Keeps a list of clouds that were in the sunbeam
        if (sunBeam.contains(c.body) && !sunClouds.contains(c)) {
            sunClouds.add(c);
        }

        //If cloud was contained by sunbeam but is no longer, then reset its suntimer
        else if (!sunBeam.contains(c.body)){
            if (sunClouds.contains(c)) {
                if (c != winCloud) {
                    sunClouds.remove(c);
                }
            }
        }


    }

    private void winLevel(Cloud c, float delta) {
        if (c instanceof RareCloud) {
            score.wonWithRareCloud = true;
        }
        if (wonTimer >= WON_TIME) return;
        //if (wonTimer == 0) Assets.playMusic(Assets.rainbow_sound[MathUtils.random(0, Assets.rainbow_sound.length - 1)]);
        else {
            player.stats.speed = prePowerUpStats.speed;
            player.stats.acceleration = prePowerUpStats.acceleration;
        }
        wonTimer += delta;
        if (winCloud == null) winCloud = c;

    }

    public void resetPlayThrough() {
        stats = new Stats();
        score.reset();
        level = 0;
        points = Stats.STATS.size() * Stats.NUM_OF_UPGRADES;//0;
    }

    private void loseLevel(float delta) {
        if (wonTimer > 0) return;
        game.set(game.loseScreen);
    }

    public void startLevel() {

        System.out.println("LEVEL: " + level);

        wonTimer = 0;
        loseTimer = 0;
        beamTimer = 0;
        cloudTimer = 0;
        windTimer = 0;
        powerUpTimer = 0;
        windForce.set(0, 0);
        winCloud = null;
        focusedCloud = null;
        //take width if normal. add with if clear skies
        int widthMod = -1;
        if (MODE == Selectable.MODE_CLEAR_SKIES) widthMod = 1;
        float sunBeamWidthScl = 1 + widthMod * (level * .007f);
        if (sunBeamWidthScl < .4f) sunBeamWidthScl = .4f;
        sunBeam = new Sunbeam(sunBeamWidthScl);
        removedObjects.addAll(objects);
        objects.clear();
        sunClouds.clear();
        Assets.player_win_animation.setTime(0);
        player = (Player) Object.createObject(Object.PLAYER);
        player.set(this, 0, 0, stats);
        prePowerUpStats = player.stats;

        score.wonWithRareCloud = false;
        rareCloudAlreadySpawnedThisLevel = false;
        chanceOfRareCloud = MathUtils.random(.5f, 10f);

        //level things
        if (level % 3 == 0) this.numOfRainParticlesPerFrame += 1;
        if (numOfRainParticlesPerFrame > 20) numOfRainParticlesPerFrame = 20;
        this.nextBeamTime = 5 - (level * .10f);
        if (nextBeamTime < 0) nextBeamTime = 0;
        sunBeam.speed = 1 + .018f * level;
        windTime = 6 - (level * .03f);
        if (windTime < 3) windTime = 3;
        maxWindForce += .01f + (.03f * level);
        maxWindForce *= .20f;
        //System.out.println("Max wind force " + maxWindForce);
        lightning.maxNextLightningTime = 60 - (level * .85f);
        if (lightning.maxNextLightningTime < 0) lightning.maxNextLightningTime = 0;

        rainParticles.clear();
        blowParticles.clear();
        lightningTrail.clear();
        rainbowTrail.clear();

        if (level == 0 && MODE != Selectable.MODE_CLEAR_SKIES) {
            //Object o = Object.createObject(Object.POWER_UP);
            //o.set(this, 0, 0);
            Cloud c = (Cloud) Object.createObject(Object.CLOUD);
            c.set(this, 0, 0, Vector2.Zero);
        }

        if (MODE == Selectable.MODE_CLEAR_SKIES) {
            clearSkiesCloud = (ClearSkiesCloud) Object.createObject(Object.CLEAR_SKIES_CLOUD);
            clearSkiesCloud.set(this, 0, 0, Vector2.Zero);
        }

    }

    private void movePlayerTowards(Vector2 endLocation) {
        if (player.body.getLinearVelocity().len() > player.stats.speed) return;
        Vector2 force = new Vector2(0, 0);
        Vector2 startLocation = player.body.getPosition();
        //vector between desination and player;
        force.set(endLocation.x - startLocation.x, endLocation.y - startLocation.y);
        force.nor();
        force.scl(player.stats.acceleration);
        force.scl(1 + (.05f * player.stats.speed));
        player.body.applyForce(force, player.body.getPosition(), true);
        //player.body.applyLinearImpulse(force, player.body.getPosition(), true);
    }

    private void applyDragForce(Object o) {
        Vector2 dragForce = new Vector2(0, 0);
        Vector2 v = o.body.getLinearVelocity();
        dragForce.set(-v.x, -v.y);
        //dragForce.scl(.32f);
        if (o instanceof Player) {
            //dragForce.scl(.35f);
            dragForce.scl(.85f);
            dragForce.scl((player.stats.acceleration/ Stats.ACCELERATION.endVal));
        }
        if (o instanceof Cloud) dragForce.scl(.10f);
        o.body.applyForce(dragForce, o.body.getPosition(), true);
    }

    private void spawnCloud() {

        float x = 0;
        float y = 0;

        Vector2 initialVel = new Vector2(0, 0);

        boolean left = MathUtils.randomBoolean();

        if (left) {
            x = -GameScreen.UNIT_WIDTH / 2;
            initialVel.x = 1f;
        }
        if (!left) {
            x = GameScreen.UNIT_WIDTH / 2;
            initialVel.x = -1f;
        }

        y = MathUtils.random(-GameScreen.UNIT_HEIGHT / 2 + 1.f, GameScreen.UNIT_HEIGHT / 2 - 1.5f);

        int ID = Object.CLOUD;
        if (MathUtils.random(0f, 1f) < (1) * (level / 70f)) {
            ID = Object.DENSE_CLOUD;
        }

        if (MODE != Selectable.MODE_CLEAR_SKIES && !rareCloudAlreadySpawnedThisLevel) {
            if (MathUtils.random(0f, 100f) < chanceOfRareCloud) {
                ID = Object.RARE_CLOUD;
                rareCloudAlreadySpawnedThisLevel = true;
            }
        }

        if (MODE == Selectable.MODE_CLEAR_SKIES) {
            initialVel.scl(.5f + (.5f * (1f + level/80f)));
        }

        Cloud c = (Cloud) Object.createObject(ID);
        c.set(this, x, y, initialVel);
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
        //maxWindForce = .1f;
        windForce.scl(MathUtils.random(minForce, maxWindForce));
        //windForce.scl();
        //System.out.println(windForce.len());
    }

    private void handlePlayerInput(float delta) {

        if (focusedCloud != null) {
            //Cloud gets bigger fastest at the start to compensate for light taps
            focusedCloud.pushedTimer += .65f*delta;
            focusedCloud.pushedTimer += delta * .05f * (1/focusedCloud.pushedTimer);
            if (focusedCloud.pushedTimer >= Cloud.PUSH_TIME) focusedCloud.pushedTimer = Cloud.PUSH_TIME;
            //
            player.manipulateCloud(focusedCloud, SELECTED_CHARACTER);

            // player stops pushing if cloud is out of range or no longer tap held
            if (player.body.getPosition().dst(focusedCloud.body.getPosition()) > player.stats.pushRange) focusedCloud = null;
            else if (!focusedCloud.body.getFixtureList().first().testPoint(mouseLocation)) focusedCloud = null;
        }

        //If not blowing then move player towards tapped location
        if (inputHeld && focusedCloud == null) this.movePlayerTowards(mouseLocation);

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

        //doesn't rotate for angles more than 65 degrees from x axis unless player is blowing
        boolean angleLessThanThreshold = Math.abs(playerAngleToFocusedCloudInDegrees) < 65;

        //boolean angleLessThan45 = (((int) playerAngleToFocusedCloudInDegrees) % 90 != 0);
        if (inputHeld && angleLessThanThreshold || (focusedCloud != null)) targetAngle = playerAngleToFocusedCloudInDegrees;

        playerRotation +=  1.4f * (targetAngle - playerRotation) * delta;

    }

    private void handlePlayerAnimations() {
        //player animations
        if (focusedCloud != null) {
            if (focusedCloud.body.getPosition().x > player.body.getPosition().x) player.setSprite(Assets.player_blowing_animation);
            else {
                player.setSprite(Assets.player_blowing_animation_flipped);
            }

                Assets.playMusic(Assets.blow_sound);

            //if (focusedCloud.body.getPosition().x < player.body.getPosition().x) player.getSprite().setFlip(true, false);
        }

        else if (wonTimer / WON_TIME >= .3f) {
            player.setSprite(Assets.player_win_animation);
            AnimatedBox2DSprite a = ((AnimatedBox2DSprite) player.getSprite());
            //cycle last two frames of animation
            if (a.isAnimationFinished()) a.setTime(5 * .35f);
        }

        else {
            if (player.getSprite() instanceof AnimatedBox2DSprite) {
                AnimatedBox2DSprite a = ((AnimatedBox2DSprite) player.getSprite());
                if (a.isAnimationFinished()) player.setSprite(Assets.player_default_image);
            }

            else {

                player.setSprite(Assets.player_default_image);
                //Ensure next animation beginds on first frame
                //Perhaps store last animation used and set its time to zero to be more general
                Assets.player_blowing_animation.setTime(0);
                Assets.player_blowing_animation_flipped.setTime(0);
            }
        }

        //End of player animations
        if (focusedCloud == null) if (Assets.blow_sound.getPosition() == 5) Assets.blow_sound.pause();
    }

    private void handleRemovedObjects() {
        for (Object o: removedObjects) {
            if (objects.contains(o)) {
                objects.remove(o);
                world.destroyBody(o.body);
            }
        }

        removedObjects.clear();
    }


    public void draw(float delta) {
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);

        game.batcher.begin();

        game.batcher.draw(Assets.background_image, -(UNIT_WIDTH)/2, -(UNIT_HEIGHT)/2, UNIT_WIDTH, UNIT_HEIGHT);
        //float playerSclOff = .2f *(MathUtils.sin(GameScreen.ELAPSED_TIME * 1.25f));

        //manipulate bg
        //game.batcher.setColor(.3f, .5f, .5f, .6f);
        //game.batcher.draw(Assets.lightning_image, -GameScreen.UNIT_WIDTH/2, -GameScreen.UNIT_HEIGHT/2);
        //game.batcher.setColor(1, 1, 1, 1);

        lightningTrail.drawParticles(game.batcher);
        rainbowTrail.drawParticles(game.batcher);

        if (SELECTED_CHARACTER == Selectable.CHARACTER_STORM) {
            player.setSprite(Assets.player_storm_image);
        }

        if (focusedCloud != null) {
            if (inputHeld) {
                float pushStrengthPer = player.stats.pushStrength/ Stats.PUSH_STRENGTH.endVal;
                if (player.blowTimer < .6f) if (player.sclOff < (1.6f * (1 + 1.5f * pushStrengthPer))) player.sclOff += 1.15f * (1 + .55f * pushStrengthPer) * delta;
                if (player.blowTimer > .6f) {
                    if (player.sclOff > 1) {
                        player.sclOff -= 1.25f * delta;
                    }
                }

                if (player.blowTimer < MathUtils.PI) player.blowTimer += delta * 3;
                if (player.blowTimer > MathUtils.PI) player.blowTimer = 0;
                player.yScl = 1 - ((.2f * (1 + 1.5f * pushStrengthPer)) * MathUtils.sin(player.blowTimer - 1.f));
                player.xScl = 1 + ((.2f * (1 + 1.5f * pushStrengthPer)) * MathUtils.sin(player.blowTimer - 1.f));
            }
        }

        if (focusedCloud == null) {
            player.blowTimer = 0;
            player.yScl -= (player.yScl - 1) * 4f * delta;
            player.xScl -= (player.xScl - 1) * 4f * delta;
            player.sclOff -= (player.sclOff - 1) * 7 * delta;
        }

        //repeat animation
        if (player.getSprite() instanceof AnimatedBox2DSprite) {
            AnimatedBox2DSprite a = ((AnimatedBox2DSprite) player.getSprite());
            if (a == Assets.player_blowing_animation || a == Assets.player_blowing_animation_flipped) {
                //start animation loop on 2nd frame
                if (focusedCloud != null) if (a.isAnimationFinished()) a.setTime(.20f * 1);
            }
        }

        player.getSprite().setScale(1.2f * player.sclOff);
        player.getSprite().setScale(player.getSprite().getScaleX() * player.xScl, player.getSprite().getScaleY() * player.yScl);
        player.getSprite().setRotation(playerRotation);

        for (Object o: objects) {
            if (o instanceof ClearSkiesCloud) continue;
            if (! (o instanceof  Cloud)) o.getSprite().draw(game.batcher, o.body);
            Assets.cloud_image.setAlpha(Assets.CLOUD_ALPHA);
            if (o instanceof Cloud) {
                Cloud c = (Cloud) o;
                float pushedTimer = c.pushedTimer;
                float scale = Assets.CLOUD_IMAGE_SCALE;
                if (pushedTimer > 0) {
                    scale += pushedTimer;
                    float alpha = Assets.CLOUD_ALPHA - pushedTimer;
                    if (scale > 2f) scale = 2f;
                    if (alpha < .4f) alpha = .4f;
                    if (o instanceof DenseCloud) alpha += .4f;
                    if (alpha < 0) alpha = 0;
                    if (alpha > 1) alpha = 1;
                    o.getSprite().setAlpha(alpha);
                }
                o.getSprite().setScale(scale);// + (.16f * MathUtils.sin(GameScreen.ELAPSED_TIME + o.timerOffset)));
                if (o == winCloud) winCloud.drawRainbow(game.batcher, (wonTimer/RAINBOW_DRAW_TIME));
                float cloudYOff = (.07f) * (MathUtils.sin(GameScreen.ELAPSED_TIME  + o.timerOffset * .85f * (((Cloud) o).getWinPercent() + 1)));
                c.getSprite().draw(game.batcher, o.body.getPosition().x, o.body.getPosition().y + cloudYOff, Cloud.WIDTH * Assets.CLOUD_IMAGE_SCALE, Cloud.HEIGHT * Assets.CLOUD_IMAGE_SCALE, 0);
                c.getSprite().setScale(Assets.CLOUD_IMAGE_SCALE);
                c.getSprite().setAlpha(Assets.CLOUD_ALPHA);
            }
            game.batcher.setColor(1, 1, 1, 1);
        }


        sunBeam.draw(game.batcher, 1);
        //game.batcher.end();

        //sr.setProjectionMatrix(guiCam.combined);

        //sr.begin(ShapeRenderer.ShapeType.Line);
        // sunBeam.drawBounds(sr);
        //sr.setColor(Color.BLACK);
        //sr.setColor(Color.ORANGE);


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

        //sr.end();

        //game.batcher.begin();
        if (MODE != Selectable.MODE_CLEAR_SKIES) rainParticles.drawParticles(game.batcher);
        blowParticles.drawParticles(game.batcher);
        stormCloudParticles.drawParticles(game.batcher);
        for (Object o: objects) {
            if (o instanceof Cloud) {
                Cloud c = (Cloud) o;
                if (c.getWinPercent() > 0) c.drawTimer(game.batcher, sunBeam.nextXDest);
            }
        }

        lightning.drawLightning(game.batcher);


        //allows me filter the games color
        //game.batcher.setColor(.8f, .65f, .65f, .2f);
        //game.batcher.draw(Assets.lightning_image, -GameScreen.UNIT_WIDTH/2, -GameScreen.UNIT_HEIGHT/2);
        //game.batcher.setColor(1, 1, 1, 1);

        float a = 1;
        a = ((loseTimer/GameScreen.LOSE_TIME)) * (1 - (wonTimer/WON_TIME));

        help_button.draw(game.batcher, game.font);

        game.batcher.setColor(.4f, .4f, .45f, .4f * a);
        game.batcher.draw(Assets.lightning_image, -GameScreen.UNIT_WIDTH/2, -GameScreen.UNIT_HEIGHT/2);
        game.batcher.setColor(1, 1, 1, 1);

        if (wonTimer > 0) {
            float alpha = wonTimer * 2f/WON_TIME;
            if (alpha >= 1) alpha = 1;
            sunBeam.draw(game.batcher, alpha);
        }

        game.batcher.end();


    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
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
                if (o.body.getPosition().dst(player.body.getPosition()) >= player.stats.pushRange) continue;
                if (o.body.getFixtureList().first().testPoint(mouseLocation)) {
                    //System.out.println("touched!");
                    focusedCloud = (Cloud) o;
                }
            }
        }

        if (help_button.containsPoint(mouseLocation.x, mouseLocation.y)) {
            help_button.pressed = true;
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
        //System.out.println(mouseLocation);

        for (Object o: objects) {
            if (o instanceof Cloud) {
                if (o.body.getPosition().dst(player.body.getPosition()) >= player.stats.pushRange) continue;
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
