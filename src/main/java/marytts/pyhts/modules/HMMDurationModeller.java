package marytts.pyhts.modules;

import marytts.modules.MaryModule;
import marytts.data.Utterance;

import marytts.config.MaryProperties;

/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class HMMDurationModeller extends MaryModule {

    public HMMDurationModeller() {
        super("HMMDurationModeller");
    }

    public Utterance process(Utterance utt, MaryProperties configuration) {
        throw new UnsupportedOperationException();
    }
}


/* HMMDurationModeller.java ends here */
