package io.github.gucksus.simplefirstgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.gucksus.simplefirstgame.entities.Bullet;
import io.github.gucksus.simplefirstgame.entities.Enemy;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.levels.Level;
import io.github.gucksus.simplefirstgame.levels.Level1;
import io.github.gucksus.simplefirstgame.tools.ScrollingBackground;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    // Sprites.
    private SpriteBatch batch;
    FitViewport viewport;
    float worldHeight;
    float worldWidth;
    ScrollingBackground scrollingBackground;
    MainShip mainShip;
    // Levels.
    Level currentLevel;
    Level1 level1;
    // This is for drawing hitbox and hurtbox.
    ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        // Initialize sprite batch and viewport.
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(8,11);
        worldHeight = viewport.getWorldHeight();
        worldWidth = viewport.getWorldWidth();
        scrollingBackground = new ScrollingBackground(viewport.getWorldHeight());
        mainShip = new MainShip(4, 0, 1, 1, .1f);
        // Level can be changed by changing currentLevel to desired level.
        level1 = new Level1();
        currentLevel = level1;
        currentLevel.enemySpawn(1/60f, worldWidth, worldHeight);
    }

    @Override
    public void resize(int width, int height) {
        // CenterCamera must be true to set origin at the bottom left.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // In case delta jump too high.
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1/55f);
        currentLevel.waveUpdate(delta);
        mainShip.update(delta, worldWidth, worldHeight);
        scrollingBackground.backgroundUpdate(delta);
        hitboxAndHurtboxLogic();
        draw();
    }

    private void hitboxAndHurtboxLogic() {
        Array<Bullet> bulletArray = mainShip.bulletArray;
        Array<Enemy> enemyArray = currentLevel.activeEnemies;
        for (int enemyIdx = enemyArray.size - 1; enemyIdx >= 0; enemyIdx--){
            Enemy currentEnemy = enemyArray.get(enemyIdx);
            if (Intersector.overlaps(mainShip.shipHurtbox, currentEnemy.hitbox) && mainShip.timerSinceLastDamage > mainShip.invulnerableDuration){
                mainShip.lives -= 1;
                if (mainShip.lives == 0){
                    mainShip.isDead = true;
                }
                mainShip.timerSinceLastDamage = 0;
            }
            for (int bulletIdx = bulletArray.size - 1; bulletIdx >= 0; bulletIdx--){
                Bullet currentBullet = bulletArray.get(bulletIdx);
                if (currentBullet.hitbox.overlaps(currentEnemy.hurtbox) && !currentEnemy.isInvulnerable) {
                    currentEnemy.health -= currentBullet.damage;
                    bulletArray.removeIndex(bulletIdx);
                }
            }
        }
    }

    private void draw() {
        // Clear the screen and get ready for the next frame.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        scrollingBackground.draw(batch);
        currentLevel.draw(batch);
        mainShip.draw(batch);

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        mainShip.drawShipHurtbox(shapeRenderer);
        mainShip.drawBulletHitbox(shapeRenderer);
        currentLevel.drawEnemyHitboxAndHurtBox(shapeRenderer);

        shapeRenderer.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
