package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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

        if (!debugMode) {
            this.enemySpawn();
        } else {
            this.enemySpawnDebug();
        }
    }

    Wave addNewWave(int totalEnemy, float interval, float startX, float startY) {
        Wave wave = new Wave(activeEnemies, totalEnemy, interval, startX, startY, worldWidth,
                worldHeight, this);
        waveArray.add(wave);
        return wave;
    }

    private void addPopcornEnemiesIntoWave(Wave... waves) {
        for (Wave wave : waves) {
            for (int i = 0; i < wave.totalEnemies; i++) {
                TextureRegion staticPopcornTexture = new TextureRegion(popcornEnemyTexture);
                Enemy enemy = new PopcornEnemy(staticPopcornTexture, wave.path.first().x,
                        wave.path.first().y, mainShip, wave);
                enemy.initializeIdleAnimation(new TextureRegion[] {staticPopcornTexture});
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
                TextureRegion[][] splitSkullAnimSheet = TextureRegion.split(skullAnimationSheet,
                        skullAnimationSheet.getWidth() / 11, skullAnimationSheet.getHeight() / 2);
                Enemy enemy = new SkullShooterEnemy(splitSkullAnimSheet[0][0],
                        TextureRegion.split(skullBulletTexture, 32, 32)[0], wave.path.first().x,
                        wave.path.first().y, mainShip, wave);
                enemy.initializeIdleAnimation(new TextureRegion[] {splitSkullAnimSheet[0][0]});
                enemy.initializeShootAnimation(splitSkullAnimSheet[0]);
                enemy.initializeDeathAnimation(splitSkullAnimSheet[1]);
                wave.addEnemy(enemy);
            }
        }
    }

    private void addCarrier() {
        Wave wave = addNewWave(1, 0, 67, 67);
        TextureRegion[][] splitCarrierSheet = TextureRegion.split(carrierTextureSheet,
                carrierTextureSheet.getWidth() / 3, carrierTextureSheet.getHeight() / 3);
        Carrier carrier = new Carrier(splitCarrierSheet[0][0],
                TextureRegion.split(powerUpTextureSheet, 32, 32)[0], worldWidth / 2,
                worldHeight + 1, mainShip, wave);
        carrier.initializeIdleAnimation(splitCarrierSheet[0]);
        carrier.initializeDeathAnimation(splitCarrierSheet[1]);
        wave.addEnemy(carrier);
    }

    @Override
    public void enemySpawn() {

        Wave A1 = addNewWave(10, .2f, -3, 9.5f);
        Wave A2 = addNewWave(10, .2f, -1, 9.5f);
        addPopcornEnemiesIntoWave(A1);
        addPopcornEnemiesIntoWave(A2);
    }

    @Override
    public void enemySpawnDebug() {
        Wave A1 = addNewWave(1, 0, 4, 8);
        Wave A2 = addNewWave(1, 0, 5, 8);
        Wave A3 = addNewWave(1, 0, 6, 8);
        addSkullShooterIntoWave(A1, A2, A3);

        // A1.moveAllEnemyStraight(new Vector2[] {v(1, 8), v(4, 4), v(7, 8)}, 0, 1, 0, 0);
        // A2.moveAllEnemyStraight(new Vector2[] {v(0, 8), v(6, 7), v(5, 1)}, 0, 1, 0, 0);
        // A2.moveAllEnemyInCircle(v(5, 1), v(3, 3), 1, 4, 0, 0, 2);
        addCarrier();
    }

    @Override
    public void dispose() {
        super.dispose();
        skullAnimationSheet.dispose();
        skullBulletTexture.dispose();
    }
}
