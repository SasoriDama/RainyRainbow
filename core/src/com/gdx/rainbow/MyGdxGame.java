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

	public static int WIDTH = 1920;//1920/2;
	public static int HEIGHT = 1200;//1200/2;

	public static final String TITLE = "Rainy Rainbow";

    public SpriteBatch batcher;

	//Horrible disgusting temporary fix
	public GameScreen s1;
	public UpgradeScreen s2;

	private boolean game = false;
	//

	@Override
	public void create () {
	    batcher = new SpriteBatch();
		Assets.load();
		//setScreen(new GameScreen(this));
		//setScreen(new UpgradeScreen(this, Stats.STARTING_STATS));

		s1 = new GameScreen(this);
		s2 = new UpgradeScreen(this, null);

		set(game);

		MyGdxGame.WIDTH = Gdx.graphics.getWidth();
		MyGdxGame.HEIGHT = Gdx.graphics.getHeight();

	}

	public void set(boolean game) {
		this.game = game;
		if (game) {
			Gdx.input.setInputProcessor(s1);
			s1.stats = s2.stats;
			s1.points = s2.points;

		}
		else {
			Gdx.input.setInputProcessor(s2);
			s2.stats = s1.stats;
			s2.points = s1.points;
		}
	}

	@Override
	public void render () {
		super.render();
		if (game) s1.render(Gdx.graphics.getDeltaTime());
		else s2.render(Gdx.graphics.getDeltaTime());
	}

	@Override
    public void dispose() {
		super.dispose();
	    this.getScreen().dispose();
        Assets.dispose();
        batcher.dispose();
    }

}
