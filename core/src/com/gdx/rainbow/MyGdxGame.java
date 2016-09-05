package com.gdx.rainbow;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class MyGdxGame extends Game {

	public static final int WIDTH = 1920/2;
	public static final int HEIGHT = 1200/2;

	public static final String TITLE = "Rainy Rainbow";

    public SpriteBatch batcher;

	@Override
	public void create () {
	    batcher = new SpriteBatch();
		Assets.load();
		setScreen(new GameScreen(this));
	}

	@Override
	public void render () {

		super.render();

	}

	@Override
    public void dispose() {
	    this.getScreen().dispose();
        Assets.dispose();
        batcher.dispose();
    }

}
