package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.misc.Button;
import com.mygdx.game.misc.Timer;
import com.mygdx.game.Kroy;
import com.mygdx.game.sprites.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the abstract class State which contains the methods and attributes required to control the
 * gameplay logic for each level as well as rendering to the screen.
 *
 * @author Lucy Ivatt & Matthew Gilmore
 */

public class PlayState extends State {

    private final float GAME_WIDTH = 1856;
    private final float GAME_HEIGHT = 832;

    private Texture background;
    private Texture map;

    private boolean levelLost;
    private boolean levelWon;
    private Preferences saveData;

    private Button quitLevel;
    private Button quitGame;

    private Timer timer;
    private float alienSpawnCountdown;
    private float timeSinceAlienKilled;
    private float timeSinceLastFortressRegen;
    private float timeLimit;

    private Entity fireStation;
    private Fortress fortress;
    private Firetruck firetruck1;
    private Firetruck firetruck2;
    private ArrayList<Entity> obstacles = new ArrayList<Entity>();
    public ArrayList<Firetruck> firetrucks = new ArrayList<Firetruck>();
    private ArrayList<Firetruck> destroyedFiretrucks = new ArrayList<Firetruck>();
    private ArrayList<Alien> aliens = new ArrayList<Alien>();
    private ArrayList<Projectile> bullets = new ArrayList<Projectile>();
    private ArrayList<Projectile> water = new ArrayList<Projectile>();

    private BitmapFont ui;
    private BitmapFont healthBars;
    private String level;
    private Sound waterShoot = Gdx.audio.newSound(Gdx.files.internal("honk.wav"));

