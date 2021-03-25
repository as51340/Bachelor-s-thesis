package test.hr.fer.zemris.fpga;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.StandardLogWriter;
import hr.fer.zemris.fpga.gui.FPGATabX;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;
import hr.fer.zemris.fpga.mapping.FPGAMapper;
import hr.fer.zemris.fpga.mapping.FPGAMapper.FPGAMapperResult;

public class Test3 {

	public static void main(String[] args) throws IOException {
		String text = Files.readString(Paths.get("./decomp-example-02.txt"));
		
		SimpleFPGA sfpga = SimpleFPGA.parseFromText(text);

		AtomicBoolean stopRequester = new AtomicBoolean(false);
		
		FPGAModel model = new FPGAModel(2, 2, 2, 3, 1);
		
		FPGAMapTask mapTask = FPGAMapTask.fromSimpleFPGA(sfpga);
		
		FPGAMapperResult result = new FPGAMapper(model, mapTask, new StandardLogWriter()).runMappingProcedure(stopRequester);
		if(result == null) {
			return;
		}
		
		model.simplify(result.configuration);
		
		model.loadFromConfiguration(result.configuration);
		for(int i = 0; i < mapTask.clbs.length; i++) {
			model.clbs[result.clbIndexes[i]].setTitle(mapTask.clbs[i].name);
			model.clbs[result.clbIndexes[i]].lut = sfpga.getLuts()[mapTask.clbs[i].decomposedIndex];
		}
		for(int i = 0; i < mapTask.variables.length; i++) {
			model.pins[result.pinIndexes[i]].setTitle(mapTask.variables[i]);
		}
		for(int i = 0; i < mapTask.aliases.length; i++) {
			model.pins[result.pinIndexes[mapTask.variables.length+i]].setTitle(mapTask.aliases[i]);
		}

		System.out.println("Mapiranje gotovo...");
		
		SwingUtilities.invokeLater(()->{
			JFrame f = new JFrame("Preglednik rezultata mapiranja");
			
			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			f.setSize(500, 500);
			f.getContentPane().setLayout(new BorderLayout());
			
			FPGATabX tab = new FPGATabX();
			tab.installFPGAModel(model, sfpga);
			
			f.getContentPane().add(tab, BorderLayout.CENTER);
			
			f.setVisible(true);
		});
	}
	
}
