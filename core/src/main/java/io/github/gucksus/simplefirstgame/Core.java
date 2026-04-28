package io.github.gucksus.simplefirstgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.levels.Level1;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
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
    DebugRenderer debugRenderer;
    ShapeRenderer shapeRenderer;
    BulletHolder bulletHolder;

    @Override
    public void create() {
        // Initialize sprite batch and viewport.
        batch = new SpriteBatch();
        debugRenderer = new DebugRenderer(new ShapeRenderer());
        shapeRenderer = debugRenderer.shapeRenderer;
        viewport = new FitViewport(8, 11);
        worldHeight = viewport.getWorldHeight();
        worldWidth = viewport.getWorldWidth();

        Constants.update(worldWidth, worldHeight, batch, debugRenderer, true);

        bulletHolder = new BulletHolder();
        mainShip = new MainShip(4, 0, 2, 2, bulletHolder);
        level1 = new Level1(bulletHolder, mainShip);
        currentLevel = level1;
        scrollingBackground = new ScrollingBackground(viewport.getWorldHeight(), batch);
    }

    @Override
    public void resize(int width, int height) {
        // CenterCamera must be true to set origin at the bottom left.
        viewport.update(width, height, true);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        viewport.apply();
    }

    @Override
    public void render() {
        currentLevel.startLevelIfHaveNotStarted();
        currentLevel.update();
        mainShip.update();
        bulletHolder.update();
        scrollingBackground.backgroundUpdate();
        draw();
    }

    private void draw() {

        batch.begin();

        scrollingBackground.draw();
        mainShip.draw();
        currentLevel.draw();
        bulletHolder.draw();

        batch.end();

        if (Constants.debugMode) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            mainShip.drawDebug();
            currentLevel.drawDebug();
            bulletHolder.drawDebug();

            shapeRenderer.end();
        }

    }

    @Override
    public void dispose() {
        batch.dispose();
        debugRenderer.dispose();
        mainShip.dispose();
        level1.dispose();
        scrollingBackground.dispose();
    }
}
