package io.github.gucksus.simplefirstgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;

public class Constants {
    public static float worldWidth;
    public static float worldHeight;
    public static SpriteBatch batch;
    public static DebugRenderer debugRenderer;
    public static boolean debugMode;

    public static void update(float worldWidth, float worldHeight, SpriteBatch batch,
            DebugRenderer debugRenderer, boolean debugMode) {
        Constants.worldWidth = worldWidth;
        Constants.worldHeight = worldHeight;
        Constants.batch = batch;
        Constants.debugRenderer = debugRenderer;
        Constants.debugMode = debugMode;
    }
}
