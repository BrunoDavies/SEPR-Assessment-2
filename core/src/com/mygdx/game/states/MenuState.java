package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Kroy;
import com.mygdx.game.misc.Button;

/**
 * An implementation of the abstract class State which controls the
 * menu screen rendering and input handling.
 *
 * @author Lucy Ivatt
 * @since 20/11/2019
 */

public class MenuState extends State {

    private final int SPACING = 100;

    private Texture background;
    private Button play;
    private Button options;
    private Button credits;
    private Button quit;


    public MenuState(GameStateManager gameStateManager) {
        super(gameStateManager);
        background = new Texture("Menu.jpg");
        options = new Button(new Texture("optionspressed.png"), new Texture("options.png"), 350,100, new Vector2((Kroy.WIDTH / 2) - 350 - (SPACING / 2), 300), false);
        play = new Button(new Texture("playpressed.png"), new Texture("play.png"),350, 100, new Vector2((Kroy.WIDTH / 2) - 350 * 2 - SPACING - SPACING / 2, 300), false);
        credits = new Button(new Texture("creditspressed.png"), new Texture("credits.png"),350, 100, new Vector2((Kroy.WIDTH / 2) + SPACING / 2, 300), false);
        quit = new Button(new Texture("quitpressed.png"), new Texture("quit.png"),350, 100, new Vector2((Kroy.WIDTH / 2) + SPACING + (SPACING / 2) + 350, 300), false);

    }

    public void handleInput() {
        if (play.mouseInRegion()){
            play.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.set(new PlayState(gameStateManager));
            }
        }
        else {
            play.setActive(false);
        }
        if (options.mouseInRegion()){
            options.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new OptionState(gameStateManager));
            }
        }
        else {
            options.setActive(false);
        }

        if (credits.mouseInRegion()){
            credits.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.push(new CreditState(gameStateManager));
            }
        }
        else {
            credits.setActive(false);
        }

        if (quit.mouseInRegion()){
            quit.setActive(true);
            if (Gdx.input.isTouched()) {
                Gdx.app.exit();
                System.exit(0);
            }
        }
        else {
            quit.setActive(false);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
            System.exit(0);
        }
    }

    @Override
    public void update(float deltaTime) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Kroy.WIDTH, Kroy.HEIGHT);
        spriteBatch.draw(play.getTexture(), play.getPosition().x, play.getPosition().y, play.getWidth(), play.getHeight());
        spriteBatch.draw(options.getTexture(), options.getPosition().x, options.getPosition().y, options.getWidth(), options.getHeight());
        spriteBatch.draw(credits.getTexture(), credits.getPosition().x, credits.getPosition().y, credits.getWidth(), credits.getHeight());
        spriteBatch.draw(quit.getTexture(), quit.getPosition().x, quit.getPosition().y, quit.getWidth(), quit.getHeight());
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        play.dispose();
        options.dispose();
        credits.dispose();
        quit.dispose();
    }
}
