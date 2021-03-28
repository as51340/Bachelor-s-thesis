package Test;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.zemris.bachelor.thesis.ai.crossover.Crossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.SimpleCrossover;
import hr.fer.zemris.bachelor.thesis.ai.gui.EvolutionaryFPGAGUIMaker;
import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
import hr.fer.zemris.bachelor.thesis.ai.mutation.SimpleMutation;
import hr.fer.zemris.bachelor.thesis.mapping.AIFPGAMapper;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAAdvancedConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SimpleSwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.StandardLogWriter;
import hr.fer.zemris.fpga.gui.FPGATabX;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Hope for the best
 * 
 * @author andi
 *
 */
public class NewTestSimpleGenetic {
	

	public static void main(String[] args) throws IOException {
		int rows = 2, columns = 2, pins = 1, variables = 2, wires = 3;
		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		String fileName = "./src/main/resources/decomp-example-01.txt"; // wont load with resource as stream
		String text = Files.readString(Paths.get(fileName));
		SimpleFPGA sfpga = SimpleFPGA.parseFromText(text);

		LogWriter logger = new StandardLogWriter();

		AIFPGAMapper mapper = new AIFPGAMapper(model, sfpga, logger);

		FPGAModel resultModel = mapper.map();
		
		SwingUtilities.invokeLater(() -> {
			new EvolutionaryFPGAGUIMaker(mapper.alg.genToBest, mapper.alg.genToAvg).setVisible(true);
		});
		
		if (resultModel == null) {
			logger.log("Result model is null and cannot be seen!\n");
			return;
		}
		
		

		SwingUtilities.invokeLater(() -> {
			JFrame f = new JFrame("Preglednik rezultata mapiranja");

			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			f.setSize(500, 500);
			f.getContentPane().setLayout(new BorderLayout());

			FPGATabX tab = new FPGATabX();
			tab.installFPGAModel(resultModel, sfpga);

			f.getContentPane().add(tab, BorderLayout.CENTER);

			f.setVisible(true);
		});

	}

}
