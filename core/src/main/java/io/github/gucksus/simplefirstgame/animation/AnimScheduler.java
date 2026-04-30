package io.github.gucksus.simplefirstgame.animation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import com.badlogic.gdx.Gdx;


public class AnimScheduler<T, K> {
    private final ConcurrentHashMap<String, AnimSpec<K>> specs = new ConcurrentHashMap<>();

    private final AtomicBoolean autoClean = new AtomicBoolean(false);
    private final AtomicBoolean disposeFlag = new AtomicBoolean(false);

    private void framer() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (disposeFlag.get()) {
                    return;
                }

                float delta = Gdx.graphics.getDeltaTime();

                specs.forEach((key, anim) -> {
                    boolean result = anim.update(delta);
                    if (autoClean.get() && !result) {
                        specs.remove(key);
                    }
                });

                Gdx.app.postRunnable(this);
            }
        });
    }

    /**
     * An animation scheduler, the actual logic happens inside of AnimSpec, this class serves as a
     * host for it.
     *
     * @param autoClean When the animation finishes, remove it from the scheduler, effectively stop
     *        it, you have to later call .play() again if you reset the AnimSpec.
     */
    public AnimScheduler(boolean autoClean) {
        this.autoClean.set(autoClean);
        framer();
    }

    public void play(String key, AnimSpec<K> task) {
        specs.put(key, task);
    }

    public void stop(String key) {
        specs.remove(key);
    }

    public void pause(String key) {
        specs.get(key).pause();
    }

    public void resume(String key) {
        specs.get(key).resume();
    }

    public void clean() {
        specs.forEach((key, anim) -> {
            if (anim.isFinished()) {
                specs.remove(key);
            }
        });
    }

    public void dispose() {
        disposeFlag.set(true);
        specs.clear();
    }
}
