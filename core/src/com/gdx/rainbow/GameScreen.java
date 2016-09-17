package com.gdx.rainbow;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.gdx.rainbow.objects.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gdx.rainbow.objects.Object;
import com.gdx.rainbow.particles.*;
import com.gdx.rainbow.particles.Particle;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FillViewport;


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
    public ArrayList<Object> objects;
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

    public final float PLAYER_PUSH_RANGE = 1f;

    //Temporary Shape Renderer
    ShapeRenderer sr;

    public GameScreen(MyGdxGame game) {
        //http://gamedev.stackexchange.com/questions/93816/libgdx-box2d-and-screen-viewport-scaling
        this.game = game;
        Gdx.input.setInputProcessor(this);
        mouseLocation = new Vector2();
        //guiCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        guiCam = new OrthographicCamera();
        //viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), guiCam);
        //RECENT ONE BELOW
        //viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), guiCam);
        viewport = new FillViewport(UNIT_WIDTH, UNIT_HEIGHT, guiCam);

        //viewport = new FillViewport(50, 50, guiCam);
        //viewport.project(new Vector2(2, 2));
        viewport.apply();

        guiCam.position.set(0, 0, 0);

        //guiCam.zoom = 5;

        //UNIT_WIDTH *= guiCam.zoom;
        //UNIT_HEIGHT *= guiCam.zoom;

        world = new World(new Vector2(0, 0), true);

        objects = new ArrayList<Object>();
        sunClouds = new ArrayList<Cloud>();

        player = (Player) Object.createObject(Object.PLAYER);
        player.set(this, 0, 0);

        sunBeam = new Sunbeam();

        sr = new ShapeRenderer();

        rainParticles = new ParticleSystem(.01f);

        //Cloud c = (Cloud) Object.createObject(Object.CLOUD);
        //c.set(this, 0, 0, new Vector2(0, 0));
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
        this.handleSunbeam(delta);
        this.handleClouds(delta);

        //nextBeamLocation = player.body.getPosition().x;
        //System.out.println(objects.size());
        //rainParticles.update(delta);

        for (Object o: objects) {
            applyDragForce(o);

            if (o instanceof Cloud) {

                if (o.body.getPosition().x < -GameScreen.UNIT_WIDTH/2 || o.body.getPosition().x > GameScreen.UNIT_WIDTH/2 ||
                        o.body.getPosition().y < -GameScreen.UNIT_HEIGHT/2 || o.body.getPosition().y > GameScreen.UNIT_HEIGHT/2) {
                        o.removed = true;
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

    }

    private void movePlayerTowards(Vector2 endLocation) {
        Vector2 force = new Vector2(0, 0);
        Vector2 startLocation = player.body.getPosition();
        //vector between desination and player;
        force.set(endLocation.x - startLocation.x, endLocation.y - startLocation.y);
        force.nor();
        force.scl(.030f);
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
        force.set(1/dx, 1/dy);
        System.out.println("Pushed with: " + 1/dx + ", " + 1/dy);
        if (force.len() < 0.90f) return;
        force.scl(-.1f);
        c.body.applyForce(force, c.body.getPosition(), true);
    }

    private void handleSunbeam(float delta) {
        this.beamTimer += delta;
        if (beamTimer >= nextBeamTime) {
            beamTimer = 0;
            nextBeamLocation = MathUtils.random(-GameScreen.UNIT_WIDTH/2 + GameScreen.UNIT_WIDTH/4, GameScreen.UNIT_WIDTH/2 - GameScreen.UNIT_WIDTH/4);
            //nextBeamLocation = MyGdxGame.WIDTH/2;
            //System.out.println(nextBeamLocation);
        }
        sunBeam.update(nextBeamLocation, delta);
    }

    private void handleClouds(float delta) {
        this.cloudTimer += delta;
        if (cloudTimer >= nextCloudTime) {
            cloudTimer = 0;
            this.spawnCloud();
        }
    }

    private void applyDragForce(Object o) {
        Vector2 dragForce = new Vector2(0, 0);
        Vector2 v = o.body.getLinearVelocity();
        dragForce.set(-v.x, -v.y);
        dragForce.scl(.32f);
        o.body.applyForce(dragForce, o.body.getPosition(), true);
    }

    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);

        game.batcher.begin();
        game.batcher.draw(Assets.background_image, -(UNIT_WIDTH)/2, -(UNIT_HEIGHT)/2, UNIT_WIDTH, UNIT_HEIGHT);
        for (Object o: objects) {
            if (o instanceof Player) Assets.player_image.draw(game.batcher, o.body);
            if (o instanceof Cloud) Assets.cloud_image.draw(game.batcher, o.body);
        }

        game.batcher.end();

        sr.setProjectionMatrix(guiCam.combined);

        sr.begin(ShapeRenderer.ShapeType.Line);
        sunBeam.draw(sr);
        sr.setColor(Color.BLACK);
        //sr.line(MyGdxGame.WIDTH/2 + player.body.getPosition().x, MyGdxGame.HEIGHT/2 + player.body.getPosition().y, MyGdxGame.WIDTH/2 + mouseLocation.x, MyGdxGame.HEIGHT/2 + mouseLocation.y);

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
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
