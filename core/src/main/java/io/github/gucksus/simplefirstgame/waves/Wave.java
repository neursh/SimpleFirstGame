package io.github.gucksus.simplefirstgame.waves;

import com.badlogic.gdx.math.Vector2;
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
    Vector2 centerPoint;
    float revolution;
    float previousDuration;
    public movingType currentMovingType;
    public enum movingType {Straight, Circle}

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
        currentMovingType = movingType.Straight;
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

    public void updatePosition(float delta) {
        switch (currentMovingType) {
            case Straight:
                moveStraight();
                break;
            case Circle:
                break;
        }
    }

    void moveStraight() {
        for(Enemy enemy: waveEnemyArray) if (enemy.isMoving) {
            enemy.sprite.translate(enemy.nextFrameXDifference, enemy.nextFrameYDifference);
            enemy.updateEnemyHitboxAndHurtboxWhenMoved();
        }
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
        previousDuration = duration;
        for (int i = 0; i < waveEnemyArray.size; i++) {
            final int idx = i;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Enemy enemy = waveEnemyArray.get(idx);
                    enemy.isMoving = true;
                    currentMovingType = movingType.Straight;
                    enemy.nextFrameXDifference = (endX - lastStartX) / duration * delta;
                    enemy.nextFrameYDifference = (endY - lastStartY) / duration * delta;
                }
            }, X + i * interval);
        }
        stopAllEnemyMovementAfterXSeconds(duration);
    }

    public void moveAllEnemyStraightAfterPreviousDuration(float endX, float endY, float duration, float delta) {
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
                    currentMovingType = movingType.Straight;
                    enemy.nextFrameXDifference = (endX - lastStartX) / duration * delta;
                    enemy.nextFrameYDifference = (endY - lastStartY) / duration * delta;
                }
            }, previousDuration + i * interval);
        }
        previousDuration = duration;
//        stopAllEnemyMovementAfterXSeconds(duration);
    }

    public void moveAllEnemyInCircleAfterXSeconds(Vector2 center, float revolutionNum, float duration, float X, boolean clockwise) {
        for (int i = 0; i < waveEnemyArray.size; i++) {
            final int idx = i;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Enemy enemy = waveEnemyArray.get(idx);
                    enemy.isMoving = true;
                }
            }, X + i * interval);
        }
    }

    public void stopAllEnemyMovementAfterXSeconds(float X) {
        com.badlogic.gdx.graphics.g2d.Sprite sprite = waveEnemyArray.first().getSprite();
        startX = sprite.getX();
        startY = sprite.getY();
        for (int i = 0; i < waveEnemyArray.size; i++) {
            Enemy enemy = waveEnemyArray.get(i);
            enemy.isMoving = false;
        }
    }
}
