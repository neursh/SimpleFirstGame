package io.github.gucksus.simplefirstgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.levels.Level1;
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

    @Override
    public void create() {
        // Initialize sprite batch and viewport.
        batch = new SpriteBatch();
        debugRenderer = new DebugRenderer(new ShapeRenderer());
        viewport = new FitViewport(8,11);
        worldHeight = viewport.getWorldHeight();
        worldWidth = viewport.getWorldWidth();
        scrollingBackground = new ScrollingBackground(viewport.getWorldHeight(), batch);
        mainShip = new MainShip(4, 0, 2, 2, worldWidth, worldHeight, batch, debugRenderer);
        // Level can be changed by changing currentLevel to desired level.
        level1 = new Level1(worldWidth, worldHeight, batch, mainShip, debugRenderer);
        currentLevel = level1;
    }

    @Override
    public void resize(int width, int height) {
        // CenterCamera must be true to set origin at the bottom left.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        currentLevel.startLevelIfHaveNotStarted();
        currentLevel.update();
        mainShip.update();
        scrollingBackground.backgroundUpdate();
        currentLevel.hitboxAndHurtboxLogic();
        draw();
    }

    private void draw() {
        // Clear the screen and get ready for the next frame.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ShapeRenderer shapeRenderer = debugRenderer.shapeRenderer;
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        batch.disableBlending();
        scrollingBackground.draw();
        batch.enableBlending();
        currentLevel.draw();
        mainShip.draw();

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        mainShip.drawDebug();
        currentLevel.drawDebug();

        shapeRenderer.end();

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
