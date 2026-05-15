package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
import io.github.gucksus.simplefirstgame.waves.Wave;


public abstract class Level {
    protected boolean isLevelCompleted = false;
    protected boolean debugMode = false;
    protected float lvTimer = 0;
    public DebugRenderer debugRenderer;
    protected Array<Enemy> activeEnemies = new Array<>();
    protected Array<Wave> waveArray = new Array<>();
    protected float lastDelta;
    public float worldWidth;
    public float worldHeight;
    protected MainShip mainShip;
    public SpriteBatch batch;
    protected BulletHolder bulletHolder;

    String vertexShader = Gdx.files.internal("Shader/defaultVertex.vert").readString();
    String fragmentShader = Gdx.files.internal("Shader/hitFlashFragment.frag").readString();
    ShaderProgram hitFlashShader = new ShaderProgram(vertexShader, fragmentShader);

    public Level(BulletHolder bulletHolder, MainShip mainShip) {
        lastDelta = 67;
        this.worldWidth = Constants.worldWidth;
        this.worldHeight = Constants.worldHeight;
        this.batch = Constants.batch;
        this.mainShip = mainShip;
        this.debugRenderer = Constants.debugRenderer;
        this.bulletHolder = bulletHolder;
    }

    public abstract void enemySpawn();

    public abstract void enemySpawnDebug();

    void drawEnemy() {
        batch.setShader(hitFlashShader);
        for (Enemy enemy : activeEnemies)
            if (!enemy.isInvisible) {
                if (enemy.getTakeDamageTimer() == 0) {
                    enemy.draw();
                }
            }
        batch.setShader(null);
        for (Enemy enemy : activeEnemies)
            if (!enemy.isInvisible) {
                if (enemy.getTakeDamageTimer() != 0) {
                    enemy.draw();
                }
            }
    }

    public void draw() {
        drawEnemy();
    }

    void drawEnemyDebug() {
        for (Enemy enemy : activeEnemies) {
            enemy.drawDebug();
        }
    }

    public void drawDebug() {
        drawEnemyDebug();
    }

    public void update() {
        updateDelta();
        wavesUpdate();
        updateEnemy();
    }

    void updateEnemy() {
        for (Enemy enemy : activeEnemies) {
            enemy.update();
        }
    }

    public void wavesUpdate() {
        for (int i = waveArray.size - 1; i >= 0; i--) {
            Wave wave = waveArray.get(i);
            wave.update();
            if (wave.isDone) {
                waveArray.removeIndex(i);
            }
        }
    }

    public void updateDelta() {
        lastDelta = Gdx.graphics.getDeltaTime();
    }

    public void dispose() {
        hitFlashShader.dispose();
    }
}
