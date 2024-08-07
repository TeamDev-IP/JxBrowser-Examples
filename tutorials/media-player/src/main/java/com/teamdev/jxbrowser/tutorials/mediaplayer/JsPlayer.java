/*
 *  Copyright 2024, TeamDev. All rights reserved.
 *
 *  Redistribution and use in source and/or binary forms, with or without
 *  modification, must retain the above copyright notice and the following
 *  disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.teamdev.jxbrowser.tutorials.mediaplayer;

import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.js.JsAccessible;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * A JavaScript Player controller that is injected into JavaScript to get notifications about
 * playback events from the Plyr JavaScript player and provides method for calling the player
 * commands from Java.
 */
public final class JsPlayer {

    private final Frame frame;

    private final Set<Consumer<Double>> timeUpdatedListeners;
    private final Set<Consumer<Boolean>> volumeChangedListeners;
    private final Set<Consumer<Void>> playbackStartedListeners;
    private final Set<Consumer<Void>> playbackPausedListeners;

    JsPlayer(Frame frame) {
        this.frame = frame;

        timeUpdatedListeners = new CopyOnWriteArraySet<>();
        playbackStartedListeners = new CopyOnWriteArraySet<>();
        playbackPausedListeners = new CopyOnWriteArraySet<>();
        volumeChangedListeners = new CopyOnWriteArraySet<>();
    }

    private <T> T execute(String s) {
        return frame.executeJavaScript(s);
    }

    /**
     * Starts playback.
     */
    void play() {
        execute("player.play()");
    }

    /**
     * Pauses playback.
     */
    void pause() {
        execute("player.pause()");
    }

    /**
     * Returns a boolean indicating if the current player is playing.
     */
    Boolean isPlaying() {
        return execute("player.playing");
    }

    /**
     * Returns the duration for the current media in seconds.
     */
    Double duration() {
        return execute("player.duration");
    }

    /**
     * Sets the current time for the player in seconds.
     *
     * @param timeInSeconds the new current time in seconds
     */
    void currentTime(double timeInSeconds) {
        execute("player.currentTime = " + timeInSeconds);
    }

    /**
     * Returns the current time for the player in seconds.
     */
    Double currentTime() {
        return execute("player.currentTime");
    }

    /**
     * Sets the volume for the player.
     *
     * @param volume a volume value between 0 and 1
     */
    void volume(double volume) {
        execute("player.volume = " + volume);
    }

    /**
     * Returns the volume for the player.
     */
    Double volume() {
        return execute("player.volume");
    }

    /**
     * Sets the muted state of the player.
     */
    void mute() {
        execute("player.muted = true");
    }

    /**
     * Resets the muted state of the player.
     */
    void unmute() {
        execute("player.muted = false");
    }

    /**
     * Returns a boolean indicating if the current player is muted.
     */
    Boolean isMuted() {
        return execute("player.muted");
    }

    /**
     * Registers a listener for getting notifications when playback has been started.
     */
    void onPlaybackStarted(Consumer<Void> listener) {
        playbackStartedListeners.add(listener);
    }

    /**
     * Registers a listener for getting notifications when playback has been paused.
     */
    void onPlaybackPaused(Consumer<Void> listener) {
        playbackPausedListeners.add(listener);
    }

    /**
     * Registers a listener for getting notifications when playback time has been updated.
     */
    void onTimeUpdated(Consumer<Double> listener) {
        timeUpdatedListeners.add(listener);
    }

    /**
     * Registers a listener for getting notifications when player volume has been changed.
     */
    void onVolumeChanged(Consumer<Boolean> listener) {
        volumeChangedListeners.add(listener);
    }

    /**
     * Invoked from JavaScript when playback has been started.
     */
    @JsAccessible
    public void onPlaybackStarted() {
        playbackStartedListeners.forEach(listener -> listener.accept(null));
    }

    /**
     * Invoked from JavaScript when playback has been paused.
     */
    @JsAccessible
    public void onPlaybackPaused() {
        playbackPausedListeners.forEach(listener -> listener.accept(null));
    }

    /**
     * Invoked from JavaScript when playback time has been updated.
     */
    @JsAccessible
    public void onTimeUpdated(double currentTime) {
        timeUpdatedListeners.forEach(listener -> listener.accept(currentTime));
    }

    /**
     * Invoked from JavaScript when player volume has been changed.
     */
    @JsAccessible
    public void onVolumeChanged(double volume, boolean muted) {
        volumeChangedListeners.forEach(listener -> listener.accept(muted));
    }
}
