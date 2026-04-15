package io.github.gucksus.simplefirstgame.waves;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;

public class Wave {
    protected Array <Enemy> activeEnemyArray;
    public Array <Enemy> waveEnemyArray;
    public int totalEnemies;
    /**
     * The interval between updating each enemy in the wave.
     */
    public float interval;
    /**
     * The initial X coordinate before a position update.
     */
    public float startX;
    /**
     * The initial Y coordinate before a position update.
     */
    public float startY;
    public boolean isDone;

    /**
     * Create a new wave of enemy.
     * @param activeEnemyArray The array that stored active enemies, this should be passed down by a level.
     * @param totalEnemies The number of enemy this wave holds.
     * @param interval The interval between updating each enemy in the wave.
     * @param startX The initial X coordinate before a position update.
     * @param startY The initial Y coordinate before a position update.
     */
    public Wave(Array<Enemy> activeEnemyArray, int totalEnemies, float interval, float startX, float startY) {
        this.activeEnemyArray = activeEnemyArray;
        waveEnemyArray = new Array<>();
        this.totalEnemies = totalEnemies;
        this.interval = interval;
        this.startX = startX;
        this.startY = startY;
    }

    /**
     * This method calls for enemies' status update. If the conditions are met, it will remove enemies from activeEnemyArray.
     * @param worldWidth The width of the world.
     * @param worldHeight The height of the world.
     */
    public void enemyUpdateRemoval(float worldWidth, float worldHeight) {
        for (Enemy enemy: waveEnemyArray) {
            enemy.updateStatus(worldWidth, worldHeight);
        }
        for (int i = waveEnemyArray.size - 1; i >= 0; --i) {
            Enemy enemy = waveEnemyArray.get(i);
            if (enemy.getIsDead() && enemy.isDeathAnimationFinished()){ // If the enemy is dead and finished death animation.
                activeEnemyArray.removeValue(enemy, true);
            }
        }
    }

    /**
     * @param worldWidth The width of the world.
     * @param worldHeight The height of the world.
     * @return Whether a wave is good to be removed or not.
     */
    public boolean waveUpdateRemoval(float worldWidth, float worldHeight) {
        for (Enemy enemy: waveEnemyArray) {
            if (enemy.getNumberOfTimeAllowedOnScreenLeft() > 0 || enemy.isInScreenThisFrame(worldWidth, worldHeight)){
                return false;
            }
        }
        return true;
    }

    /**
     * This method updates enemies' next frame X and Y difference so that they can reach the destination in time.
     * @param endX The destination's X coordinate.
     * @param endY The destination's Y coordinate.
     * @param duration The amount of time for the first enemy of the wave to reach the destination.
     * @param delta The frame delta time.
     * @param X The amount of time to delay.
     */
    public void moveAllEnemyStraightAfterXSeconds(float endX, float endY, float duration, float delta, float X){
        // Here the duration is the amount of time it takes for the first enemy to reach the destination.
        float lastStartX = startX;
        float lastStartY = startY;
        startX = endX;
        startY = endY;
        for (int i = 0; i < waveEnemyArray.size; i++) {
            final int idx = i;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Enemy enemy = waveEnemyArray.get(idx);
                    enemy.isMoving = true;
                    enemy.nextFrameXDifference = (endX - lastStartX) / duration * delta;
                    enemy.nextFrameYDifference = (endY - lastStartY) / duration * delta;
                }
            }, X + i * interval);
        }
    }

    public void stopAllEnemyMovementAfterXSeconds(float X) {
        for (int i = 0; i < waveEnemyArray.size; i++) {
            final int idx = i;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Enemy enemy = waveEnemyArray.get(idx);
                    enemy.isMoving = false;
                    enemy.nextFrameXDifference = 0;
                    enemy.nextFrameYDifference = 0;
                }
            }, X + i * interval);
        }
    }

    public void updateEnemyMovingStatus(float delta) {
        for (Enemy enemy: waveEnemyArray) {
            enemy.updatePosition(delta);
        }
    }
}
