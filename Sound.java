import java.io.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;

public class Sound {
    // MIDI sequencer and audio clip references
    private Sequencer midiPlayer;
    private Clip clip;
    private Sequence sequence;
    private boolean isMidi;

    // Loads the file as MIDI or WAV depending on extension
    public Sound(String fileName) {
        isMidi = fileName.contains("mid");
        try {
            if (isMidi) {
                sequence = MidiSystem.getSequence(new File(fileName));
                midiPlayer = MidiSystem.getSequencer();
            } else {
                clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File(fileName)));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Starts playback, loops MIDI tracks
    public void play() {
        try {
            if (isMidi) {
                midiPlayer.open();
                midiPlayer.setSequence(sequence);
                midiPlayer.setLoopCount(-1);
                midiPlayer.start();
            } else if (!clip.isRunning()) {
                clip.setFramePosition(0);
                clip.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Stops playback and releases resources
    public void stop() {
        if (isMidi) {
            if (midiPlayer.isRunning()) { midiPlayer.stop(); midiPlayer.close(); }
        } else {
            clip.stop();
        }
    }
}
