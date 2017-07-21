package marytts.pyhts;

import javax.sound.sampled.UnsupportedAudioFileException;


import marytts.data.SupportedSequenceType;
import marytts.data.Utterance;
import marytts.data.Sequence;
import marytts.data.item.acoustic.AudioItem;
import marytts.io.serializer.Serializer;
import marytts.MaryException;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileWriter;

/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class PyHTSWrapper {

    private static final String HMM_GENERATION = "--generator=default";
    private static final String NO_GENERATION = "--generator=none";
    private static final String NO_RENDERING  = "--renderer=none";
    private static final String SYNTH_SCRIPT_NAME = "synth.py";

    private String synth_script_path;
    private int pg_type;


    public PyHTSWrapper() throws MaryException {
        setPGType(0);
        synth_script_path = null;

        // FIXME: temp dirty hack
        setBase("/home/slemaguer/work/maintained_tools/src/pyhts");
    }

    public PyHTSWrapper(String base) throws MaryException {
        setPGType(0);
        setBase(base);
    }

    public void setBase(String base) throws MaryException {
        this.synth_script_path = base + "/" + SYNTH_SCRIPT_NAME;
        if (!(new File(this.synth_script_path)).isFile()) {
            throw new MaryException("This is not a valid file: " + this.synth_script_path, null);
        }
    }

    public void setPGType(int pg_type) {
        this.pg_type = pg_type;
    }


    /*************************************************************************
     **
     *************************************************************************/
    public Utterance generate(PyHTSVoice v, Utterance input_utt, Serializer label_serializer) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void execCommand(String command) throws IOException, InterruptedException, MaryException  {

        // Run the command
        final Process process = Runtime.getRuntime().exec(command);

        // Consommation de la sortie standard de l'application externe dans un Thread separe
        new Thread() {
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = "";
                    try {
                        while ((line = reader.readLine()) != null) {
                            // Traitement du flux de sortie de l'application si besoin est
                        }
                    } finally {
                        reader.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } .start();

        // Consommation de la sortie d'erreur de l'application externe dans un Thread separe
        new Thread() {
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = "";
                    try {
                        while ((line = reader.readLine()) != null) {
                            System.out.println("line = " + line);
                        }
                    } finally {
                        reader.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } .start();


        process.waitFor();

    }


    public Utterance synthesizeHMM(PyHTSVoice v, Utterance input_utt, Serializer label_serializer,
                                   boolean imposed_dur) throws IOException, InterruptedException, MaryException, UnsupportedAudioFileException {

        if (synth_script_path == null) {
            throw new MaryException("you have to define where is installed pyhts first!", null);
        }

        // Serialize
        File tmp_label = File.createTempFile("pyhts_" + v.getName(), ".lab");

        // write it
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmp_label));
        bw.write(label_serializer.export(input_utt).toString());
        bw.close();

        // Create
        Path tmp_output_dir = Files.createTempDirectory("pyhts_" + v.getName());



        // Prepare the command
        String command = "python3 " + this.synth_script_path + " -c " + v.getConfigurationPath() + " ";
        command += "-p " + this.pg_type + " " + HMM_GENERATION + " -r " + " -v ";
        command += tmp_label + " " + tmp_output_dir;

        // Execute synthesis
        execCommand(command);

        // Retrieve the wave
        String wav_filename = tmp_output_dir + "/" + tmp_label.getName().split("\\.(?=[^\\.]+$)")[0] + ".wav";
        System.out.println("wav_filename = " + wav_filename);
        AudioItem audio_it = new AudioItem(wav_filename);

        // Add the item to the utterance
        Sequence<AudioItem> seq_aud = new Sequence<AudioItem>();
        seq_aud.add(audio_it);
        input_utt.addSequence(SupportedSequenceType.AUDIO, seq_aud);

        // clean
        tmp_label.delete();

        Files.walkFileTree(tmp_output_dir, new SimpleFileVisitor<Path>() {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		    Files.delete(file);
		    return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		    Files.delete(dir);
		    return FileVisitResult.CONTINUE;
		}
            });


        Files.walkFileTree(Paths.get("./tmp"), new SimpleFileVisitor<Path>() {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		    Files.delete(file);
		    return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		    Files.delete(dir);
		    return FileVisitResult.CONTINUE;
		}
	    });
        return input_utt;
    }

}


/* PyHTSWrapper.java ends here */
