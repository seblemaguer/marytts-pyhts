package marytts.pyhts;


import javax.sound.sampled.*;

import marytts.features.FeatureComputer;
import marytts.data.Utterance;
import marytts.data.Sequence;
import marytts.data.SupportedSequenceType;
import marytts.data.item.acoustic.AudioItem;
import marytts.io.serializer.XMLSerializer;
import marytts.io.serializer.DefaultHTSLabelSerializer;
import marytts.io.MaryIOException;

import java.io.InputStream;
import java.io.File;
import java.util.Scanner;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.*;


/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class TestPyHTSWrapper  implements LineListener {

    Clip clip;

    @Test
    public void checkCallPyHTSWrapper() throws Exception {

        // FIXME: temp initialisation of the
        FeatureComputer.initDefault();

        // Load XML
        try {
            String testResourceName = "input_utt.xml";
            InputStream input = this.getClass().getResourceAsStream(testResourceName);
            String input_xml = new Scanner(input, "UTF-8").useDelimiter("\\A").next();

            // Load the utterance
            XMLSerializer xml_seri = new XMLSerializer();
            Utterance utt = xml_seri.load(input_xml);

            // Create fake voice
            PyHTSVoice v = new PyHTSVoice();

            // Try to call the wrapper
            PyHTSWrapper py_wrapper = new PyHTSWrapper();
            utt = py_wrapper.synthesizeHMM(v, utt, new DefaultHTSLabelSerializer(), false);
            Sequence<AudioItem> seq_aud = (Sequence<AudioItem>) utt.getSequence(SupportedSequenceType.AUDIO);
            AudioItem ai = seq_aud.get(0);

            AudioSystem.write(ai.getAudio(), AudioFileFormat.Type.WAVE, new File("/home/slemaguer/test.wav"));

        } catch (MaryIOException ex) {
            if (ex.getEmbeddedException() != null) {
                throw ex.getEmbeddedException();
            } else {
                throw ex;
            }
        }
    }


    public void update(LineEvent le) {
        LineEvent.Type type = le.getType();
        if (type == LineEvent.Type.OPEN) {
            System.out.println("OPEN");
        } else if (type == LineEvent.Type.CLOSE) {
            System.out.println("CLOSE");
        } else if (type == LineEvent.Type.START) {
            System.out.println("START");
        } else if (type == LineEvent.Type.STOP) {
            System.out.println("STOP");
            clip.close();
        }
    }
}


/* TestPyHTSWrapper.java ends here */
