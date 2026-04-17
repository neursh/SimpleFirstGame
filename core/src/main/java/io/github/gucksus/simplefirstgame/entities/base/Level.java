package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;
import io.github.gucksus.simplefirstgame.waves.Wave;


public abstract class Level {
    protected boolean isLevelCompleted = false;
    protected boolean debugMode = false;
    protected float lvTimer = 0;
    DebugRenderer debugRenderer;
    protected Array<Enemy> activeEnemies;
    protected Array<Wave> waveArray;
    protected boolean isLevelStarted;
    protected float delta;
    protected Array<EnemyBullet> enemyBulletArray;
    public Array<Bullet> bulletArray;

    public Level() {
        delta = 69;
        debugRenderer = new DebugRenderer();
        activeEnemies = new Array<>();
        waveArray = new Array<>();
        enemyBulletArray = new Array<>();
        bulletArray = new Array<>();
    }

    public abstract void enemySpawn(float worldWidth, float worldHeight);

    public abstract void enemySpawnDebug(float worldWidth, float worldHeight);

    public void draw(SpriteBatch batch) {
        for (Enemy enemy : activeEnemies)
            if (!enemy.isInvisible) {
                enemy.draw(batch, delta);
            }
        for (EnemyBullet enemyBullet: enemyBulletArray) {
            enemyBullet.sprite.draw(batch);
        }
        for (Bullet basicBullet : bulletArray) {
            basicBullet.sprite.draw(batch);
        }
    }

    public void drawEnemyHitboxAndHurtBox(ShapeRenderer shapeRenderer) {
        for (Enemy enemy : activeEnemies) {
            for (BoxWithOffset hitbox : enemy.hitboxes) {
                debugRenderer.drawHitbox(hitbox.getBox(), shapeRenderer);
            }
            for (BoxWithOffset hurtbox: enemy.hurtboxes) {
                debugRenderer.drawHurtbox(hurtbox.getBox(), shapeRenderer);
            }
        }
        for (EnemyBullet enemyBullet: enemyBulletArray) {
            if (enemyBullet.isCircle) {
                debugRenderer.drawCircleHitbox(enemyBullet.circleHitbox, shapeRenderer);
            } else {
                debugRenderer.drawHitbox(enemyBullet.rectangleHitbox, shapeRenderer);
            }
        }
        for (Bullet bullet : bulletArray) {
            debugRenderer.drawHitbox(bullet.hitbox, shapeRenderer);
        }
    }

    public void update (float delta, float worldWidth, float worldHeight, MainShip mainShip) {
        updateDelta(delta);
        wavesUpdate(delta, worldWidth, worldHeight);
        addEnemyBulletUpdate(mainShip);
        enemyBulletUpdate(delta);
        bulletUpdate(delta, worldHeight);
    }

    public void wavesUpdate(float delta, float worldWidth, float worldHeight) {
        for (int i = waveArray.size - 1; i >= 0; i--) {
            Wave wave = waveArray.get(i);
            wave.update(delta, worldWidth, worldHeight);
            if (wave.isDone) {
                waveArray.removeIndex(i);
            }
        }
    }

    public void updateDelta (float delta) {
        this.delta = delta;
    }

    /**
     * This method is used to trigger start the level only when libGdx is fully stable.
     * @param delta The frame delta time.
     * @param worldWidth The width of the world.
     * @param worldHeight The height of the world.
     */
    public void startLevelIfHaveNotStarted (float delta, float worldWidth, float worldHeight) {
        if (!this.isLevelStarted && Math.abs(this.delta - delta) <= .0001f) {
            if (!debugMode) {
                this.enemySpawn(worldWidth, worldHeight);
            }
            else {
                this.enemySpawnDebug(worldWidth, worldHeight);
            }
            this.isLevelStarted = true;
            System.out.println(delta);
        }
    }

    public void addEnemyBulletUpdate (MainShip mainShip) {
        for (Enemy enemy: activeEnemies) {
            EnemyBullet enemyBullet = enemy.shoot(mainShip);
            if (enemyBullet != null) {
                enemyBulletArray.add(enemyBullet);
            }
        }
    }

    public void enemyBulletUpdate(float delta) {
        for (EnemyBullet enemyBullet: enemyBulletArray) {
            enemyBullet.update(delta);
        }
    }

    /**
     * This method updates bullet position; checks if a bullet is out of screen and removes any bullet that does.
     * @param delta The frame delta time.
     * @param worldHeight The height of the world.
     */
    private void bulletUpdate(float delta, float worldHeight) {
        for (int i = bulletArray.size - 1; i >= 0; i--){
            bulletArray.get(i).update(delta);
        }

        for (int i = bulletArray.size - 1; i >= 0; i--) {
            Sprite currentBulletSprite = bulletArray.get(i).sprite;
            if (currentBulletSprite.getY() > worldHeight) {
                bulletArray.removeIndex(i);
            }
        }
    }

    /**
     * This method runs every time ship takes damage.
     * @param mainShip The ship that takes damage.
     */
    public void mainShipTakeDamage(MainShip mainShip) {
        mainShip.lives -= 1;
        if (mainShip.lives == 0){
            mainShip.isDead = true;
        }
        mainShip.timerSinceLastDamage = 0;
    }

    public void hitboxAndHurtboxLogic(MainShip mainShip) {
        for (int enemyIdx = activeEnemies.size - 1; enemyIdx >= 0; enemyIdx--){
            Enemy currentEnemy = activeEnemies.get(enemyIdx);
            if (currentEnemy.hitboxIntersectWithMainShip(mainShip) && mainShip.timerSinceLastDamage > mainShip.invulnerableDuration && !currentEnemy.isHarmless){
                mainShipTakeDamage(mainShip);
            }
            for (int bulletIdx = bulletArray.size - 1; bulletIdx >= 0; bulletIdx--){
                Bullet currentBullet = bulletArray.get(bulletIdx);
                if (currentEnemy.hurtboxIntersectWithThisBullet(currentBullet) && !currentEnemy.isInvulnerable) {
                    currentEnemy.health -= currentBullet.getDamage();
                    bulletArray.removeIndex(bulletIdx);
                }
            }
        }
        for (EnemyBullet enemyBullet: enemyBulletArray) {
            if (enemyBullet.isCircle) {
                if (Intersector.overlaps(enemyBullet.circleHitbox, mainShip.shipHurtbox)) {
                    mainShipTakeDamage(mainShip);
                }
            } else {
                if (Intersector.overlaps(mainShip.shipHurtbox, enemyBullet.rectangleHitbox)) {
                    mainShipTakeDamage(mainShip);
                }
            }
        }
    }
}
