package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;
import io.github.gucksus.simplefirstgame.waves.Wave;


public abstract class Level {
    protected boolean isLevelCompleted = false;
    protected boolean debugMode = false;
    protected float lvTimer = 0;
    protected DebugRenderer debugRenderer;
    protected Array<Enemy> activeEnemies;
    protected Array<Wave> waveArray;
    protected boolean isLevelStarted;
    protected float lastDelta;
    protected float worldWidth;
    protected float worldHeight;
    protected MainShip mainShip;
    protected SpriteBatch batch;

    public Level(float worldWidth, float worldHeight, SpriteBatch batch, MainShip mainShip, DebugRenderer debugRenderer) {
        lastDelta = 69;
        activeEnemies = new Array<>();
        waveArray = new Array<>();
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.batch = batch;
        this.mainShip = mainShip;
        this.debugRenderer = debugRenderer;
    }

    public abstract void enemySpawn();

    public abstract void enemySpawnDebug();

    public void draw() {
        float delta = Gdx.graphics.getDeltaTime();
        for (Enemy enemy : activeEnemies)
            if (!enemy.isInvisible) {
                enemy.draw();
            }
    }

    public void drawDebug() {
        for (Enemy enemy: activeEnemies) {
            enemy.drawBulletDebug();
        }
        drawEnemyHitboxAndHurtBox();
    }

    public void drawEnemyHitboxAndHurtBox() {
        for (Enemy enemy : activeEnemies) {
            for (BoxWithOffset hitbox : enemy.hitboxes) {
                debugRenderer.drawHitbox(hitbox.getBox());
            }
            for (BoxWithOffset hurtbox: enemy.hurtboxes) {
                debugRenderer.drawHurtbox(hurtbox.getBox());
            }
        }
    }

    public void update() {
        updateDelta();
        wavesUpdate();
        updateEnemy();
    }

    void updateEnemy() {
        for(Enemy enemy: activeEnemies) {
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

    public void updateDelta () {
        this.lastDelta = Gdx.graphics.getDeltaTime();
    }

    /**
     * This method is used to trigger start the level only when libGdx is fully stable.
     */
    public void startLevelIfHaveNotStarted () {
        float delta = Gdx.graphics.getDeltaTime();
        if (!this.isLevelStarted && Math.abs(lastDelta - delta) <= .0001f) {
            if (!debugMode) {
                this.enemySpawn();
            }
            else {
                this.enemySpawnDebug();
            }
            this.isLevelStarted = true;
            System.out.println(delta);
        }
    }

    public void hitboxAndHurtboxLogic() {
        for (int enemyIdx = activeEnemies.size - 1; enemyIdx >= 0; enemyIdx--){
            Enemy currentEnemy = activeEnemies.get(enemyIdx);
            if (currentEnemy.hitboxIntersectWithMainShip(mainShip) && mainShip.timerSinceLastDamage > mainShip.invulnerableDuration && !currentEnemy.isHarmless){
                mainShip.takeDamage();
            }
            for (int bulletIdx = mainShip.bulletArray.size - 1; bulletIdx >= 0; bulletIdx--){
                Bullet currentBullet = mainShip.bulletArray.get(bulletIdx);
                if (currentEnemy.hurtboxIntersectWithThisBullet(currentBullet) && !currentEnemy.isInvulnerable) {
                    currentEnemy.health -= currentBullet.getDamage();
                    mainShip.bulletArray.removeIndex(bulletIdx);
                }
            }
            currentEnemy.damageShip();
        }
    }
}
