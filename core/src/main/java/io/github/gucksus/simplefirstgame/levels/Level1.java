package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.gucksus.simplefirstgame.entities.Enemy;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.entities.PopcornEnemy;

public class Level1 extends Level {
    Texture popcornEnemyTexture;

    public Level1() {
        super();
        popcornEnemyTexture = new Texture("enemylv1.png");
    }

    public void popcornEnemySpawn(Texture texture ,float iniX, float iniY) {
        enemyArray.add(new PopcornEnemy(texture, iniX, iniY));
    }

    public void enemySpawn() {
        popcornEnemySpawn(popcornEnemyTexture ,4, 8);
    }

    public void enemyUpdate(float delta) {
        for (Enemy enemy: enemyArray) {
            enemy.update(delta);
        }
    }
}
