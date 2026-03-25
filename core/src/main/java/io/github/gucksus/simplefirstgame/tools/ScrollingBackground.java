package io.github.gucksus.simplefirstgame.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ScrollingBackground {
    // Different background textures.
    Texture backgroundTextureNo0;
    Texture backgroundTextureNo1;
    Texture backgroundTextureNo2;
    public Sprite[] backgroundSprites;
    float backgroundSpeed = 3f;

    public ScrollingBackground(float worldHeight) {
        // Background has 3 sprites for scrolling effect.
        backgroundSprites = new Sprite[3];
        backgroundTextureNo0 = new Texture("background1.png");
        backgroundTextureNo1 = new Texture("background2.png");
        backgroundTextureNo2 = new Texture("background3.png");

        backgroundSprites[0] = new Sprite(backgroundTextureNo0);
        backgroundSprites[1] = new Sprite(backgroundTextureNo1);
        backgroundSprites[2] = new Sprite(backgroundTextureNo2);

        backgroundSprites[0].setSize(8, 11);
        backgroundSprites[1].setSize(8, 11);
        backgroundSprites[2].setSize(8, 11);

        backgroundSprites[0].setY(worldHeight);
        backgroundSprites[2].setY(-worldHeight);
    }

    public void backgroundUpdate(float delta) {

        for (Sprite background : backgroundSprites) {
            background.translateY(-delta * backgroundSpeed);
        }

        /*How does this work:
         * As a background sprite go entirely below the screen, it looks for the highest sprite and then put the off-screen sprite
         * on top.*/
        float highestY = backgroundSprites[0].getY();
        int highestIdx = 0;
        int outOfScreenIdx = -1;
        for (int i = 0; i < 3; i++) { // The for is for both looking for the off-screen and the highest sprite.
            if (backgroundSprites[i].getY() > highestY) {
                highestY = backgroundSprites[i].getY();
                highestIdx = i;
            }
            if (backgroundSprites[i].getY() + backgroundSprites[i].getHeight() < 0)
                outOfScreenIdx = i;
        }
        if (outOfScreenIdx != -1) // If there IS a sprite that off-screen.
            backgroundSprites[outOfScreenIdx].setY(highestY + backgroundSprites[highestIdx].getHeight());
    }

    public void draw(Batch batch) {
        for (Sprite background: backgroundSprites) {
            background.draw(batch);
        }
    }
}
