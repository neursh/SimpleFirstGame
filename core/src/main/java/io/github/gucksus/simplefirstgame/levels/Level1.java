package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.entities.enemies.ArchEnemy;
import io.github.gucksus.simplefirstgame.entities.enemies.Carrier;
import io.github.gucksus.simplefirstgame.entities.enemies.PopcornEnemy;
import io.github.gucksus.simplefirstgame.entities.enemies.SkullShooterEnemy;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class Level1 extends Level {
    Texture popcornEnemyTexture;
    Texture skullAnimationSheet;
    Texture skullBulletTexture;
    Texture carrierTextureSheet;
    Texture powerUpTextureSheet;

    Vector2 v(float x, float y) {
        return new Vector2(x, y);
    }

    public Level1(BulletHolder bulletHolder, MainShip mainShip) {
        super(bulletHolder, mainShip);
        popcornEnemyTexture = new Texture("Enemy/popcornEnemy.png");
        skullAnimationSheet = new Texture("Enemy/skull_animation.png");
        skullBulletTexture = new Texture("Bullet/skull_bullet_texture.png");
        carrierTextureSheet = new Texture("Mainship/PowerUp/PowerUpCarrier.png");
        powerUpTextureSheet = new Texture("Mainship/PowerUp/PowerUp.png");
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
                Enemy enemy = new PopcornEnemy(staticPopcornTexture, wave.path.first().x,
                        wave.path.first().y, mainShip, wave);
                wave.addEnemy(enemy);
            }
        }
    }

    private void addArch(Wave wave) {
        TextureRegion staticPopcornTexture = new TextureRegion(skullBulletTexture);
        Enemy enemy = new ArchEnemy(staticPopcornTexture, wave.path.first().x, wave.path.first().y,
                mainShip, wave);
        wave.addEnemy(enemy);
    }

    private void addSkullShooterIntoWave(Wave... waves) {
        for (Wave wave : waves) {
            for (int i = 0; i < wave.totalEnemies; i++) {
                TextureRegion[][] temp = TextureRegion.split(skullAnimationSheet,
                        skullAnimationSheet.getWidth() / 11, skullAnimationSheet.getHeight() / 2);
                Enemy enemy = new SkullShooterEnemy(temp[0][0],
                        TextureRegion.split(skullBulletTexture, 32, 32)[0], wave.path.first().x,
                        wave.path.first().y, mainShip, wave);
                enemy.initializeShootAnimation(temp[0]);
                enemy.initializeDeathAnimation(temp[1]);
                wave.addEnemy(enemy);
            }
        }
    }

    private void addCarrier(Wave... waves) {
        for (Wave wave : waves) {
            TextureRegion[][] temp = TextureRegion.split(carrierTextureSheet,
                    carrierTextureSheet.getWidth() / 3, carrierTextureSheet.getHeight() / 3);
            Carrier carrier =
                    new Carrier(temp[0][0], TextureRegion.split(powerUpTextureSheet, 32, 32)[0],
                            worldWidth / 2, worldHeight + 1, mainShip, wave);
            carrier.initializeIdleAnimation(temp[0]);
            carrier.initializeDeathAnimation(temp[1]);
            wave.addEnemy(carrier);
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
    }

    @Override
    public void enemySpawnDebug() {
        addNewWave(15000, .001f, 1, 8);
        Wave A1 = waveArray.first();
        addPopcornEnemiesIntoWave(A1);
        A1.moveAllEnemyStraight(new Vector2[] {v(1, 8), v(4, 4), v(7, 8)}, 0, 1, 0, 1);
    }

    public void dispose() {
        skullAnimationSheet.dispose();
        skullBulletTexture.dispose();
    }
}
