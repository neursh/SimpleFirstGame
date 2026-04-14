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
            if (enemy.isDead && enemy.deathAnimation.isAnimationFinished(enemy.stateTime)){ // If the enemy is dead and finished death animation.
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
            if (enemy.numberOfTimeAllowedOnScreenLeft > 0 || enemy.isInScreenThisFrame(worldWidth, worldHeight))
                return false;
        }
        return true;
    }

    /**
     * This method updates enemies' next frame X and Y difference so that they can reach the destination in time.
     * @param endX The destination's X coordinate.
     * @param endY The destination's Y coordinate.
     * @param duration The amount of time for the first enemy of the wave to reach the destination.
     * @param delta The frame delta time.
     */
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

    public void stopAllEnemyMovement() {
        waveEnemyArray.first().isMoving = false;
        waveEnemyArray.first().nextFrameXDifference = 0;
        waveEnemyArray.first().nextFrameYDifference = 0;
        for (int i = 1; i < waveEnemyArray.size; i++) {
            final int idx = i;
            if (!waveEnemyArray.get(i).isDead){
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Enemy enemy = waveEnemyArray.get(idx);
                        enemy.isMoving = false;
                        enemy.nextFrameXDifference = 0;
                        enemy.nextFrameYDifference = 0;
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
