package com.omarkandil.exchange;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.util.LinkedList;
import java.util.Queue;

// This function handles the voiceover functionality. It is a singleton pattern with a thread which queues all requests to speak
// and says them one after the other. These all run on a seperate thread speakingThread so that other functionality can continue.
public class VoiceOverService {
    private static final String VOICE_NAME = "kevin16";
    private Voice voice;
    private final Object lock = new Object();
    private Queue<String> speechQueue = new LinkedList<>();
    private Thread speakingThread;
    private static VoiceOverService instance;

    // This initializes the voice.
    private VoiceOverService() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(VOICE_NAME);
        if (voice == null) {
            System.err.println("No voice named " + VOICE_NAME + " found.");
        } else {
            voice.allocate();
        }
    }
    // Initializes the singleton object.
    public static synchronized VoiceOverService getInstance() {
        if (instance == null) {
            instance = new VoiceOverService();
        }
        return instance;
    }

    // This function speaks by going over the queue and emitting each message in it.
    public void speak(String text) {
        synchronized (lock) {
            speechQueue.add(text);
            if (speakingThread == null || !speakingThread.isAlive()) {
                speakingThread = new Thread(this::processSpeechQueue);
                speakingThread.start();
            }
        }
    }

    private void processSpeechQueue() {
        while (true) {
            final String nextSpeech;
            synchronized (lock) {
                nextSpeech = speechQueue.poll();
                if (nextSpeech == null) {
                    return;
                }
            }
            if (voice != null) {
                voice.speak(nextSpeech);
            }
        }
    }

    //This terminates the speech by clearing the queue.
    public void stopSpeaking() {
        synchronized (lock) {
            speechQueue.clear();
        }
    }

    public void shutdown() {
        stopSpeaking();
        synchronized (lock) {
            if (voice != null) {
                voice.deallocate();
            }
        }
    }
}
