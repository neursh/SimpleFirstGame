package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.entities.enemies.PopcornEnemy;
import io.github.gucksus.simplefirstgame.entities.enemies.SkullShooterEnemy;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class Level1 extends Level {
    Texture popcornEnemyTexture;
    Texture skullAnimationSheet;
    Texture skullBulletTexture;
    PopcornEnemy examplePopcornEnemy;

    public Level1(Constants constants, BulletHolder bulletHolder, MainShip mainShip) {
        super(constants, bulletHolder, mainShip);
        popcornEnemyTexture = new Texture("Enemy/popcornEnemy.png");
        skullAnimationSheet = new Texture("Enemy/skull_animation.png");
        skullBulletTexture = new Texture("Bullet/skull_bullet_texture.png");
        debugMode = true;
    }

    private void addNewWave(int totalEnemy, float interval, float startX, float startY) {
        waveArray.add(new Wave(activeEnemies, totalEnemy, interval, startX, startY, worldWidth,
                worldHeight, this));
    }

    private void addPopcornEnemiesIntoWave(Wave... waves) {
        for (Wave wave : waves) {
            for (int i = 0; i < wave.totalEnemies; i++) {
                TextureRegion staticPopcornTexture = new TextureRegion(popcornEnemyTexture);
                Enemy enemy = new PopcornEnemy(staticPopcornTexture, wave.startPoint.x,
                        wave.startPoint.y, mainShip, wave);
                wave.addEnemy(enemy);
            }
        }
    }

    private void addSkullShooterIntoWave(Wave... waves) {
        for (Wave wave : waves) {
            for (int i = 0; i < wave.totalEnemies; i++) {
                TextureRegion[][] temp = TextureRegion.split(skullAnimationSheet,
                        skullAnimationSheet.getWidth() / 11, skullAnimationSheet.getHeight() / 2);
                Enemy enemy = new SkullShooterEnemy(temp[0][0], skullBulletTexture,
                        wave.startPoint.x, wave.startPoint.y, mainShip, wave);
                enemy.initializeShootAnimation(temp[0]);
                enemy.initializeDeathAnimation(temp[1]);
                wave.addEnemy(enemy);
            }
        }
    }

    @Override
    public void enemySpawn() {

        // addNewWave(10, .2f, -3, 9.5f);
        // addNewWave(10, .2f, -1, 9.5f);
        // Wave A1 = waveArray.first();
        // Wave A2 = waveArray.peek();
        // addPopcornEnemiesIntoWave(A1);
        // addPopcornEnemiesIntoWave(A2);
        // A1.moveAllEnemyStraight(3f - examplePopcornEnemy.getWidth() / 2, 1.5f, 2f,
        // float.ofSeconds(0));
        // A2.moveAllEnemyStraight(5f - examplePopcornEnemy.getWidth() / 2, 1.5f, 2f,
        // float.ofSeconds(0));
        // A1.moveAllEnemyStraightAfterPreviousTasks(A1.startX, 11, 2f);
        // A2.moveAllEnemyStraightAfterPreviousTasks(A2.startX, 11, 2f);
        //
        // Timer.schedule(new Timer.Task() {
        // @Override
        // public void run() {
        // addNewWave(1, 0, 0, 11);
        // Wave A3 = waveArray.peek();
        // addSkullShooterIntoWave(A3);
        // A3.moveAllEnemyStraight(0, -10, 15, float.ofSeconds(0));
        // Timer.schedule(new Timer.Task() {
        // @Override
        // public void run() {
        // addNewWave( 1, 0, 4, 11);
        // Wave A4 = waveArray.peek();
        // addSkullShooterIntoWave(A4);
        // A4.moveAllEnemyStraight(4, -10, 15, float.ofSeconds(0));
        // }
        // }, 1);
        // }
        // }, 5.5f);
        //
        // Timer.schedule(new Timer.Task() {
        // @Override
        // public void run() {
        // addNewWave(1, 0, -2, 7);
        // Wave A1C = waveArray.peek();
        // addNewWave(10, .2f, -1, 6);
        // Wave A1 = waveArray.peek();
        // addNewWave(1, 0, 13, 5);
        // Wave A2C = waveArray.peek();
        // addNewWave(10, .2f, 67, 67);
        // Wave A2 = waveArray.peek();
        // addSkullShooterIntoWave(A1C);
        // addPopcornEnemiesIntoWave(A1);
        // A1C.moveAllEnemyStraightAfterPreviousTasks(12, A1C.startY, 3);
        // A1.moveAllEnemyInCircleAfterXSeconds(A1C.waveEnemyArray.peek(), 99, 99, 0, false);
        // }
        // }, 9.5f);
    }

    @Override
    public void enemySpawnDebug() {
        addNewWave(1, 0, 7, 7);
        Wave A1 = waveArray.peek();
        addSkullShooterIntoWave(A1);
        A1.moveAllEnemyStraight(3, 3, 1);
        A1.moveAllEnemyInCircle(new Vector2(4, 11/2f), 4, 3, false);
    }

    public void dispose() {
        popcornEnemyTexture.dispose();
        skullAnimationSheet.dispose();
        skullBulletTexture.dispose();
    }
}
