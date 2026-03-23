package io.github.gucksus.simplefirstgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.gucksus.simplefirstgame.entities.Bulletlv1;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends ApplicationAdapter {
    // Declare variables.
    Texture backgroundTextureNo0;
    Texture backgroundTextureNo1;
    Texture backgroundTextureNo2;
    Texture shipTexture;
    Texture bulletlv1Texture;
    private SpriteBatch batch;
    Sprite shipSprite;
    Sprite[] backgroundSprites;
    FitViewport viewport;
    Array <Bulletlv1> bulletlv1Array;
    float bulletTimer = 1f;
    float fireRate = .2f;
    float backgroundSpeed = 3f;

    @Override
    public void create() {
        // Adds texture.
        backgroundSprites = new Sprite[3];
        backgroundTextureNo0 = new Texture("background1.png");
        backgroundTextureNo1 = new Texture("background2.png");
        backgroundTextureNo2 = new Texture("background3.png");
        shipTexture = new Texture("ShipSprite.png");
        bulletlv1Texture = new Texture("bullet_texture.png");

        // Initialize sprites.
        shipSprite = new Sprite(shipTexture);
        shipSprite.setSize(1, 1);
        shipSprite.setCenterX(4);
        backgroundSprites[0] = new Sprite(backgroundTextureNo0);
        backgroundSprites[1] = new Sprite(backgroundTextureNo1);
        backgroundSprites[2] = new Sprite(backgroundTextureNo2);
        // Height is 22 because the background is supposed to be twice the size to create infinity effect.
        backgroundSprites[0].setSize(8, 11);
        backgroundSprites[1].setSize(8, 11);
        backgroundSprites[2].setSize(8, 11);

        // Initialize sprite batch and viewport.
        batch = new SpriteBatch();
        viewport = new FitViewport(8,11);
        bulletlv1Array = new Array<>();
        backgroundSprites[0].setY(viewport.getWorldHeight());
        backgroundSprites[2].setY(-viewport.getWorldHeight());
    }

    @Override
    public void resize(int width, int height) {
        // CenterCamera must be true to set origin at the bottom left.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        backgroundUpdate();
        clampLogic();
        bulletLogic();
        draw();
    }

    private void input() {
        float speed = 6f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            shipSprite.translateX(-speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            shipSprite.translateX(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            shipSprite.translateY(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            shipSprite.translateY(-speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            bulletSpawn();
        }
    }

    private void backgroundUpdate() {
        float delta = Gdx.graphics.getDeltaTime();

        for (Sprite background : backgroundSprites) {
            background.translateY(-backgroundSpeed * delta);
        }

        // find the highest sprite's Y
        float highestY = backgroundSprites[0].getY();
        int highestIdx = 0;
        int outOfScreenIdx = -1;
        for (int i = 0; i < 3; i++) {
            if (backgroundSprites[i].getY() > highestY) {
                highestY = backgroundSprites[i].getY();
                highestIdx = i;
            }
            if (backgroundSprites[i].getY() + backgroundSprites[i].getHeight() < 0)
                outOfScreenIdx = i;
        }
        if (outOfScreenIdx != -1)
            backgroundSprites[outOfScreenIdx].setY(backgroundSprites[highestIdx].getY() + backgroundSprites[highestIdx].getHeight());
    }

    private void bulletSpawn() {
        if (bulletTimer >= fireRate) {
            float iniX = shipSprite.getX() + shipSprite.getWidth() / 2;
            float iniY = shipSprite.getY() + shipSprite.getHeight();
            bulletlv1Array.add(new Bulletlv1(bulletlv1Texture, iniX, iniY));
            bulletTimer = 0;
        }
    }

    private void bulletLogic() {
        float delta = Gdx.graphics.getDeltaTime();
        bulletTimer += delta;

        for (Bulletlv1 bullet: bulletlv1Array) {
            bullet.update(delta);
        }
    }

    private void clampLogic() {
        float worldHeight = viewport.getWorldHeight();
        float worldWidth = viewport.getWorldWidth();

        shipSprite.setX(MathUtils.clamp(shipSprite.getX(), -(shipSprite.getWidth()/4), worldWidth - shipSprite.getWidth() / 4 * 3));
        shipSprite.setY(MathUtils.clamp(shipSprite.getY(), 0, worldHeight - shipSprite.getHeight()));
    }

    private void draw() {
        // Clear the screen and get ready for the next frame.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // Draw background and ship.
        for (Sprite background: backgroundSprites) {
            background.draw(batch);
        }
        shipSprite.draw(batch);

        for (Bulletlv1 bulletlv1: bulletlv1Array) {
            bulletlv1.bulletSelfSprite.draw(batch);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
