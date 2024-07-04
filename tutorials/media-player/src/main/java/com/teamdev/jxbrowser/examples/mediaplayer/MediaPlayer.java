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

package com.teamdev.jxbrowser.examples.mediaplayer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.SwingUtilities.invokeLater;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback.Response;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to create a Java Media Player using the Chromium web browser HTML5
 * video capabilities that allows playing MP4 media content and using <a
 * href="https://github.com/sampotts/plyr">Plyr</a> &mdash; one of the most popular JavaScript
 * library, control the playback directly from Java UI.
 */
public final class MediaPlayer extends JPanel {

    private static final String BTN_MUTE_TEXT = "Mute";
    private static final String BTN_UNMUTE_TEXT = "Unmute";

    private static final String BTN_PLAY_TEXT = "Play";
    private static final String BTN_PAUSE_TEXT = "Pause";

    public static void main(String[] args) {
        invokeLater(() -> {
            MediaPlayer mediaPlayer = new MediaPlayer();

            JFrame frame = new JFrame("Java Media Player");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mediaPlayer.dispose();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(mediaPlayer, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private final Engine engine;
    private JsPlayer player;

    private MediaPlayer() {
        engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        // In this demo we load MP4 video file, so we have to enable
                        // the corresponding proprietary features that are disabled
                        // by default.
                        .enableProprietaryFeature(ProprietaryFeature.H_264)
                        .enableProprietaryFeature(ProprietaryFeature.AAC)
                        // Enable the possibility to play video programmatically
                        // from JavaScript without user interaction on a web page.
                        .enableAutoplay()
                        .build());
        Browser browser = engine.newBrowser();

        // Inject an instance of JsPlayer into JavaScript to call its methods from
        // JavaScript to inform about player events.
        browser.set(InjectJsCallback.class, params -> {
            Frame frame = params.frame();
            JsObject window = frame.executeJavaScript("window");
            if (window != null) {
                player = new JsPlayer(frame);
                window.putProperty("java", player);
            }
            return Response.proceed();
        });

        // Get absolute path to the media.html file with the JS video player, load it and
        // wait until the file is loaded completely, so we can build the player UI controls.
        URL resource = MediaPlayer.class.getResource("/media.html");
        if (resource != null) {
            browser.navigation().loadUrlAndWait(resource.toString());
        }

        // Create a visual Swing control that will display content of the web page with video.
        BrowserView view = BrowserView.newInstance(browser);
        view.setPreferredSize(new Dimension(1280, 720));

        // Embed the control into Java Swing Frame.
        setLayout(new BorderLayout());
        add(view, BorderLayout.CENTER);
        add(playbackControls(), BorderLayout.SOUTH);
    }

    /**
     * Releases all allocated resources and closes the web browser instance.
     */
    private void dispose() {
        engine.close();
    }

    /**
     * Formats an elapsed time in the "H:MM:SS" form for display on the call-in-progress screen.
     *
     * @param elapsedSeconds the elapsed time in seconds.
     */
    private static String formatElapsedTime(int elapsedSeconds) {
        checkArgument(elapsedSeconds >= 0);
        int hours = elapsedSeconds / 3600;
        int minutes = (elapsedSeconds % 3600) / 60;
        int seconds = elapsedSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private JComponent playbackControls() {
        JPanel controls = new JPanel();
        controls.add(createPlaybackTimeLabels());
        controls.add(createMuteUnmuteButton());
        controls.add(createVolumeSlider());

        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        result.add(createPlayPauseButton(), BorderLayout.WEST);
        result.add(createPlaybackSlider(), BorderLayout.CENTER);
        result.add(controls, BorderLayout.EAST);
        return result;
    }

    private JSlider createVolumeSlider() {
        JSlider volume = new JSlider(0, 100, ((int) (player.volume() * 100)));
        // When user moves the volume slider, set the corresponding volume level in JS player.
        volume.addChangeListener(e -> player.volume((double) volume.getValue() / 100));
        return volume;
    }

    private JComponent createPlaybackTimeLabels() {
        JLabel currentTimeLabel = new JLabel(formatElapsedTime(player.currentTime().intValue()));
        JLabel durationLabel = new JLabel(formatElapsedTime(player.duration().intValue()));

        // Update the current time label during playback.
        player.onTimeUpdated(currentTime -> invokeLater(
                () -> currentTimeLabel.setText(formatElapsedTime(currentTime.intValue()))));

        JPanel result = new JPanel();
        result.add(currentTimeLabel);
        result.add(new JLabel("/"));
        result.add(durationLabel);
        return result;
    }

    private JComponent createPlaybackSlider() {
        // The max value of the slider equals the track duration in seconds.
        JSlider playbackSlider = new JSlider(0, player.duration().intValue(), 0);
        playbackSlider.addChangeListener(e -> {
            if (playbackSlider.getValueIsAdjusting()) {
                // When user moves slider, update the current time in JS player.
                player.currentTime(playbackSlider.getValue());
            }
        });
        // Update the slider position when the current time has changed during playback.
        player.onTimeUpdated(
                currentTime -> invokeLater(() -> playbackSlider.setValue(currentTime.intValue())));

        return playbackSlider;
    }

    private JButton createMuteUnmuteButton() {
        JButton muteButton = new JButton(BTN_MUTE_TEXT);
        muteButton.addActionListener(e -> {
            if (player.isMuted()) {
                player.unmute();
            } else {
                player.mute();
            }
        });
        // Update the button text when sound has been muted or unmuted.
        player.onVolumeChanged(
                muted -> muteButton.setText(muted ? BTN_UNMUTE_TEXT : BTN_MUTE_TEXT));
        return muteButton;
    }

    private JComponent createPlayPauseButton() {
        JButton playButton = new JButton(BTN_PLAY_TEXT);
        playButton.addActionListener(e -> {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }
        });

        // Update the button text when playback has been started or paused.
        player.onPlaybackStarted(unused -> playButton.setText(BTN_PAUSE_TEXT));
        player.onPlaybackPaused(unused -> playButton.setText(BTN_PLAY_TEXT));

        JPanel panel = new JPanel();
        panel.add(playButton);
        return panel;
    }
}
