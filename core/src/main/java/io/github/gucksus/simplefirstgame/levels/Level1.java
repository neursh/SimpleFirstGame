package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.entities.enemies.PopcornEnemy;
import io.github.gucksus.simplefirstgame.entities.enemies.SkullShooterEnemy;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class Level1 extends Level {
    Texture popcornEnemyTexture;
    Texture skullAnimationSheet;
    Texture skullBulletTexture;
    PopcornEnemy examplePopcornEnemy;

    public Level1() {
        super();
        popcornEnemyTexture = new Texture("Enemy/popcornEnemy.png");
        skullAnimationSheet = new Texture("Enemy/skull_animation.png");
        skullBulletTexture = new Texture("Bullet/skull_bullet_texture.png");
        TextureRegion staticPopcornTexture = new TextureRegion(popcornEnemyTexture);
        examplePopcornEnemy = new PopcornEnemy(staticPopcornTexture, 67, 67);
        debugMode = true;
    }

    private void addNewWave(int totalEnemy, float interval, float startX, float startY) {
        waveArray.add(new Wave(activeEnemies, totalEnemy, interval, startX, startY));
    }

    private void addPopcornEnemiesIntoWave(Wave wave) {
        for (int i = 0; i < wave.totalEnemies; i++) {
            TextureRegion staticPopcornTexture = new TextureRegion(popcornEnemyTexture);
            Enemy enemy = new PopcornEnemy(staticPopcornTexture, wave.startX, wave.startY);
            wave.waveEnemyArray.add(enemy);
            activeEnemies.add(enemy);
        }
    }

    private void addSkullShooterIntoWave(Wave wave) {
        for (int i = 0; i < wave.totalEnemies; i++) {
            TextureRegion[][] temp = TextureRegion.split(skullAnimationSheet, skullAnimationSheet.getWidth() / 11, skullAnimationSheet.getHeight() / 2);
            Enemy enemy = new SkullShooterEnemy(temp[0][0], skullBulletTexture, wave.startX, wave.startY);
            enemy.initializeShootAnimation(temp[0]);
            enemy.initializeDeathAnimation(temp[1]);
            wave.waveEnemyArray.add(enemy);
            activeEnemies.add(enemy);
        }
    }

    @Override
    public void enemySpawn(float worldWidth, float worldHeight) {

        addNewWave(14, .1f, -3, 9.5f);
        addNewWave(14, .1f, -1, 9.5f);
        Wave A1 = waveArray.first();
        Wave A2 = waveArray.peek();
        addPopcornEnemiesIntoWave(A1);
        addPopcornEnemiesIntoWave(A2);
        A1.moveAllEnemyStraightAfterXSeconds(3f - examplePopcornEnemy.getWidth() / 2, 1.5f, .5f, delta, 0);
        A2.moveAllEnemyStraightAfterXSeconds(5f - examplePopcornEnemy.getWidth() / 2, 1.5f, .5f, delta, 0);
        A1.moveAllEnemyStraightAfterPreviousDuration(A1.startX, 11, .11f, delta);
        A2.moveAllEnemyStraightAfterPreviousDuration(A2.startX, 11, .11f, delta);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                addNewWave(1, 0, 0, 11);
                Wave A3 = waveArray.peek();
                addSkullShooterIntoWave(A3);
                A3.moveAllEnemyStraightAfterXSeconds(0, -10, 15, delta, 0);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        addNewWave( 1, 0, 4, 11);
                        Wave A4 = waveArray.peek();
                        addSkullShooterIntoWave(A4);
                        A4.moveAllEnemyStraightAfterXSeconds(4, -10, 15, delta, 0);
                    }
                }, 1);
            }
        }, 5.5f);


    }

    @Override
    public void enemySpawnDebug(float worldWidth, float worldHeight) {
//        addNewWave(1, 0, 3, 3);
//        Wave A1 = waveArray.peek();
//        addSkullShooterIntoWave(A1);
//        A1.moveAllEnemyStraightAfterXSeconds(1, 7, 1, delta, 0);
        addNewWave(30, .1f, 6, 6);
        addNewWave(1, 0, 5, 5);
        Wave A1 = waveArray.first();
        Wave A1Center = waveArray.peek();
        addPopcornEnemiesIntoWave(A1);
        addSkullShooterIntoWave(A1Center);
        A1Center.moveAllEnemyStraightAfterPreviousDuration(2, 2, 1, delta);
        A1Center.moveAllEnemyStraightAfterPreviousDuration(5, 3, 2, delta);
        A1Center.moveAllEnemyStraightAfterPreviousDuration(1, 5, 1, delta);
        A1Center.stopAllEnemyMovementAfterXSeconds(4);
        A1.moveAllEnemyInCircleAfterXSeconds(A1Center.waveEnemyArray.peek(), 99, 99, 0, false, delta);
    }

    public void dispose() {
        popcornEnemyTexture.dispose();
        skullAnimationSheet.dispose();
        skullBulletTexture.dispose();
    }
}
