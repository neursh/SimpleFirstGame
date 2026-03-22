package io.github.gucksus.simplefirstgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    // Declare variables.
    Texture backgroundTexture;
    Texture shipTexture;
    private SpriteBatch batch;
    Sprite shipSprite;
    Sprite backgroundSprite;
    FitViewport viewport;

    @Override
    public void create() {
        // Adds texture.
        backgroundTexture = new Texture("background.png");
        shipTexture = new Texture("ShipSprite.png");

        // Initialize sprites.
        shipSprite = new Sprite(shipTexture);
        shipSprite.setSize(1, 1);
        backgroundSprite = new Sprite(backgroundTexture);
        // Height is 22 because the background is supposed to be twice the size to create infinity effect.
        backgroundSprite.setSize(8, 22);

        // Initialize sprite batch and viewport.
        batch = new SpriteBatch();
        viewport = new FitViewport(8,11);
    }

    @Override
    public void resize(int width, int height) {
        // CenterCamera must be true to set origin at the bottom left.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {

    }

    private void logic() {

    }

    private void draw() {
        // Clear the screen and get ready for the next frame.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // Draw background and ship.
        backgroundSprite.draw(batch);
        shipSprite.draw(batch);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
