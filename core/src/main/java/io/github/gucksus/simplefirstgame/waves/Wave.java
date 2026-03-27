package io.github.gucksus.simplefirstgame.waves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.entities.Enemy;


public class Wave {
    Texture popcornEnemyTexture;
    protected Array <Enemy> activeEnemyArray;
    public Array <Enemy> waveEnemyArray;
    public int totalEnemies;

    public Wave(Array<Enemy> activeEnemyArray, int totalEnemies) {
        this.activeEnemyArray = activeEnemyArray;
        waveEnemyArray = new Array<>();
        this.totalEnemies = totalEnemies;
        popcornEnemyTexture = new Texture("enemylv1.png");
    }

    public void enemyUpdateRemoval() {
        for (Enemy enemy: waveEnemyArray) {
            enemy.updateStatus();
        }
        for (int i = waveEnemyArray.size - 1; i >= 0; --i) {
            if (waveEnemyArray.get(i).isDead){
                activeEnemyArray.removeValue(waveEnemyArray.get(i), true);
                waveEnemyArray.removeIndex(i);
                totalEnemies--;
            }
        }
    }

    public void moveStraight(float startX, float startY, float endX, float endY, float duration, float delta, float interval){
        waveEnemyArray.first().isMoving = true;
        waveEnemyArray.first().nextFrameXDifference = (endX - startX) / duration * delta;
        waveEnemyArray.first().nextFrameYDifference = (endY - startY) / duration * delta;
        for (int i = 1; i < waveEnemyArray.size; i++) {
            final int idx = i;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Enemy enemy = waveEnemyArray.get(idx);
                    enemy.isMoving = true;
                    enemy.nextFrameXDifference = (endX - startX) / duration * delta;
                    enemy.nextFrameYDifference = (endY - startY) / duration * delta;
                }
            }, i * interval);
        }
    }

    public void updateEnemyMovingStatus(float delta) {
        for (Enemy enemy: waveEnemyArray) {
            enemy.updatePosition(delta);
        }
    }
}
