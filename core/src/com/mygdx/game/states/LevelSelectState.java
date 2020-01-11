package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Kroy;
import com.mygdx.game.misc.Button;
import com.mygdx.game.sprites.Alien;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An implementation of the abstract class State which controls the
 * menu screen rendering and input handling.
 *
 * @author Lucy Ivatt
 * @since 11/1/2020
 */

public class LevelSelectState extends State{

    private Texture background;
    private Button level1;
    private Button level2;
    private Button level3;
    private Button level4;
    private Button level5;
    private Button level6;
    private Button back;
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private Preferences settings;

    protected LevelSelectState(GameStateManager gameStateManager) {
        super(gameStateManager);
        settings = Gdx.app.getPreferences("My Preferences");
        back = new Button(new Texture("backbutton2.png"), new Texture("backbutton1.png"),
                100, 100, new Vector2(30, 960), false, false);

        level1 = new Button(new Texture("PressedBlue1.png"), new Texture("NotPressedBlue1.png"),
                350, 100, new Vector2(Kroy.WIDTH / 2 - 350 / 2 - 100 - 350, 400), false, false);

        level2 = new Button(new Texture("PressedGrey2.png"), new Texture("PressedGrey2.png"),
                350, 100, new Vector2(Kroy.WIDTH / 2 - 350 / 2, 400), false, true);

        level3 = new Button(new Texture("PressedGrey3.png"), new Texture("PressedGrey3.png"),
                350, 100, new Vector2(Kroy.WIDTH / 2 + 350 / 2 + 100, 400), false, true);

        level4 = new Button(new Texture("PressedGrey4.png"), new Texture("PressedGrey4.png"),
                350, 100, new Vector2(Kroy.WIDTH / 2 - 350 / 2 - 100 - 350, 200), false, true);

        level5 = new Button(new Texture("PressedGrey5.png"), new Texture("PressedGrey5.png"),
                350, 100, new Vector2(Kroy.WIDTH / 2 - 350 / 2, 200), false, true);

        level6 = new Button(new Texture("PressedGrey6.png"), new Texture("PressedGrey6.png"),
                350, 100, new Vector2(Kroy.WIDTH / 2 + 350 / 2 + 100, 200), false, true);

        buttons.add(level1);
        buttons.add(level2);
        buttons.add(level3);
        buttons.add(level4);
        buttons.add(level5);
        buttons.add(level6);
        background = new Texture("Menu.jpg");
    }

    public void handleInput() {
        if (back.mouseInRegion()) {
            back.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.pop();
            }
        } else {
            back.setActive(false);
        }

        if (level1.mouseInRegion() && level1.isLocked() == false){
            level1.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new PlayState(gameStateManager, 1));
            }
        }
        else {
            level1.setActive(false);
        }

        if (level2.mouseInRegion() && level2.isLocked() == false){
            level2.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new PlayState(gameStateManager, 2));
            }
        }
        else {
            level2.setActive(false);
        }

        if (level3.mouseInRegion() && level3.isLocked() == false){
            level3.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new PlayState(gameStateManager, 3));
            }
        }
        else {
            level3.setActive(false);
        }

        if (level4.mouseInRegion() && level4.isLocked() == false){
            level4.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new PlayState(gameStateManager, 4));
            }
        }
        else {
            level4.setActive(false);
        }

        if (level5.mouseInRegion() && level5.isLocked() == false){
            level5.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new PlayState(gameStateManager, 5));
            }
        }
        else {
            level5.setActive(false);
        }

        if (level6.mouseInRegion() && level6.isLocked() == false){
            level6.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new PlayState(gameStateManager, 6));
            }
        }
        else {
            level6.setActive(false);
        }


        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
            System.exit(0);
        }
    }

    @Override
    public void update(float deltaTime) {
        handleInput();
        if(settings.getBoolean("1") == true) {
            level1.setOnTexture(new Texture("PressedGreen1.png"));
            level1.setOffTexture(new Texture("NotPressedGreen1.png"));
            level2.setLocked(false);

            level2.setOnTexture(new Texture("PressedBlue2.png"));
            level2.setOffTexture(new Texture("NotPressedBlue2.png"));
        }

        if(settings.getBoolean("2") == true) {
            level2.setOnTexture(new Texture("PressedGreen2.png"));
            level2.setOffTexture(new Texture("NotPressedGreen2.png"));
            level3.setLocked(false);

            level3.setOnTexture(new Texture("PressedBlue3.png"));
            level3.setOffTexture(new Texture("NotPressedBlue3.png"));
        }

        if(settings.getBoolean("3") == true) {
            level3.setOnTexture(new Texture("PressedGreen3.png"));
            level3.setOffTexture(new Texture("NotPressedGreen3.png"));
            level4.setLocked(false);

            level4.setOnTexture(new Texture("PressedBlue4.png"));
            level4.setOffTexture(new Texture("NotPressedBlue4.png"));
        }

        if(settings.getBoolean("4") == true) {
            level4.setOnTexture(new Texture("PressedGreen4.png"));
            level4.setOffTexture(new Texture("NotPressedGreen4.png"));
            level5.setLocked(false);

            level5.setOnTexture(new Texture("PressedBlue5.png"));
            level5.setOffTexture(new Texture("NotPressedBlue5.png"));
        }

        if(settings.getBoolean("5") == true) {
            level5.setOnTexture(new Texture("PressedGreen5.png"));
            level5.setOffTexture(new Texture("NotPressedGreen5.png"));
            level6.setLocked(false);

            level6.setOnTexture(new Texture("PressedBlue6.png"));
            level6.setOffTexture(new Texture("NotPressedBlue6.png"));
        }

        if(settings.getBoolean("6") == true) {
            level6.setOnTexture(new Texture("PressedGreen5.png"));
            level6.setOffTexture(new Texture("NotPressedGreen5.png"));
        }


    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Kroy.WIDTH, Kroy.HEIGHT);
        spriteBatch.draw(back.getTexture(), back.getPosition().x, back.getPosition().y, back.getWidth(), back.getHeight());
        for (Button level : buttons) {
            spriteBatch.draw(level.getTexture(), level.getPosition().x, level.getPosition().y, level.getWidth(), level.getHeight());
        }
        spriteBatch.end();
    }

    @Override
    public void dispose() {

    }
}