package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.misc.Timer;
import com.mygdx.game.Kroy;
import com.mygdx.game.sprites.*;
import sun.util.locale.StringTokenIterator;

import java.util.ArrayList;
import java.util.Random;

public class PlayState extends State {

    private final float gameWidth = 1856;
    private final float gameHeight = 832;

    private Texture background;
    private boolean levelFailed;
    private boolean levelWon;
    private Preferences settings;
    private Timer timer;

    private boolean winCondition;

    private Entity refillSquare;

    private Firetruck truck1;
    private Firetruck truck2;

    private Fortress minster;

    private float alienSpawnCountdown;
    private float timeSinceKill;

    public ArrayList<Entity> obstacles = new ArrayList<Entity>();
    public ArrayList<Firetruck> trucks = new ArrayList<Firetruck>();
    public ArrayList<Firetruck> destroyedTrucks = new ArrayList<Firetruck>();
    public ArrayList<Alien> aliens = new ArrayList<Alien>();
    private ArrayList<Projectile> bullets = new ArrayList<Projectile>();
    private ArrayList<Projectile> water = new ArrayList<Projectile>();
    private BitmapFont ui;
    private BitmapFont healthBars;

    public PlayState(GameStateManager gsm, int level) {
        super(gsm);
        background = new Texture("LevelProportions.png");
        levelFailed = false;
        levelWon = false;
        settings = Gdx.app.getPreferences("My Preferences");
        timer = new Timer();
        ui = new BitmapFont(Gdx.files.internal("font.fnt"));
        healthBars = new BitmapFont();
        winCondition = false;

        // Level Obstacles
        refillSquare = new Entity(new Vector2(33, 212), 128, 128, new Texture("teal.jpg"));
        // Firetrucks
        truck1 = new Firetruck(new Vector2(50, 550), 64, 64, new Texture("truckthin.png"), 100, 2,
                 null, 250, 10, 10, 100,
                true, 5);
        truck2 = new Firetruck(new Vector2(300, 550), 64, 64, new Texture("truckthin.png"), 100, 2,
                 null, 250, 10, 10, 100,
                false, 5);
        trucks.add(truck1);
        trucks.add(truck2);

        // Aliens
        ArrayList<Vector2> spawnCoordinates = new ArrayList<Vector2>();
        spawnCoordinates.add(new Vector2(1696 - 64 * 5, 212 + (gameHeight / 2) - 64 / 2));
        spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (gameHeight / 2) + 64));
        spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (gameHeight / 2) + 160));
        spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (gameHeight / 2) - 128 ));
        spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32, 212 + (gameHeight / 2) - 224));
        spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32 + 64 + 32, 212 + (gameHeight / 2) - 320));
        spawnCoordinates.add(new Vector2(1696 - 64 * 5 + 64 + 32 + 64 + 32, 212 + (gameHeight / 2) +  256));

        // Fortress
        minster = new Fortress(new Vector2(1696, 212 + (gameHeight / 2) - 300 / 2), 100, 300, new Texture("grey.png"),
                10000, spawnCoordinates, 2);
        alienSpawnCountdown = minster.getSpawnRate();
        ui.setColor(Color.DARK_GRAY);
        timeSinceKill = -1;
    }

    public void handleInput() {

        // Handles input for firetruck attacks
        for(Firetruck firetruck : trucks) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && firetruck.isSelected() && firetruck.getCurrentWater() > 0) {
                Projectile drop = new Projectile(new Vector2(firetruck.getPosition().x + firetruck.getWidth() / 2, firetruck.getPosition().y + firetruck.getHeight() / 2), 5, 5,
                        new Texture("lightblue.jpg"), (new Vector2(Gdx.input.getX(), Kroy.HEIGHT - Gdx.input.getY())), 5);
                water.add(drop);
                firetruck.updateCurrentWater(1);
                firetruck.resetTimeSinceAttack();
            }
        }

        // Test hotkeys
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            freezeLevel();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            gameStateManager.push(new MenuState(gameStateManager));
        }

        // Opens pause menu
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            gameStateManager.push(new OptionState(gameStateManager));
        }

        // Switches active firetruck
        Vector2 mousePos = new Vector2(Gdx.input.getX(), Kroy.HEIGHT - Gdx.input.getY());
        if (Gdx.input.isTouched()) {
            for (Firetruck truck : trucks) {
                if (mousePos.x >= (truck.getPosition().x) && mousePos.x <= (truck.getPosition().x + truck.getWidth())
                        && mousePos.y >= (truck.getPosition().y) && mousePos.y <= (truck.getPosition().y
                        + truck.getHeight())) {
                    for (Firetruck clearTruck : trucks) {
                        clearTruck.setSelected(false);
                    }
                    truck.setSelected(true);
                }
            }
        }

        // Handles Truck Movement
        if (truck1.isSelected()) {
            truckMovement(truck1);
        }
        else if (truck2.isSelected()){
            truckMovement(truck2);
        }
    }

    @Override
    public void update(float dt) {

        // Calls input handler and updates timer each tick
        handleInput();
        timer.update();

        // Updates aliens and handles automatic attacks
        for (Alien alien : aliens) {
            alien.update();
            alien.truckInAttackRange(trucks);
            if (alien.getTimeSinceAttack() >= alien.getAttackCooldown()) {
                if (alien.hasTarget()) {
                    Projectile bullet = new Projectile(new Vector2(alien.getPosition().x + alien.getWidth() / 2, alien.getPosition().y + alien.getHeight() / 2), 5, 5,
                            new Texture("black.jpg"), (new Vector2(alien.getTarget().getPosition().x + 45, alien.getTarget().getPosition().y + 50)), 5);
                    bullets.add(bullet);
                    alien.resetTimeSinceAttack();
                }
            }
            alien.updateTimeSinceAttack(dt);
        }
        alienSpawnCountdown -= dt;
        timeSinceKill -= dt;



        // Respawns aliens
        if (alienSpawnCountdown <= 0 && timeSinceKill <= 0) {
            produceAlien();
            alienSpawnCountdown = minster.getSpawnRate();
            timeSinceKill = 0;
        }

        // Handles alien projectile movement and collision
        for (Projectile bullet : new ArrayList<Projectile>(bullets)) {
            bullet.update();
            for(Firetruck truck : new ArrayList<Firetruck>(trucks)) {
                if (bullet.hitUnit(truck)) {
                    truck.takeDamage(10);
                    bullets.remove(bullet);
                    if(truck.getCurrentHealth() == 0) {
                        trucks.remove(truck);
                        destroyedTrucks.add(truck);
                    }
                }
            }
        }

        // Refills tank if truck overlaps the  refill location.
        for (Firetruck truck : trucks) {
            if (!(truck.getTopRight().y < refillSquare.getPosition().y || truck.getPosition().y > refillSquare.getTopRight().y ||
                    truck.getTopRight().x < refillSquare.getPosition().x || truck.getPosition().x > refillSquare.getTopRight().x)) {
                // Would call minigame here
                truck.setCurrentWater(truck.getMaxWater());
            }
        }

        // Handles movement and collision for firetruck projectiles
        for (Projectile drop : new ArrayList<Projectile>(water)) {
            drop.update();
            for(Alien alien : new ArrayList<Alien>(aliens)) {
                if (drop.hitUnit(alien)) {
                    alien.takeDamage(2);
                    water.remove(drop);
                    if(alien.getCurrentHealth() == 0) {
                        minster.getAlienPositions().add(alien.getPosition());
                        aliens.remove(alien);
                        timeSinceKill = minster.getSpawnRate();
                    }
                }
            }
            if (drop.hitUnit(minster)) {
                minster.takeDamage(2);
                if(minster.getCurrentHealth() == 0) {
                    settings.putBoolean("level1", true);
                    gameStateManager.set(new EndState(gameStateManager, true));
                }
            }
        }

        // Handles game end states
        if (trucks.size() == 0 || timer.getDeltaTime() > 60) {
            freezeLevel();
            levelFailed = true;
        }

        minster.addHealth(1);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Kroy.WIDTH, Kroy.HEIGHT);
        spriteBatch.draw(refillSquare.getTexture(), refillSquare.getPosition().x, refillSquare.getPosition().y, refillSquare.getWidth(),
                refillSquare.getHeight());

        for (Firetruck truck : trucks) {
            spriteBatch.draw(truck.getTexture(), truck.getPosition().x, truck.getPosition().y, truck.getWidth(),
                    truck.getHeight());
            healthBars.draw(spriteBatch, "Water: " + truck.getCurrentWater(), truck.getPosition().x, truck.getPosition().y + truck.getHeight() + 10);
        }

        spriteBatch.draw(minster.getTexture(), minster.getPosition().x, minster.getPosition().y, minster.getWidth(),
                minster.getHeight());
        healthBars.draw(spriteBatch, "HP: " + minster.getCurrentHealth(), minster.getPosition().x, minster.getPosition().y + minster.getHeight() + 10);

        for (Alien alien : aliens){
            spriteBatch.draw(alien.getTexture(), alien.getPosition().x, alien.getPosition().y, alien.getWidth(),
                    alien.getHeight());
            healthBars.draw(spriteBatch, "HP: " + alien.getCurrentHealth(), alien.getPosition().x, alien.getPosition().y + alien.getHeight() + 10);

        }
        for(Projectile bullet : bullets) {
            spriteBatch.draw(bullet.getTexture(), bullet.getPosition().x, bullet.getPosition().y, bullet.getWidth(),
                    bullet.getHeight());
        }

        for(Projectile drop : water) {
            spriteBatch.draw(drop.getTexture(), drop.getPosition().x, drop.getPosition().y, drop.getWidth(),
                    drop.getHeight());
        }
        for(Entity obstacle : obstacles) {
            spriteBatch.draw(obstacle.getTexture(), obstacle.getPosition().x, obstacle.getPosition().y, obstacle.getWidth(),
                    obstacle.getHeight());
        }

        timer.drawTime(spriteBatch, ui);
        ui.draw(spriteBatch, "Truck 1 Health: " + Integer.toString(truck1.getCurrentHealth()), 70,
                Kroy.HEIGHT - 920);
        ui.draw(spriteBatch, "Truck 2 Health: " + Integer.toString(truck2.getCurrentHealth()), 546,
                Kroy.HEIGHT - 920);
        ui.draw(spriteBatch, "Truck 3 Health: N/A", 1023, Kroy.HEIGHT - 920);
        ui.draw(spriteBatch, "Truck 4 Health: N/A", 1499, Kroy.HEIGHT - 920);

        if(levelFailed) {
            spriteBatch.draw(new Texture("levelFail.png"), 480, 270);
        }
        spriteBatch.end();
    }

    public void truckMovement(Firetruck truck) {
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            boolean obstacleCollision = false;
            if(truck.getPosition().y >= 1043 - truck.getHeight()) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                if (truck.willCollide(obstacle, "up")) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move("up");
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            boolean obstacleCollision = false;
            if(truck.getPosition().y <= 212) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                if (truck.willCollide(obstacle, "down")) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move("down");
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            boolean obstacleCollision = false;
            if(truck.getPosition().x <= 33) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                if (truck.willCollide(obstacle, "left")) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move("left");
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            boolean obstacleCollision = false;
            if(truck.getPosition().x >= 1888 - truck.getWidth()) {
                obstacleCollision = true;
            }
            for (Entity obstacle : obstacles) {
                if (truck.willCollide(obstacle, "right")) {
                    obstacleCollision = true;
                }
            }
            if (!obstacleCollision) {
                truck.move("right");
            }
        }
    }


    public void freezeLevel(){
        timer.stop();
        for (Firetruck truck : trucks){
            truck.setSpeed(0);
            truck.setDps(0);
        }
        for (Alien alien : aliens){
            alien.setSpeed(0);
            alien.setDps(0);
        }
    }

    public void produceAlien() {
        Random rand = new Random();
        if (minster.getAlienPositions().size() > 0) {
            Vector2 coordinate = minster.getAlienPositions().get(rand.nextInt(minster.getAlienPositions().size()));
            Alien alien = new Alien(coordinate, 64, 64, new Texture("alien.png"), 100, 1000,
                    null, 1, 10, 10, new Vector2[]{new Vector2(coordinate.x, coordinate.y),
                    new Vector2(coordinate.x, coordinate.y + 30)}, 5);
            aliens.add(alien);
            minster.getAlienPositions().remove(coordinate);
        }
        }

    @Override
    public void dispose() {

    }
}