package marytts.pyhts.modules;

import marytts.config.MaryProperties;
import marytts.MaryException;

import marytts.modules.MaryModule;
import marytts.data.Utterance;
import marytts.pyhts.PyHTSWrapper;
import marytts.pyhts.PyHTSVoice;
import marytts.io.serializer.DefaultHTSLabelSerializer;

/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class HMMSynthesizer extends MaryModule {

    public HMMSynthesizer() {
        super("HMMSynthesizer");
    }

    public Utterance process(Utterance utt, MaryProperties configuration) throws MaryException {

	try {
	    // Create fake voice
	    PyHTSVoice v = new PyHTSVoice();

	    // Try to call the wrapper
	    PyHTSWrapper py_wrapper = new PyHTSWrapper();
	    utt = py_wrapper.synthesizeHMM(v, utt, new DefaultHTSLabelSerializer(), false);

	    return utt;
	} catch (Exception ex) {
	    throw new MaryException("Problem during synthesis", ex);
	}
    }


}


/* HMMSynthesizer.java ends here */
