package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes;

import hr.fer.zemris.fpga.FPGAModel;

/**
 * This algorithm will try, if it impossible to add switched wires with given
 * indexes, to swap indexes. 2 with 1 or 1 with 2.
 * 
 * @author andi
 *
 */
public class AdvancedSwitchBoxCleaner extends SwitchBoxCleaner {

	public AdvancedSwitchBoxCleaner(FPGAModel model) {
		super(model);
	}

	@Override
	public void clean(byte[][] conf) {
		boolean swapped = false;
		for (int j = 0; j < conf.length; j++) {
			boolean isEnding = false, isStarting = false;
			for (int k = 0; k < conf[0].length; k++) {
				int group1 = j / model.wiresCount;
				int group2 = k / model.wiresCount;
				if (group1 == group2) {
					conf[j][k] = 0;
					conf[k][j] = 0;
				} else {
					if (conf[j][k] == 2) {
						if (isEnding) { // we already have one
							if (swapped) { // if it is already swapped
								swapped = false;
								conf[j][k] = 0;
								conf[k][j] = 0;
							} else {
								swapped = true; // now we will try to swap
								conf[j][k] = 1; // change indexes
								conf[k][j] = 2; // change indexes
								k--; // check that cell again
							}
						} else { // check if we can put down 1
							byte[] scan = conf[k];
							boolean valid = true;
							for (int y = 0; y < j; y++) {
								if (scan[y] != 0) {
									valid = false;
									break;
								}
							}
							if (!valid) { //same fail for case 2 but now rowScan is invalid
								if (swapped) { // if it is already swapped
									swapped = false;
									conf[j][k] = 0;
									conf[k][j] = 0;
								} else {
									swapped = true; // now we will try to swap
									conf[j][k] = 1; // change indexes
									conf[k][j] = 2; // change indexes
									k--; // check that cell again
								}
							} else {
								conf[k][j] = 1;
								isStarting = true; // put two
							}
						}
					} else if (conf[j][k] == 1) {
						if(isEnding) { //already one, no help
							conf[j][k] = 0;
							conf[k][j] = 0;
						} else if(isStarting) { //try to put 2 up and one 1=swap
							if (swapped) { // if it is already swapped
								swapped = false;
								conf[j][k] = 0;
								conf[k][j] = 0;
							} else {							
								swapped = true; //now we will try to swap
								conf[j][k] = 2; //change indexes
								conf[k][j] = 1; //change indexes
								k--; //check that cell again
							}
						}
						else { // probaj stavit dole 2
							byte[] scan = conf[k];
							boolean valid = true;
							for (int y = 0; y < j; y++) {
								if (scan[y] == 1) {
									valid = false;
									break;
								}
							}
							if (!valid) { //we cannot try swap becaue 1 is down so we don't have valid swap try
								conf[j][k] = 0;
								conf[k][j] = 0;
							} else {
								conf[k][j] = 2;
								isEnding = true; // put two
							}
						}
					}
				}
			}
		}
	}

}
