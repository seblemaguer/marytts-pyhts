package marytts.pyhts;

import marytts.Voice;

/**
 * /!\ Locally /!\  installed PyHTS voice
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class PyHTSVoice extends Voice
{
    private String configuration_path;

    public PyHTSVoice() {
	super("Blizzard");
	setConfigurationPath("/home/slemaguer/tmp/test-voice/raw/config.json");
    }

    public PyHTSVoice(String name, String configuration_path)
    {
	super(name);
	setConfigurationPath(configuration_path);
    }


    public String getConfigurationPath() {
	return configuration_path;
    }

    public void setConfigurationPath(String configuration_path) {
	this.configuration_path = configuration_path;
    }
}


/* PyHTSVoice.java ends here */
