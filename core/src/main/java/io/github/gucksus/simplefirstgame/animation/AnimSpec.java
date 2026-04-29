package io.github.gucksus.simplefirstgame.animation;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import io.github.gucksus.simplefirstgame.helpers.AtomicFloat;

public class AnimSpec<T> {
    @FunctionalInterface
    public interface AnimationCallback<T> {
        void execute(T value, float progress);
    }

    private CallableMath<T> math;
    private AnimationCallback<T> callback;

    private AtomicBoolean finished = new AtomicBoolean(false);

    private AtomicFloat delaySec = new AtomicFloat(0);
    private AtomicFloat animateSec = new AtomicFloat(0);
    private AtomicFloat waitSec = new AtomicFloat(0);
    private AtomicInteger repeat = new AtomicInteger(0);

    private AtomicFloat delayedSec = new AtomicFloat(0);
    private AtomicFloat animatedSec = new AtomicFloat(0);
    private AtomicFloat waitedSec = new AtomicFloat(0);
    private AtomicInteger repeated = new AtomicInteger(0);

    private AtomicFloat progress = new AtomicFloat(0);

    private AtomicBoolean paused = new AtomicBoolean(false);

    /**
     * AnimSpec describes how an animaion plays in seconds format. The class allows manual frame
     * handling, but best paired with AnimScheduler for simplicity.
     *
     * @param math A class that can be used to get value on each frame for your actions.
     * @param callback A function that calls every animate frame, this is where you would update
     *        your objects.
     * @param delay Delay in seconds before animate, doesn't trigger callback.
     * @param animate Fetch math value and callback in seconds.
     * @param wait Delay after animate, this value allows animation sequencing.
     * @param repeat How many repeat times for the animation.
     */
    public AnimSpec(CallableMath<T> math, AnimationCallback<T> callback, float delay, float animate,
            float wait, int repeat) {
        this.setMath(math);
        this.setCallback(callback);

        this.setDelay(delay);
        this.setAnimate(animate);
        this.setWait(wait);
        this.setRepeat(repeat);
    }

    private float normalize(float value) {
        if (value > 1) {
            return 1;
        }
        if (value < 0) {
            return 0;
        }
        return value;
    }

    public boolean update(float delta) {
        if (this.repeated.get() > this.repeat.get()) {
            this.finished.set(true);
            return false;
        }

        if (this.paused.get()) {
            return true;
        }

        if (this.delayedSec.get() < this.delaySec.get()) {
            this.delayedSec.set(this.delayedSec.get() + delta);
            return true;
        }

        this.animatedSec.set(this.animatedSec.get() + delta);
        this.progress.set(normalize((float) this.animatedSec.get() / this.animateSec.get()));

        if (this.waitedSec.get() == 0) {
            this.callback.execute(this.math.get(this.progress.get()), this.progress.get());
        }

        if (progress.get() == 1) {
            if (this.waitedSec.get() < this.waitSec.get()) {
                this.waitedSec.set(this.waitedSec.get() + delta);
                return true;
            }

            this.repeated.incrementAndGet();
            this.reset(false);
        }

        return true;
    }

    public void pause() {
        this.paused.set(true);
    }

    public void resume() {
        this.paused.set(false);
    }

    public void reset(boolean destructive) {
        if (destructive) {
            this.repeated.set(0);
            this.paused.set(false);
            this.finished.set(false);
        }
        this.delayedSec.set(0);
        this.animatedSec.set(0);
        this.waitedSec.set(0);
        this.progress.set(0);
    }

    private void setMath(CallableMath<T> math) {
        this.math = math;
    }

    private void setCallback(AnimationCallback<T> callback) {
        this.callback = callback;
    }

    public void setDelay(float delay) {
        this.delaySec.set(delay);
    }

    public Duration getDelay() {
        return Duration.ofMillis((long) this.delaySec.get());
    }

    public void setAnimate(float animate) {
        this.animateSec.set(animate);
    }

    public Duration getDuration() {
        return Duration.ofMillis((long) this.animateSec.get());
    }

    public void setWait(float wait) {
        this.waitSec.set(wait);
    }

    public Duration getWait() {
        return Duration.ofMillis((long) this.waitSec.get());
    }

    public void setRepeat(int repeat) {
        this.repeat.set(repeat);
    }

    public long getRepeat() {
        return this.repeat.get();
    }

    public boolean isFinished() {
        return this.finished.get();
    }

    public boolean isPaused() {
        return this.paused.get();
    }
}
