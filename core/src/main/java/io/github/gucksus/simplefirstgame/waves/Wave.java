package io.github.gucksus.simplefirstgame.waves;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.animation.AnimSpec;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.Level;
import io.github.gucksus.simplefirstgame.maths.Circular;
import io.github.gucksus.simplefirstgame.maths.PathLerp;

public class Wave {
    protected Array<Enemy> activeEnemyArray;
    public Array<Enemy> waveEnemyArray = new Array<>();
    public int totalEnemies;
    /**
     * The interval between updating each enemy in the wave.
     */
    public float interval;
    public boolean isDone;
    public Array<Vector2> path = new Array<>();
    Enemy centerEnemy;
    public Vector2 centerPoint;
    public float worldWidth;
    public float worldHeight;
    public float clockwiseMultiplier = -1;
    public Level level;

    /**
     * Create a new wave of enemy.
     *
     * @param activeEnemyArray The array that stored active enemies, this should be passed down by a
     *        level.
     * @param totalEnemies The number of enemy this wave holds.
     * @param interval The interval between updating each enemy in the wave.
     * @param startX The initial X coordinate before a position update.
     * @param startY The initial Y coordinate before a position update.
     */
    public Wave(Array<Enemy> activeEnemyArray, int totalEnemies, float interval, float startX,
            float startY, float worldWidth, float worldHeight, Level level) {
        this.activeEnemyArray = activeEnemyArray;
        this.totalEnemies = totalEnemies;
        this.interval = interval;
        path.add(new Vector2(startX, startY));
        path.add(new Vector2());
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.level = level;
    }

    public void addEnemy(Enemy enemy) {
        waveEnemyArray.add(enemy);
        activeEnemyArray.add(enemy);
    }

    /**
     * This method calls for enemies' status update. If the conditions are met, it will remove
     * enemies.
     */
    public void enemyUpdateRemoval() {
        for (int i = waveEnemyArray.size - 1; i >= 0; --i) {
            Enemy enemy = waveEnemyArray.get(i);
            // If the enemy is dead and finished death animation.
            if (enemy.getIsDead() && enemy.isDeathAnimationFinished()) {
                enemy.dispose();
                activeEnemyArray.removeValue(enemy, true);
                waveEnemyArray.removeIndex(i);
            }
        }
    }

    void updateCenterPoint() {
        if (centerEnemy != null) {
            centerPoint = centerEnemy.getCoordinate();
        }
    }

    public void update() {
        enemyUpdateRemoval();
        updateCenterPoint();
    }

    public void moveAllEnemyStraight(Vector2[] path, float delay, float animate, float wait,
            int repeat) {
        // Here the duration is the amount of time it takes for the first enemy to reach the
        // destination.
        PathLerp moveStraight = new PathLerp(new Array<>(path));
        for (int i = 0; i < waveEnemyArray.size; i++) {
            Enemy enemy = waveEnemyArray.get(i);
            AnimSpec<Vector2> moveStraightAnim = new AnimSpec<>(moveStraight, (value, progress) -> {
                enemy.setPosition(value);
            }, delay + i * interval, animate, wait, repeat);
            Constants.pathLerpAnimScheduler.play(enemy.getId() + "moveStraight", moveStraightAnim);
        }
    }

    public void moveAllEnemyInCircle(Vector2 startPoint, Vector2 center, float delay, float animate,
            float wait, int repeat, int revolutionNum) {
        Circular moveCircular = new Circular(startPoint, center, revolutionNum);
        for (int i = 0; i < waveEnemyArray.size; i++) {
            Enemy enemy = waveEnemyArray.get(i);
            AnimSpec<Vector2> moveCircularAnim = new AnimSpec<>(moveCircular, (value, progress) -> {
                enemy.setPosition(value);
            }, delay + i * interval, animate, wait, repeat);
            Constants.circularAnimScheduler.play(enemy.getId() + "moveCircular", moveCircularAnim);
        }
    }
}