    public PlayState(GameStateManager gsm, int levelNumber) {
        super(gsm);
        background = new Texture("LevelProportions.png");

        quitLevel = new Button(new Texture("PressedQuitLevel.png"), new Texture("NotPressedQuitLevel.png"),
                350 / 2, 100 / 2, new Vector2(30, 30), false, false);
        quitGame = new Button(new Texture("PressedQuitGame.png"), new Texture("NotPressedQuitGame.png"),
                350 / 2, 100 / 2, new Vector2(1920 - 30 - 350 / 2, 30), false, false);

        level = Integer.toString(levelNumber);
        levelLost = false;
        levelWon = false;
        saveData = Gdx.app.getPreferences("My Preferences");

        ui = new BitmapFont(Gdx.files.internal("font.fnt"));
        ui.setColor(Color.DARK_GRAY);
        healthBars = new BitmapFont();

        timer = new Timer();
        alienSpawnCountdown = 0;
        timeSinceLastFortressRegen = 0;
        timeSinceAlienKilled = -1;

        ArrayList<Vector2> spawnCoordinates = new ArrayList<Vector2>();

        Vector2 firetruck1pos = null;
        Vector2 firetruck2pos = null;

        if (levelNumber == 1) { // Bottom left coord of map --> (33, 212)
            firetruck1pos = new Vector2(33 + 10 * 32, 212 + 6 * 32);
            firetruck2pos = new Vector2(33 + 11 * 32, 212 + 6 * 32);

            timeLimit = 120;
            map = new Texture("level1background.png");

            // Level 1 Obstacles
            obstacles.add(new Entity(new Vector2(257, 628), 64, 64, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(257, 724), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(289, 756), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(257, 820), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(257, 564), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(289, 532), 32, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(513, 532), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(513, 564), 64, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(577, 692), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(577, 724), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(577, 436), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(609, 468), 64, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(737, 404), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(737, 692), 64, 64, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(833, 404), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(929, 404), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1025, 404), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1057, 436), 32, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(1121, 404), 64, 64, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1121, 500), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1121, 532), 64, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(1217, 404), 64, 64, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1217, 532), 64, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(1345, 436), 32, 96, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1377, 468), 32, 96, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(961, 692), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1249, 692), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1249, 628), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1345, 692), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1345, 628), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(33 + 24 * 32, 212 + 22 * 32), 6 * 32, 4 * 32,
                    new Texture("teal.jpg")));

            fireStation = new Entity(new Vector2(33 + 8 * 32, 212 + 4 * 32), 128, 128, new Texture("teal.jpg"));

            // Level 1 Alien Spawn Coordinates
            spawnCoordinates.add(new Vector2(22 * 32, 1044 - 3 * 32));
            spawnCoordinates.add(new Vector2(33 * 32, 1044 - 3 * 32));
            spawnCoordinates.add(new Vector2(24 * 32, 1044 - 6 * 32));
            spawnCoordinates.add(new Vector2(31 * 32, 1044 - 6 * 32));
            spawnCoordinates.add(new Vector2(28 * 32 - 16, 1044 - 8 * 32));

            // Level 1 Fortress
            fortress = new Fortress(new Vector2(33 + 24 * 32, 212 + 22 * 32), 6 * 32, 4 * 32, new Texture("grey.png"),
                    5000, spawnCoordinates, 2.5f);
        }

        else if (levelNumber == 2) {
            firetruck1pos = new Vector2(33 + 2 * 32, 212 + 4 * 32);
            firetruck2pos = new Vector2(33 + 2 * 32, 212 + 5 * 32);

            timeLimit = 120;

            map = new Texture("level2background.png");


            // Level 2 Obstacles
            obstacles.add(new Entity(new Vector2(225, 212), 192, 64, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(225, 308), 224, 128, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(257, 436), 192, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(321, 468), 96, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(449, 340), 352, 128, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(641, 308), 128, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(545, 468), 320, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(801, 436), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(801, 404), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(577, 500), 288, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(609, 532), 224, 160, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(609, 692), 192, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(641, 724), 128, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(705, 756), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(801, 756), 416, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(801, 788), 384, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(769, 820), 384, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(865, 852), 224, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(961, 884), 96, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(833, 724), 448, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(865, 692), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(993, 660), 288, 64, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1057, 596), 224, 64, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1025, 468), 256, 128, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1057, 436), 96, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1313, 468), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1313, 500), 320, 256, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1473, 468), 288, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1505, 436), 256, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1569, 404), 160, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1633, 500), 64, 128, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1697, 500), 64, 96, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1377, 756), 224, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1409, 788), 160, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1409, 820), 128, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(97, 692), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(321, 916), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(353, 948), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(449, 756), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(481, 724), 32, 32, new Texture("teal.jpg")));

            obstacles.add(new Entity(new Vector2(1121, 244), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1153, 276), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1665, 820), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1665, 788), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(897, 372), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(897, 340), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(33 + 36 * 32, 212 + 19 * 32), 4 * 32, 4 * 32,
                    new Texture("teal.jpg")));

            fireStation = new Entity(new Vector2(33 + 1 * 32, 212 + 4 * 32), 64, 128,
                    new Texture("teal.jpg"));

            // Level 2 Alien Spawn Coordinates
            spawnCoordinates.add(new Vector2(33 + 37 * 32, 212 + 15 * 32));
            spawnCoordinates.add(new Vector2(33 + 41 * 32, 212 + 15 * 32));
            spawnCoordinates.add(new Vector2(33 + 33 * 32, 212 + 17 * 32));
            spawnCoordinates.add(new Vector2(33 + 30 * 32, 212 + 20 * 32));
            spawnCoordinates.add(new Vector2(33 + 30 * 32, 212 + 24 * 32));
            spawnCoordinates.add(new Vector2(33 + 44 * 32, 212 + 20 * 32));


            // Level 2 Fortress  36
            fortress = new Fortress(new Vector2(33 + 36 * 32, 212 + 19 * 32), 4 * 32, 4 * 32, new Texture("grey.png"),
                    7500, spawnCoordinates, 4);
        }

        else if (levelNumber == 3) {

            firetruck1pos = new Vector2(33 + 2 * 32, 212 + 4 * 32);
            firetruck2pos = new Vector2(33 + 2 * 32, 212 + 5 * 32);

            timeLimit = 120;

            map = new Texture("level3background.png");

            // Level 3 Obstacles
            obstacles.add(new Entity(new Vector2(737, 244), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(705, 276), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(609, 500), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(577, 532), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(961, 532), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1281, 308), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1281, 340), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1697, 340), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1729, 372), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1665, 500), 32, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(1665, 532), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(673, 724), 64, 32, new Texture("teal.jpg")));
            obstacles.add(new Entity(new Vector2(705, 756), 32, 32, new Texture("teal.jpg")));


            for (int i = 0; i<= 192; i += 32){
                obstacles.add(new Entity(new Vector2(257 + i, 1012 - i), 64, 32, new Texture("teal.jpg")));

            }

            obstacles.add(new Entity(new Vector2(33 + 14 * 32, 212 + 18 * 32), 32, 32, new Texture("teal.jpg")));


            for (int i = 0; i<= 192; i += 32){
                obstacles.add(new Entity(new Vector2(1601 - i, 1012 - i), 64, 32, new Texture("teal.jpg")));

            }
            obstacles.add(new Entity(new Vector2(33 + 43 * 32, 212 + 18 * 32), 32, 32, new Texture("teal.jpg")));

            for (int i = 0; i<= 352; i += 32){
                obstacles.add(new Entity(new Vector2(577 + i, 692 - i), 64, 32, new Texture("teal.jpg")));

            }

            obstacles.add(new Entity(new Vector2(33 + 17 * 32, 212 + 16 * 32), 32, 32, new Texture("teal.jpg")));

            for (int i = 0; i<= 320; i += 32){
                obstacles.add(new Entity(new Vector2(1281 - i, 692 - i), 64, 32, new Texture("teal.jpg")));
            }

            obstacles.add(new Entity(new Vector2(33 + 40 * 32, 212 + 16 * 32), 32, 32, new Texture("teal.jpg")));

            fireStation = new Entity(new Vector2(33, 212), 128, 128, new Texture("teal.jpg"));

            // Level 3 Alien Spawn Coordinates
            spawnCoordinates.add(new Vector2(1696 - 64 * 5, 212 + (GAME_HEIGHT / 2) - 64 / 2));
            spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (GAME_HEIGHT / 2) + 64));
            spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (GAME_HEIGHT / 2) + 160));
            spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (GAME_HEIGHT / 2) - 128));
            spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (GAME_HEIGHT / 2) - 224));
            spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32 + 64 + 32, 212 + (GAME_HEIGHT / 2) - 320));
            spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32 + 64 + 32, 212 + (GAME_HEIGHT / 2) + 256));

            // Level 3 Fortress
            fortress = new Fortress(new Vector2(1696, 212 + (GAME_HEIGHT / 2) - 300 / 2), 100, 300, new Texture("grey.png"),
                    1000, spawnCoordinates, 2);
        }

        firetruck1 = new Firetruck(firetruck1pos, 25, 25,
                new Texture("truck.png"), 100, 200,
                null, 100, 2, 10, 100,
                true, 5);

        firetruck2 = new Firetruck(firetruck2pos, 25, 25,
                new Texture("truck.png"), 50, 200,
                null, 200, 2, 10, 150,
                false, 5);

        firetrucks.add(firetruck1);
        firetrucks.add(firetruck2);

    }

    /**
     * The game logic which is executed due to specific user inputs. Is called in the update method.
     */
    public void handleInput() {

        if (quitGame.mouseInRegion()){
            quitGame.setActive(true);
            if (Gdx.input.isTouched()) {
                Gdx.app.exit();
                System.exit(0);
                }
            }

        else {
            quitGame.setActive(false);
        }

        if (quitLevel.mouseInRegion()){
            quitLevel.setActive(true);
            if (Gdx.input.isTouched()) {
                gameStateManager.pop();
            }
        }

        else {
            quitLevel.setActive(false);
        }

        // Handles input for firetruck attacks
        for (Firetruck firetruck : firetrucks) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && firetruck.isSelected() && firetruck.getCurrentWater() > 0) {
                Projectile drop = new Projectile(new Vector2(firetruck.getPosition().x + firetruck.getWidth() / 2, firetruck.getPosition().y + firetruck.getHeight() / 2), 5, 5,
                        new Texture("lightblue.jpg"), (new Vector2(Gdx.input.getX(), Kroy.HEIGHT - Gdx.input.getY())), 5, firetruck.getDamage(), firetruck.getRange());
                water.add(drop);
                waterShoot.play();
                firetruck.updateCurrentWater(1);
                firetruck.resetTimeSinceAttack();
            }
        }

        // Opens pause menu
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            gameStateManager.push(new OptionState(gameStateManager));
        }

        // Switches active firetruck
        Vector2 mousePos = new Vector2(Gdx.input.getX(), Kroy.HEIGHT - Gdx.input.getY());
        if (Gdx.input.isTouched()) {
            for (Firetruck truck : firetrucks) {
                if (mousePos.x >= (truck.getPosition().x) && mousePos.x <= (truck.getPosition().x + truck.getWidth())
                        && mousePos.y >= (truck.getPosition().y) && mousePos.y <= (truck.getPosition().y
                        + truck.getHeight())) {
                    for (Firetruck clearTruck : firetrucks) {
                        clearTruck.setSelected(false);
                    }
                    truck.setSelected(true);

                }
            }
        }

        // Handles Truck Movement
        if (firetruck1.isSelected()) {
            truckMovement(firetruck1);
        } else if (firetruck2.isSelected()) {
            truckMovement(firetruck2);
        }


        if ((levelLost || levelWon) && Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gameStateManager.set(new LevelSelectState(gameStateManager));
        }
    }

    /**
     * Updates the game logic before the next render() is called
     * @param deltaTime the amount of time which has passed since the last render() call
     */
    @Override
    public void update(float deltaTime) {

        // Calls input handler and updates timer each tick
        handleInput();
        timer.update();

        // Updates aliens and handles automatic attacks
        for (Alien alien : aliens) {
            alien.update();
            alien.truckInRange(firetrucks);
            if (alien.getTimeSinceAttack() >= alien.getAttackCooldown()) {
                if (alien.hasTarget()) {
                    Projectile bullet = new Projectile(new Vector2(alien.getPosition().x + alien.getWidth() / 2, alien.getPosition().y + alien.getHeight() / 2), 5, 5,
                            new Texture("red.png"), (new Vector2(alien.getTarget().getPosition().x, alien.getTarget().getPosition().y)), 5, alien.getDamage());
                    bullets.add(bullet);
                    alien.resetTimeSinceAttack();
                }
            }
            alien.updateTimeSinceAttack(deltaTime);
        }
        alienSpawnCountdown -= deltaTime;
        timeSinceAlienKilled -= deltaTime;


        // Respawns aliens
        if (alienSpawnCountdown <= 0 && timeSinceAlienKilled <= 0) {
            spawnAlien();
            alienSpawnCountdown = fortress.getSpawnRate();
            timeSinceAlienKilled = 0;
        }

        // Handles alien projectile movement and collision
        for (Projectile bullet : new ArrayList<Projectile>(bullets)) {
            bullet.update();
            for (Firetruck truck : new ArrayList<Firetruck>(firetrucks)) {
                if (bullet.hitUnit(truck)) {
                    truck.takeDamage(bullet.getDamage());
                    bullets.remove(bullet);
                    if (truck.getCurrentHealth() == 0) {
                        truck.setSelected(false);
                        firetrucks.remove(truck);
                        destroyedFiretrucks.add(truck);
                    }
                }
            }
        }

        // Refills tank if truck overlaps the  refill location.
        for (Firetruck truck : firetrucks) {
            if (!(truck.getTopRight().y < fireStation.getPosition().y || truck.getPosition().y > fireStation.getTopRight().y ||
                    truck.getTopRight().x < fireStation.getPosition().x || truck.getPosition().x > fireStation.getTopRight().x)) {
                // Would call minigame here
                truck.setCurrentWater(truck.getMaxWater());
            }
        }

        // Handles movement and collision for firetruck projectiles
        for (Projectile drop : new ArrayList<Projectile>(water)) {
            drop.update();
            if (drop.getLength() > drop.getMaxLength()) {
                water.remove(drop);
            }
            for (Alien alien : new ArrayList<Alien>(aliens)) {
                if (drop.hitUnit(alien)) {
                    alien.takeDamage(drop.getDamage());
                    water.remove(drop);
                    if (alien.getCurrentHealth() == 0) {
                        fortress.getAlienPositions().add(alien.getPosition());
                        aliens.remove(alien);
                        timeSinceAlienKilled = fortress.getSpawnRate();
                    }
                }
            }
            if (drop.hitUnit(fortress)) {
                fortress.takeDamage(drop.getDamage());
                if (fortress.getCurrentHealth() == 0) {
                    levelWon = true;
                    saveData.putBoolean(level, true);
                    saveData.flush();
                }
            }
        }

        // Handles game end states
        if (firetrucks.size() == 0 || timer.getTime() > timeLimit) {
            levelLost = true;
        }
        if(timeSinceLastFortressRegen <= 0) {
            fortress.addHealth(10);
            timeSinceLastFortressRegen = 1;
        }
        timeSinceLastFortressRegen -= deltaTime;
    }

    /**
     * Used to draw elements onto the screen.
     * @param spriteBatch a container for all elements which need rendering to the screen.
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Kroy.WIDTH, Kroy.HEIGHT);
        spriteBatch.draw(map, 33, 212, 1856, 832);
        spriteBatch.draw(quitLevel.getTexture(), quitLevel.getPosition().x, quitLevel.getPosition().y,
                quitLevel.getWidth(), quitLevel.getHeight());
        spriteBatch.draw(quitGame.getTexture(), quitGame.getPosition().x, quitGame.getPosition().y,
                quitGame.getWidth(), quitGame.getHeight());

//        spriteBatch.draw(fireStation.getTexture(), fireStation.getPosition().x, fireStation.getPosition().y, fireStation.getWidth(),
//                fireStation.getHeight());

        for (Firetruck truck : firetrucks) {
            spriteBatch.draw(truck.getTexture(), truck.getPosition().x, truck.getPosition().y, truck.getWidth(),
                    truck.getHeight());
            healthBars.draw(spriteBatch, "Water: " + truck.getCurrentWater(), truck.getPosition().x, truck.getPosition().y + truck.getHeight() + 10);
        }

        healthBars.draw(spriteBatch, "HP: " + fortress.getCurrentHealth(), fortress.getPosition().x + 70,
                fortress.getPosition().y + fortress.getHeight() + 20);

        for (Alien alien : aliens) {
            spriteBatch.draw(alien.getTexture(), alien.getPosition().x, alien.getPosition().y, alien.getWidth(),
                    alien.getHeight());
            healthBars.draw(spriteBatch, "HP: " + alien.getCurrentHealth(), alien.getPosition().x, alien.getPosition().y + alien.getHeight() + 10);

        }
        for (Projectile bullet : bullets) {
            spriteBatch.draw(bullet.getTexture(), bullet.getPosition().x, bullet.getPosition().y, bullet.getWidth(),
                    bullet.getHeight());
        }

        for (Projectile drop : water) {
            spriteBatch.draw(drop.getTexture(), drop.getPosition().x, drop.getPosition().y, drop.getWidth(),
                    drop.getHeight());
        }

//        for (Entity obstacle : obstacles) {
//            spriteBatch.draw(obstacle.getTexture(), obstacle.getPosition().x, obstacle.getPosition().y, obstacle.getWidth(),
//                    obstacle.getHeight());
//        }

        timer.drawTime(spriteBatch, ui);
        ui.setColor(Color.WHITE);
        if ((timeLimit - 15) < timer.getTime() && timer.getTime() < (timeLimit - 10)) {
            ui.draw(spriteBatch, "15 seconds remaining!", 150, 1000);
        }
        ui.setColor(Color.DARK_GRAY);
        ui.draw(spriteBatch, "Truck 1 Health: " + Integer.toString(firetruck1.getCurrentHealth()), 70,
                Kroy.HEIGHT - 920);
        ui.draw(spriteBatch, "Truck 2 Health: " + Integer.toString(firetruck2.getCurrentHealth()), 546,
                Kroy.HEIGHT - 920);
        ui.draw(spriteBatch, "Truck 3 Health: N/A", 1023, Kroy.HEIGHT - 920);
        ui.draw(spriteBatch, "Truck 4 Health: N/A", 1499, Kroy.HEIGHT - 920);

        if (levelLost) {
            spriteBatch.draw(new Texture("levelFail.png"), 0, 0);
        }

        if (levelWon) {
            spriteBatch.draw(new Texture("LevelWon.png"), 0, 0);
        }

        spriteBatch.end();
    }

    /**
     * Used to dispose of all textures, music etc. when no longer required to avoid memory leaks
     */
    @Override
    public void dispose() {

    }

    /**
     * Used to call the correct methods to move the trucks position depending on obstacles and which truck is
     * currently selected.
     * @param truck the truck which is currently selected
     */
    public void truckMovement(Firetruck truck) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            truck.setTexture(new Texture("truck.png"));
            boolean obstacleCollision = false;
            if (truck.getPosition().y >= 1043 - truck.getHeight()) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                if (truck.willCollide(obstacle, 3)) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move(3);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            truck.setTexture(new Texture("truckdown.png"));
            boolean obstacleCollision = false;
            if (truck.getPosition().y <= 212) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                if (truck.willCollide(obstacle, 4)) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move(4);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            truck.setTexture(new Texture("truckleft.png"));
            boolean obstacleCollision = false;
            if (truck.getPosition().x <= 33) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                if (truck.willCollide(obstacle, 1)) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move(1);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            truck.setTexture(new Texture("truckright.png"));
            boolean obstacleCollision = false;
            if (truck.getPosition().x >= 1888 - truck.getWidth()) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                System.out.println("truck will collide" + truck.willCollide(obstacle, 2));
                if (truck.willCollide(obstacle, 2)) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move(2);
            }
        }
    }

    /**
     * Used to spawn the Aliens around the fortress by accessing the spawnRate and alienPositions stored within
     * the fortress object.
     */
    public void spawnAlien() {
        Random rand = new Random();
        if (fortress.getAlienPositions().size() > 0) {
            Vector2 coordinate = fortress.getAlienPositions().get(rand.nextInt(fortress.getAlienPositions().size()));
            Alien alien = new Alien(coordinate, 32, 32, new Texture("alien.gif"), 30 + rand.nextInt(60),
                    250, null, 1, 5 + rand.nextInt(15), new Vector2[]{new Vector2(coordinate.x, coordinate.y),
                    new Vector2(coordinate.x, coordinate.y + 30)}, 5);
            aliens.add(alien);
            fortress.getAlienPositions().remove(coordinate);
        }
    }
}

// Used to freeze the game when the level is completed while the notifier pops up but currently hangs the game.
//    public void freezeLevel() {
//        timer.stop();
//        for (Firetruck truck : firetrucks) {
//            truck.setSpeed(0);
//            truck.setDps(0);
//        }
//        for (Alien alien : aliens) {
//            alien.setSpeed(0);
//            alien.setDps(0);
//        }
//
//    }