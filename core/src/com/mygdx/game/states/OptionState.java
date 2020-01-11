package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Kroy;
import com.mygdx.game.misc.Button;
import com.badlogic.gdx.audio.*;

public class OptionState extends State implements InputProcessor {

    private Texture background;
    private Button back;
    private Button musicToggle;
    private Button effectsToggle;
    private Preferences settings;
    private Texture tick;
    private Texture cross;
    private Sound honk = Gdx.audio.newSound(Gdx.files.internal("honk.wav"));

    public OptionState(GameStateManager gameStateManager) {
        super(gameStateManager);
        settings = Gdx.app.getPreferences("My Preferences");
        background = new Texture("optionsMenu.png");
        back = new Button(new Texture("backbutton2.png"), new Texture("backbutton1.png"), 100, 100, new Vector2(30, 960), false);
        tick = new Texture("tick.png");
        cross = new Texture("cross.png");
        musicToggle = new Button(tick, cross, 100, 100, new Vector2(1091, 389), settings.getBoolean("music"));
        effectsToggle = new Button(tick, cross, 100, 100, new Vector2(1274, 174), settings.getBoolean("effects"));
        Gdx.input.setInputProcessor(this);
    }
    public void update(float deltaTime) {
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Kroy.WIDTH, Kroy.HEIGHT);
        spriteBatch.draw(back.getTexture(), back.getPosition().x, back.getPosition().y, back.getWidth(), back.getHeight());
        spriteBatch.draw(musicToggle.getTexture(), musicToggle.getPosition().x, musicToggle.getPosition().y, musicToggle.getWidth(), musicToggle.getHeight());
        spriteBatch.draw(effectsToggle.getTexture(), effectsToggle.getPosition().x, effectsToggle.getPosition().y, effectsToggle.getWidth(), effectsToggle.getHeight());
        spriteBatch.end();
    }

    public void dispose() {
        background.dispose();

    }

    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE){
            gameStateManager.pop();
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (musicToggle.clickInRegion(screenX, screenY)) {
            if (settings.getBoolean("music") == true) {
                settings.putBoolean("music", false);
                honk.play();
                musicToggle.setActive(false);
            }
            else {
                settings.putBoolean("music", true);
                musicToggle.setActive(true);
            }
            return false;
        }

        else if (effectsToggle.clickInRegion(screenX, screenY)) {
            if (settings.getBoolean("effects") == true) {
                settings.putBoolean("effects", false);
                effectsToggle.setActive(false);
            }
            else {
                settings.putBoolean("effects", true);
                effectsToggle.setActive(true);
            }
            return false;
        }

        else if (back.clickInRegion(screenX, screenY)) {
            gameStateManager.pop();
        }

        return false;

    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        if (back.clickInRegion(screenX, screenY)) {
            back.setActive(true);
        }

        else {
            back.setActive(false);
        }
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }
}
