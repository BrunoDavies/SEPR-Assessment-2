package com.mygdx.game.sprites;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.util.Timer;
import java.util.TimerTask;
import com.badlogic.gdx.math.Vector3;
import java.util.Random;

public class Fortress extends Unit {

    private int spawnRate;
    private String fortressImg;
    private Texture texture;
    private Timer timer = new Timer();
    private ArrayList<Alien> aliens;

    private ArrayList<Vector2> objectPositions;

     public Fortress(int width, int height, Texture texture, Vector2 position) { // Default constructor
        super(width, height, texture, position);
        this.spawnRate = 5;
        this.fortressImg = "fortress";
    }

    public Fortress(int width, int height, Texture texture, Vector2 position, String img) { // basic fortress
        super(width, height, texture, position);
        this.spawnRate = 5;
        this.fortressImg = img;
    }

    public Fortress(int width, int height, Texture texture, Vector2 position, String img, int spawnRate) {
        super(width, height, texture, position);
        this.spawnRate = spawnRate;
        this.fortressImg = img;
    }

    public int getSpawnRate() {
        return this.spawnRate;
    }

    public void setSpawnRate(int new_spawnRate) {
        this.spawnRate = new_spawnRate;
    }

    public void produceAlien(int spawnRate) {
        while (aliens.length() < 20) {
            timer.schedule(new TimerTask(){
                @Override
                public void run() {

                    while (true) {
                        Random rand = new Random();
                        int x1 = rand.nextInt(TestEntity.getWidth());
                        int y1 = rand.nextInt(TestEntity.getHeight());
                        int x2 = rand.nextInt(TestEntity.getWidth());
                        int y2 = rand.nextInt(TestEntity.getHeight());

                        Vector2 waypoint1 = new Vector2(x1,y1);
                        Vector2 waypoint2 = new Vector2(x2,y2);
                        if (checkWaypoint(waypoint1) && checkWaypoint(waypoint2)) {
                            break
                        }
                    }

                    Vector2[] waypoints = [waypoint1, waypoint2];
                    Alien alien = new Alien(waypoints, null, 10, 10, 0)
                    aliens.add(alien);
                }
            },1000*this.spawnRate,1000*this.spawnRate); // 1000*5=5000 mlsec. i.e. 5 seconds. u can change accordngly
            // given two times, first is for first time to execute this
            // code and second is for interval time
        }
    }

    public void checkWaypoint(Vector2 waypoint1) {
         if (objectPositions.contains(waypoint1)) {
             return false;
         }

         return true;
    }


}

// check alien way points dont over lap
//