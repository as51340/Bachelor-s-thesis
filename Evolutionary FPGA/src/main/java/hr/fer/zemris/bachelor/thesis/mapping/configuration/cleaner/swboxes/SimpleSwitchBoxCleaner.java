package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes;

import hr.fer.zemris.fpga.FPGAModel;

/**
 * Performs only simplest operations. Does not try to change connection indexes etx.
 * @author andi
 *
 */
public class SimpleSwitchBoxCleaner extends SwitchBoxCleaner{

	public SimpleSwitchBoxCleaner(FPGAModel model) {
		super(model);
	}

	@Override
	public void clean(byte[][][] swConf) {
		for(int i = 0; i < swConf.length; i++) {
			byte[][] conf = swConf[i];
			for(int j = 0; j < swConf[0].length; j++) {
				boolean isEnding = false, isStarting = false;
				for(int k = 0; k < swConf[0][0].length; k++) {
					if(k - j > 0 && (k-j) < model.wiresCount) {
						conf[j][k] = 0;
						conf[k][j] = 0;
					} else {
						if(conf[j][k] == 2) {
							if(isEnding) { //we already have one
								conf[j][k] = 0;
								conf[k][j] = 0;
							} else { //check if we can put down 1
								byte[] scan = conf[k];
								boolean valid = true;
								for(int y = 0; y < scan.length; y++) {
									if(scan[y] != 0) {
										valid = false;
										break;
									}
								}
								if(!valid) {
									conf[j][k] = 0;
									conf[k][j] = 0;
								} else {
									conf[k][j] = 1;
									isStarting = true; //put two
								}
							}
						} else if(conf[j][k] == 1) {
							if(isStarting || isEnding) {
								conf[j][k] = 0;
								conf[k][j] = 0;
							} else { //probaj stavit dole 2
								byte[] scan = conf[k];
								boolean valid = true;
								for(int y = 0; y < scan.length; y++) {
									if(scan[y] == 1) {
										valid = false;
										break;
									}
								}
								if(!valid) {
									conf[j][k] = 0;
									conf[k][j] = 0;
								} else {
									conf[k][j] = 2;
									isEnding = true; //put two
								}
							}
						}
					}
				}
			}
		}
	}

}
