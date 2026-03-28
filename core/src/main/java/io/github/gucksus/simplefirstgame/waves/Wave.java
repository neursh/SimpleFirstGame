package io.github.gucksus.simplefirstgame.waves;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.entities.Enemy;


public class Wave {
    protected Array <Enemy> activeEnemyArray;
    public Array <Enemy> waveEnemyArray;
    public int totalEnemies;
    public float interval;
    public float startX;
    public float startY;
    public boolean isDone;

    public Wave(Array<Enemy> activeEnemyArray, int totalEnemies, float interval, float startX, float startY) {
        this.activeEnemyArray = activeEnemyArray;
        waveEnemyArray = new Array<>();
        this.totalEnemies = totalEnemies;
        this.interval = interval;
        this.startX = startX;
        this.startY = startY;
    }

    public void enemyUpdateRemoval(float worldWidth, float worldHeight) {
        for (Enemy enemy: waveEnemyArray) {
            enemy.updateStatus(worldWidth, worldHeight);
        }
        for (int i = waveEnemyArray.size - 1; i >= 0; --i) {
            Enemy enemy = waveEnemyArray.get(i);
            if (enemy.isDead){
                activeEnemyArray.removeValue(enemy, true);
            }
        }
    }

    public boolean waveUpdateRemoval(float worldWidth, float worldHeight) {
        for (Enemy enemy: waveEnemyArray) {
            if (enemy.numberOfTimeAllowedOnScreenLeft > 0 || enemy.isInScreenThisFrame(worldWidth, worldHeight))
                return false;
        }
        return true;
    }

    public void moveStraight(float endX, float endY, float duration, float delta){
        // Here the duration is the amount of time it takes for the first enemy to reach the destination.
        float lastStartX = startX;
        float lastStartY = startY;
        startX = endX;
        startY = endY;
        waveEnemyArray.first().isMoving = true;
        waveEnemyArray.first().nextFrameXDifference = (endX - lastStartX) / duration * delta;
        waveEnemyArray.first().nextFrameYDifference = (endY - lastStartY) / duration * delta;
        for (int i = 1; i < waveEnemyArray.size; i++) {
            final int idx = i;
            if (!waveEnemyArray.get(i).isDead){
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Enemy enemy = waveEnemyArray.get(idx);
                        enemy.isMoving = true;
                        enemy.nextFrameXDifference = (endX - lastStartX) / duration * delta;
                        enemy.nextFrameYDifference = (endY - lastStartY) / duration * delta;
                    }
                }, i * interval);
            }
        }
    }

    public void updateEnemyMovingStatus(float delta) {
        for (Enemy enemy: waveEnemyArray) {
            enemy.updatePosition(delta);
        }
    }
}
