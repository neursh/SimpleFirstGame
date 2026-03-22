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
    Texture background;
    Texture shipTexture;
    private SpriteBatch batch;
    Sprite shipSprite;
    FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        batch.begin();

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
