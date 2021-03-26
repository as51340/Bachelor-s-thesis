package Test;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleAliasesEvaluator;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.CLBBox;
import hr.fer.zemris.fpga.StandardLogWriter;
import hr.fer.zemris.fpga.gui.FPGATabX;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;
import hr.fer.zemris.fpga.mapping.FPGAMapper;
import hr.fer.zemris.fpga.mapping.FPGAMapper.FPGAMapperResult;


public class Test3 {
	
	static void testFPGAMapTask(FPGAMapTask mapTask) {
		System.out.println("Printing aliases!");
		for(String s: mapTask.aliases) {
			System.out.println(s);
		}
		System.out.println();
		System.out.println("Printing aliasMap");
		for(var key: mapTask.aliasMap.keySet()) {
			System.out.println("Key: " + key + " Value: " + mapTask.aliasMap.get(key));
		}
		System.out.println();
		System.out.println("Printing variables");
		for(String s: mapTask.variables) {
			System.out.println(s);
		}
		System.out.println("Printing clbs");
		for(FPGAMapTask.CLB clb: mapTask.clbs) {
			System.out.println("Alias: " + clb.alias);
			System.out.println("Decomposed index: " + clb.decomposedIndex);
			System.out.println("Name: " + clb.name);
			for(String i: clb.inputs) {
				System.out.println("Input: " + i);
			}
			System.out.println();
		}
		System.out.println("Printing positions: ");
		for(String key: mapTask.positions.keySet()) {
			System.out.println("Key: " + key + " isPin: " + mapTask.positions.get(key).isPin + " position: " + mapTask.positions.get(key).position);
		}
		System.out.println();
		System.out.println();
	}

	private static void testModelCLBS(CLBBox[] clbs) {
		for(int i = 0; i < clbs.length; i++) {
			System.out.println(clbs[i].alias + " " + clbs[i].title);
		}
		System.out.println();
		System.out.println();
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		String fileName = "./src/main/resources/decomp-example-01.txt"; //wont load with resource as stream
		String text = Files.readString(Paths.get(fileName));
		
		
		SimpleFPGA sfpga = SimpleFPGA.parseFromText(text);

		AtomicBoolean stopRequester = new AtomicBoolean(false);
		
		FPGAModel model = new FPGAModel(2, 2, 2, 3, 1);
		
		FPGAMapTask mapTask = FPGAMapTask.fromSimpleFPGA(sfpga);
//		testFPGAMapTask(mapTask);
		
		//Od tud dolje bi trebao ja mijenjati, da li da koristim ovo ili novo svoje nešto radit?
		FPGAMapperResult result = new FPGAMapper(model, mapTask, new StandardLogWriter()).runMappingProcedure(stopRequester);
		if(result == null) {
			return;
		}
		
		model.simplify(result.configuration);
		
		model.loadFromConfiguration(result.configuration);
		for(int i = 0; i < mapTask.clbs.length; i++) { //sta je to, samo kopiranje lutova iz rezultata?
//			System.out.println("Result clb index[" + i + "]: " + result.clbIndexes[i]);
			model.clbs[result.clbIndexes[i]].setTitle(mapTask.clbs[i].name); //to znači stavi ime, tu se radi ovo "instaliranje" logičkog modela u fizički
			model.clbs[result.clbIndexes[i]].lut = sfpga.getLuts()[mapTask.clbs[i].decomposedIndex]; //prekopiraj cijeli LUT
		}
		for(int i = 0; i < mapTask.variables.length; i++) { //vjv isto samo s pinovima
			model.pins[result.pinIndexes[i]].setTitle(mapTask.variables[i]);
		}
		for(int i = 0; i < mapTask.aliases.length; i++) { //namještavanje labela pinovima
			model.pins[result.pinIndexes[mapTask.variables.length+i]].setTitle(mapTask.aliases[i]);
		}
		
		Evaluator eval = new SimpleAliasesEvaluator();
		double val = eval.evaluate(model, mapTask);
		System.out.println("Value is: " + val);
		
//		testModelCLBS(model.clbs);

		System.out.println("Mapiranje gotovo...");
		
//		SwingUtilities.invokeLater(() -> {
//			JFrame f = new JFrame("Moj GUI visualizer!");
//			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//			f.setSize(500, 500);
//			f.getContentPane().setLayout(new BorderLayout());
//			f.setVisible(true);
//		});
		
		SwingUtilities.invokeLater(()->{
			JFrame f = new JFrame("Preglednik rezultata mapiranja");
			
			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			f.setSize(500, 500);
			f.getContentPane().setLayout(new BorderLayout());
			
			FPGATabX tab = new FPGATabX();
			tab.installFPGAModel(model, sfpga); //zašto mu treba i sfpga za crtanje, zar ne crta samo model?
			
			f.getContentPane().add(tab, BorderLayout.CENTER);
			
			f.setVisible(true);
		});
	}
	
}
