package com.sonavtek.sonav;

import java.util.Stack;

import android.media.MediaPlayer;
import android.util.Log;

/**
 * This is a player that plays sounds by given URL. Currently, it is used by the
 * engine to play the sounds when events occurred and need to play sounds. <br/>
 * <br/>
 * This class uses stack to store received URLs to play, it only plays the last
 * one sound every time and clear the others.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class EventSoundPlayer implements Runnable {

    private MediaPlayer player;
    private boolean stopped;
    private String currentUrl;
    private Stack<String> playList;

    /**
     * Create new instance of EventSoundPlayer.
     */
    public EventSoundPlayer() {
        this.player = new MediaPlayer();
        this.playList = new Stack<String>();
    }

    /**
     * Get the last URL of sound to play and clear others.
     */
    public void run() {
        while (!stopped) {
            try {
                if (currentUrl == null) {
                    synchronized (playList) {
                        if (currentUrl == null && playList.size() > 0) {
                            currentUrl = playList.pop();
                            playList.clear();

                            player.setDataSource(currentUrl);
                            player.prepare();

                            player.setOnCompletionListener(
                                    new MediaPlayer.OnCompletionListener() {

                                public void onCompletion(MediaPlayer mp) {
                                    try {
                                        Log.d(getClass().toString(), "onCompletion");
                                        mp.reset();
                                    } catch (Throwable t) {
                                        Log.e(getClass().toString(), t.getMessage(), t);
                                    } finally {
                                        currentUrl = null;

                                        synchronized (playList) {
                                            playList.notifyAll();
                                        }
                                    }
                                }
                            });

                            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    try {
                                        Log.d(getClass().toString(), "onError");
                                        mp.reset();
                                    } catch (Throwable t) {
                                        Log.e(getClass().toString(), t.getMessage(), t);
                                    } finally {
                                        currentUrl = null;

                                        synchronized (playList) {
                                            playList.notifyAll();
                                        }
                                    }

                                    return true;
                                }
                            });

                            player.start();
                        }

                        playList.wait();
                    }
                } else {
                    synchronized (playList) {
                        playList.wait();
                    }
                }
            } catch (Throwable t) {
                Log.e(getClass().toString(), t.getMessage(), t);
                currentUrl = null;
            }
        }
    }

    /**
     * Add a new URL to play.
     * 
     * @param url
     *            URL of sound.
     */
    public void addNewSound(String url) {
        synchronized (playList) {
            playList.add(url);
            playList.notifyAll();
        }
    }

    /**
     * Check if the thread is stopped.
     * 
     * @return the stopped
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * Stop this thread. Stop the instance.
     */
    public void stop() {
        this.stopped = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EventSoundPlayer [currentUrl=" + currentUrl + ", playList=" + playList +
                ", player=" + player + ", stopped=" + stopped + "]";
    }
}
