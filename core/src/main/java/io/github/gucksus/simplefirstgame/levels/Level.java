package io.github.gucksus.simplefirstgame.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.entities.Enemy;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
import io.github.gucksus.simplefirstgame.waves.Wave;

public abstract class Level {
    public boolean isLevelCompleted = false;
    public float lvTimer = 0;
    DebugRenderer debugRenderer;
    Texture popcornEnemyTexture;
    public Array<Enemy> activeEnemies;
    public Array <Wave> waveArray;

    public Level() {
        debugRenderer = new DebugRenderer();
        activeEnemies = new Array<>();
        waveArray = new Array<>();
    }

    public abstract void enemySpawn(float delta, float worldWidth, float worldHeight);

    public void draw(Batch batch) {
        for (Enemy enemy: activeEnemies) {
            enemy.sprite.draw(batch);
        }
    }

    public void drawEnemyHitboxAndHurtBox(ShapeRenderer shapeRenderer){
        for (Enemy enemy: activeEnemies){
            debugRenderer.drawHitbox(enemy.hitbox, shapeRenderer);
            debugRenderer.drawHurtbox(enemy.hurtbox, shapeRenderer);
        }
    }

    public void waveUpdate(float delta) {
        for (Wave wave: waveArray) {
            wave.enemyUpdateRemoval();
            wave.updateEnemyMovingStatus(delta);
        }
    }

}
