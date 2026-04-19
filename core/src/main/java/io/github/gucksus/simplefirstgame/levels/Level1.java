package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.entities.enemies.PopcornEnemy;
import io.github.gucksus.simplefirstgame.entities.enemies.SkullShooterEnemy;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class Level1 extends Level {
    Texture popcornEnemyTexture;
    Texture skullAnimationSheet;
    Texture skullBulletTexture;
    PopcornEnemy examplePopcornEnemy;

    public Level1(float worldWidth, float worldHeight, SpriteBatch batch, MainShip mainShip, DebugRenderer debugRenderer) {
        super(worldWidth, worldHeight, batch, mainShip, debugRenderer);
        popcornEnemyTexture = new Texture("Enemy/popcornEnemy.png");
        skullAnimationSheet = new Texture("Enemy/skull_animation.png");
        skullBulletTexture = new Texture("Bullet/skull_bullet_texture.png");
        TextureRegion staticPopcornTexture = new TextureRegion(popcornEnemyTexture);
        examplePopcornEnemy = new PopcornEnemy(staticPopcornTexture, 67, 67, worldWidth, worldHeight, mainShip, batch, debugRenderer);
        debugMode = true;
    }

    private void addNewWave(int totalEnemy, float interval, float startX, float startY) {
        waveArray.add(new Wave(activeEnemies, totalEnemy, interval, startX, startY, worldWidth, worldHeight));
    }

    private void addPopcornEnemiesIntoWave(Wave... waves) {
        for (Wave wave: waves) {
            for (int i = 0; i < wave.totalEnemies; i++) {
                TextureRegion staticPopcornTexture = new TextureRegion(popcornEnemyTexture);
                Enemy enemy = new PopcornEnemy(staticPopcornTexture, wave.startX, wave.startY, worldWidth, worldHeight, mainShip, batch, debugRenderer);
                wave.waveEnemyArray.add(enemy);
                activeEnemies.add(enemy);
            }
        }
    }

    private void addSkullShooterIntoWave(Wave... waves) {
        for (Wave wave: waves) {
            for (int i = 0; i < wave.totalEnemies; i++) {
                TextureRegion[][] temp = TextureRegion.split(skullAnimationSheet, skullAnimationSheet.getWidth() / 11, skullAnimationSheet.getHeight() / 2);
                Enemy enemy = new SkullShooterEnemy(temp[0][0], skullBulletTexture, wave.startX, wave.startY, worldWidth, worldHeight, mainShip, batch, debugRenderer);
                enemy.initializeShootAnimation(temp[0]);
                enemy.initializeDeathAnimation(temp[1]);
                wave.waveEnemyArray.add(enemy);
                activeEnemies.add(enemy);
            }
        }
    }

    @Override
    public void enemySpawn() {

        addNewWave(10, .2f, -3, 9.5f);
        addNewWave(10, .2f, -1, 9.5f);
        Wave A1 = waveArray.first();
        Wave A2 = waveArray.peek();
        addPopcornEnemiesIntoWave(A1);
        addPopcornEnemiesIntoWave(A2);
        A1.moveAllEnemyStraightAfterXSeconds(3f - examplePopcornEnemy.getWidth() / 2, 1.5f, 2f, lastDelta, 0);
        A2.moveAllEnemyStraightAfterXSeconds(5f - examplePopcornEnemy.getWidth() / 2, 1.5f, 2f, lastDelta, 0);
        A1.moveAllEnemyStraightAfterPreviousDuration(A1.startX, 11, 2f, lastDelta);
        A2.moveAllEnemyStraightAfterPreviousDuration(A2.startX, 11, 2f, lastDelta);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                addNewWave(1, 0, 0, 11);
                Wave A3 = waveArray.peek();
                addSkullShooterIntoWave(A3);
                A3.moveAllEnemyStraightAfterXSeconds(0, -10, 15, lastDelta, 0);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        addNewWave( 1, 0, 4, 11);
                        Wave A4 = waveArray.peek();
                        addSkullShooterIntoWave(A4);
                        A4.moveAllEnemyStraightAfterXSeconds(4, -10, 15, lastDelta, 0);
                    }
                }, 1);
            }
        }, 5.5f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                addNewWave(1, 0, -2, 7);
                Wave A1C = waveArray.peek();
                addNewWave(10, .2f, -1, 6);
                Wave A1 = waveArray.peek();
                addNewWave(1, 0, 13, 5);
                Wave A2C = waveArray.peek();
                addNewWave(10, .2f, 67, 67);
                Wave A2 = waveArray.peek();
                addSkullShooterIntoWave(A1C);
                addPopcornEnemiesIntoWave(A1);
                A1C.moveAllEnemyStraightAfterPreviousDuration(12, A1C.startY, 3, lastDelta);
                A1.moveAllEnemyInCircleAfterXSeconds(A1C.waveEnemyArray.peek(), 99, 99, 0, false, lastDelta);
            }
        }, 9.5f);
    }

    @Override
    public void enemySpawnDebug() {
        addNewWave(1, 0, -5, 7);
        Wave A1C = waveArray.peek();
        addNewWave(20, .1f, -4, 7);
        Wave A1 = waveArray.peek();
        addNewWave(1, 0, 13, 5);
        Wave A2C = waveArray.peek();
        addNewWave(10, .2f, 67, 67);
        Wave A2 = waveArray.peek();
        addSkullShooterIntoWave(A1C);
        addPopcornEnemiesIntoWave(A1);
        A1C.moveAllEnemyStraightAfterXSeconds(12, A1C.startY, 20, lastDelta, 2);
        A1.moveAllEnemyInCircleAfterXSeconds(A1C.waveEnemyArray.peek(), 99, 150, 0, false, lastDelta);
    }

    public void dispose() {
        popcornEnemyTexture.dispose();
        skullAnimationSheet.dispose();
        skullBulletTexture.dispose();
    }
}
