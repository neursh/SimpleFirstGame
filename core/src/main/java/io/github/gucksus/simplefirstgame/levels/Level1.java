package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.entities.enemies.PopcornEnemy;
import io.github.gucksus.simplefirstgame.entities.enemies.SkullShooterEnemy;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class Level1 extends Level {
    PopcornEnemy popcornEnemy;
    Texture popcornEnemyTexture;
    Texture skullAnimationSheet;
    Texture skullBulletTexture;

    public Level1() {
        super();
        popcornEnemyTexture = new Texture("Enemy/popcornEnemy.png");
        skullAnimationSheet = new Texture("Enemy/skull_animation.png");
        skullBulletTexture = new Texture("Bullet/skull_bullet_texture.png");
    }

    private void addPopcornEnemiesIntoWave(Wave wave) {
//        for (int i = 0; i < wave.totalEnemies; i++) {
//            Enemy enemy = new PopcornEnemy(popcornEnemyTexture, wave.startX, wave.startY);
//            wave.waveEnemyArray.add(enemy);
//            activeEnemies.add(enemy);
//        }
    }

    @Override
    public void enemySpawn(float worldWidth, float worldHeight) {
        waveArray.add(new Wave(activeEnemies, 7, .4f, -3, 9.5f));
        waveArray.add(new Wave(activeEnemies, 7, .4f, -1, 9.5f));
        Wave A1 = waveArray.first();
        Wave A2 = waveArray.peek();
//        addPopcornEnemiesIntoWave(A1);
//        addPopcornEnemiesIntoWave(A2);
//
//        float currentDuration = 3f;
//        A1.moveStraight(3f - popcornEnemy.width / 2, 1.5f, currentDuration, delta);
//        A2.moveStraight(5f - popcornEnemy.width / 2, 1.5f, currentDuration, delta);
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                A1.moveStraight(A1.startX, 11, 2.5f, delta);
//                A2.moveStraight(A2.startX, 11, 2.5f, delta);
//            }
//        }, currentDuration);
//        currentDuration = 2.5f;
        TextureRegion[][] temp = TextureRegion.split(skullAnimationSheet, skullAnimationSheet.getWidth() / 11, skullAnimationSheet.getHeight() / 2);
        Enemy enemy = new SkullShooterEnemy(temp[0][0], skullBulletTexture,4, 4);
        enemy.initializeShootAnimation(temp[0]);
        enemy.initializeDeathAnimation(temp[1]);
        enemy.triggerShootAnimation();
        A1.waveEnemyArray.add(enemy);
        activeEnemies.add(enemy);
        A1.startX = 4;
        A1.startY = 4;
    }

    public void dispose() {
        popcornEnemyTexture.dispose();
        skullAnimationSheet.dispose();
    }
}
