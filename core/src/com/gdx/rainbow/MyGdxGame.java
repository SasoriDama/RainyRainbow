package com.gdx.rainbow;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.rainbow.screens.*;
import com.gdx.rainbow.screens.menus.utils.Menu;

public class MyGdxGame extends Game {

	public static int WIDTH = 1920;//1920/2;
	public static int HEIGHT = 1200;//1200/2;

	public static final String TITLE = "Rainy Rainbow";

    public SpriteBatch batcher;

	public OrthographicCamera guiCam;
	public Viewport viewport;

	public BitmapFont font;

	//Horrible disgusting temporary fix
	public GameScreen gameScreen;
	public UpgradeScreen upgradeScreen;
	public MainMenu mainMenu;
	public CharacterSelectScreen characterSelectScreen;
	public WinScreen winScreen;
	public LoseScreen loseScreen;
	public HelpScreen helpScreen;

	private ScreenAdapter screen;

	public static Preferences PREFS;
	public static Unlocks UNLOCKS;

	public MyGdxGame() {
	}

	@Override
	public void create () {
	    batcher = new SpriteBatch();

		PREFS = Gdx.app.getPreferences("My Preferences");
		PREFS.putBoolean("Unlocked_Mode_ClearSkies", true);
		PREFS.putBoolean("Unlocked_Mode_Zen", true);
		PREFS.putBoolean("Unlocked_Character_Storm", true);
		PREFS.putBoolean("Unlocked_Character_Storm", true);
		PREFS.putInteger("High_Score", 0);

		UNLOCKS = new Unlocks(PREFS);
		Assets.load();
		Selectable.initializeImages();

		gameScreen = new GameScreen(this);
		upgradeScreen = new UpgradeScreen(this);
		mainMenu = new MainMenu(this);
		characterSelectScreen = new CharacterSelectScreen(this);
		winScreen = new WinScreen(this);
		loseScreen = new LoseScreen(this);
		helpScreen = new HelpScreen(this);


		Score.HIGH_SCORE = MyGdxGame.PREFS.getInteger("High_Score");

		set(mainMenu);

		MyGdxGame.WIDTH = Gdx.graphics.getWidth();
		MyGdxGame.HEIGHT = Gdx.graphics.getHeight();

	}

	public void set(ScreenAdapter screen) {
		if (Assets.rain_sound.isPlaying()) Assets.rain_sound.pause();
		ScreenAdapter previousScreen = this.screen;
		this.screen = screen;

		if (screen instanceof Menu) {
			Menu m = (Menu) screen;
			m.onScreenSwitch(previousScreen);
		}

		if (screen == gameScreen && previousScreen != helpScreen) gameScreen.startLevel();

		Gdx.input.setInputProcessor((InputProcessor) this.screen);

	}

	@Override
	public void render () {
		super.render();
		screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
    public void dispose() {
		System.out.println("PREFS NOT SAVED");
		//PREFS.flush();
		super.dispose();
        Assets.dispose();
        batcher.dispose();
    }

}
