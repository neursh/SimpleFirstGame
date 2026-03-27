package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.entities.Enemy;
import io.github.gucksus.simplefirstgame.entities.PopcornEnemy;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class Level1 extends Level {

    public Level1() {
        super();
        popcornEnemyTexture = new Texture("enemylv1.png");
    }

    public void popcornEnemySpawn(Texture texture ,float iniX, float iniY) {
        activeEnemies.add(new PopcornEnemy(popcornEnemyTexture, iniX, iniY));
    }

    private void addPopcornEnemiesIntoWave(Wave wave, float startX, float startY) {
        for (int i = 0; i < wave.totalEnemies; i++) {
            Enemy enemy = new PopcornEnemy(popcornEnemyTexture, startX, startY);
            wave.waveEnemyArray.add(enemy);
            activeEnemies.add(enemy);
        }
    }

    @Override
    public void enemySpawn(float delta, float worldWidth, float worldHeight) {
        waveArray.add(new Wave(activeEnemies, 5));
        addPopcornEnemiesIntoWave(waveArray.first(), 0, 10);

        waveArray.first().moveStraight(0, 10, 3, 3, 3, delta, 1f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                waveArray.first().moveStraight(3, 3, 0, 10, 3, delta, 1f);
            }
        }, 3f);
    }
}
