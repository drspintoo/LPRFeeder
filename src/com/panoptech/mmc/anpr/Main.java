package com.panoptech.mmc.anpr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.panoptech.mmc.server.exception.NotAuthorisedServerException;

/**
*
* Main is designed as a runnable jar that takes optional command line arguments for
* processing an input CSV file of license plate numbers along with latitudes and longitudes. 
* It will output XML files suitable for the LPR/ANPR Feeder. 
*
* @author Devon P. Smith
*
*/
public class Main implements FlagConstants {
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		CommandLine cmdLine = new CommandLine();
		
		cmdLine.saveFlagValue(DIR_FLAG); // anprDir flag.
		cmdLine.saveFlagValue(CSVFILE_FLAG); // input CSV_filename flag; includes full path.
		cmdLine.saveFlagValue(WAIT_FLAG); // wait_secs flag; time in seconds to wait before sending next batch.
		cmdLine.saveFlagValue(BATCHSIZE_FLAG); // batch_to_send flag; size of batch.
		cmdLine.saveFlagValue(CAMERA_FLAG);  // Name of camera.  Quote if spaces.
		
		cmdLine.parse(args);
		
		checkHelpArg(cmdLine);
		String anprDir = checkDirArg(cmdLine);
		AnprFeeder anprFeeder = new AnprFeeder(anprDir); 
		int wait_secs = checkWaitArg(cmdLine);
		int batch_to_send = checkBatchSizeArg(cmdLine);
		
		String camera = checkCameraArg(cmdLine);  
		
		String CSV_filename = checCsvFileArg(cmdLine);
		/* Sample content of CSV file : 
		"CID355","34.8488655090332","-82.3330078125"
		"CJP5851","35.37506866455078","-80.72057342529297"
		"7GGN023","34.07568359375","-117.55223846435547" */


		List<ArrayList> lprDataList = new ArrayList<ArrayList>();
		BufferedReader lprDataBuffer = null;
		try {
			lprDataBuffer = new BufferedReader(new FileReader(
					CSV_filename));
			String lprDataLine;
			while ((lprDataLine = lprDataBuffer.readLine()) != null) {
				lprDataList.add(readAndParseCSVtoArrayList(lprDataLine));
			}

			int size = 0;
			Enumeration<ArrayList> e = Collections.enumeration(lprDataList);
			// Send a line from the csv file to the Feeder process for XML file creation.
			while (e.hasMoreElements()) {
				ArrayList<?> l = e.nextElement();
				System.out.println("Camera: " + camera + " Plate Number: "
						+ ((LPRTrigger) l.get(0)).plateNumber + " Lat.: "
						+ ((LPRTrigger) l.get(0)).latitude + " Lon.: "
						+ ((LPRTrigger) l.get(0)).longtitude);
				anprFeeder.send(camera,
						((LPRTrigger) l.get(0)).plateNumber,
						((LPRTrigger) l.get(0)).latitude,
						((LPRTrigger) l.get(0)).longtitude);

				++size;
				// Wait 'wait_secs' after/in-between each 'batch_to_send'... 
				if (size % batch_to_send == 0) {
					System.out.println("Waiting " + wait_secs + " secs. after sending " + size
							+ " triggers...");
					wait(wait_secs);
				}

			}

		} catch (IOException | NotAuthorisedServerException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(1);
		} finally {
			try {
				if (lprDataBuffer != null)
					lprDataBuffer.close();
			} catch (IOException csvtoarraylistException) {
				csvtoarraylistException.printStackTrace();
			}
		}
	}

	public static ArrayList<LPRTrigger> readAndParseCSVtoArrayList(
			String lineFromCSV) {
		ArrayList<LPRTrigger> arrLineResult = new ArrayList<LPRTrigger>();

		if (lineFromCSV != null) {
			String[] splitData = lineFromCSV.split("\\s*,\\s*");
			if ((splitData[0] != null) || (splitData[0].length() != 0)) {
				String plate = splitData[0].trim().replaceAll("^\"|\"$", "");
				float lat = Float.valueOf(
						splitData[1].trim().replaceAll("^\"|\"$", ""))
						.floatValue();
				float lon = Float.valueOf(
						splitData[2].trim().replaceAll("^\"|\"$", ""))
						.floatValue();
				System.out.println("Plate : " + plate + " Latitude: " + lat
						+ " Longitude: " + lon);

				arrLineResult.add(new LPRTrigger(plate, lat, lon));
			}
		}

		return arrLineResult;
	}

	public static void wait(int seconds) {
		long waitTime = seconds * 1000000000L;
		long start = System.nanoTime();

		while (System.nanoTime() - start < waitTime)
			;
	}

	static class LPRTrigger {
		String plateNumber;
		float latitude;
		float longtitude;

		public LPRTrigger(String plateNumber, float latitude, float longtitude) {
			this.plateNumber = plateNumber;
			this.latitude = latitude;
			this.longtitude = longtitude;
		}

		public String getPlateNumber() {
			return this.plateNumber;
		}

		public void setPlateNumber(String plateNumber) {
			this.plateNumber = plateNumber;
		}

		public float getLatitude() {
			return this.latitude;
		}

		public void setLatitude(float latitude) {
			this.latitude = latitude;
		}

		public float getLongtitude() {
			return this.longtitude;
		}

		public void setLongtitude(float longtitude) {
			this.longtitude = longtitude;
		}
	}
	
	/**
	 * Check to see if the user provided the help parameter
	 * and display the help message.
	 * 
	 * @param    cmdLine   The <code>CommandLine</code> arguments passed to the Java class' main(String[]) method
	 */
	private static void checkHelpArg(CommandLine cmdLine) {
		if(cmdLine.hasFlag(HELP_FLAG) || cmdLine.hasFlag(ABBR_HELP_FLAG)) {
			System.out.print("Optional FLAGS : ");
			System.out.println(cmdLine.getFlagNames());
			System.out.println("***Default values when not/none supplied :   -wait 5 -batch 10 -csvfile \"/tmp/lprdata.csv\" -dir \"/store/anpr_data\" -camera \"Camera 1\"" );
			System.exit(1);
		}
	}
	
	/**
	 * Check to see if the user provided the output directory parameter
	 * and returns the default location when none is provided.
	 * 
	 * @param    cmdLine   The <code>CommandLine</code> arguments passed to the Java class' main(String[]) method
	 */
	private static String checkDirArg(CommandLine cmdLine) {
		if(cmdLine.hasFlag(DIR_FLAG) && cmdLine.getFlagValue(DIR_FLAG) != null) {
			return cmdLine.getFlagValue(DIR_FLAG); 
		}
		
		// System.out.println( File.separatorChar == '/' ? "Unix" : "Windows" );
		// Default: Linux -- "/store/anpr_data" or Windows -- "C://Windows//Temp"
		if (File.separatorChar == '/') {
			return "/store/anpr_data";
		}
		return "C://Windows//Temp";
	}

	/**
	 * Check to see if the user provided the wait interval parameter
	 * and returns the default wait time in seconds when none is provided.
	 * 
	 * @param    cmdLine   The <code>CommandLine</code> arguments passed to the Java class' main(String[]) method
	 */
	private static int checkWaitArg(CommandLine cmdLine) {
		if(cmdLine.hasFlag(WAIT_FLAG) && cmdLine.getFlagValue(WAIT_FLAG) != null) {
			return Integer.parseInt(cmdLine.getFlagValue(WAIT_FLAG)); 
		}
		return 5;
	}

	/**
	 * Check to see if the user provided the batch size to send/process at each interval
	 * and returns the default ba size when none is provided.
	 * 
	 * @param    cmdLine   The <code>CommandLine</code> arguments passed to the Java class' main(String[]) method
	 */
	private static int checkBatchSizeArg(CommandLine cmdLine) {
		if(cmdLine.hasFlag(BATCHSIZE_FLAG) && cmdLine.getFlagValue(BATCHSIZE_FLAG) != null) {
			return Integer.parseInt(cmdLine.getFlagValue(BATCHSIZE_FLAG)); 
		}
		return 10;
	}
	
	/**
	 * Check to see if the user provided the name of the CSV input file to process
	 * and returns the default CSV file and location when none is provided.
	 * 
	 * @param    cmdLine   The <code>CommandLine</code> arguments passed to the Java class' main(String[]) method
	 */
	private static String checCsvFileArg(CommandLine cmdLine) {
		if(cmdLine.hasFlag(CSVFILE_FLAG) && cmdLine.getFlagValue(CSVFILE_FLAG) != null) {
			return cmdLine.getFlagValue(CSVFILE_FLAG); 
		}
		
		// System.out.println( File.separatorChar == '/' ? "Unix" : "Windows" );
		// Default: Linux -- "/tmp/lprdata.csv" or Windows -- "C://Windows//Temp//lprdata.csv"
		if (File.separatorChar == '/') {
			return "/tmp/lprdata.csv";
		}
		return "C://Windows//Temp//lprdata.csv";
	}
	
	/**
	 * Check to see if the user provided the name of the camera
	 * and returns the default camera name when none is provided.
	 * 
	 * @param    cmdLine   The <code>CommandLine</code> arguments passed to the Java class' main(String[]) method
	 */
	private static String checkCameraArg(CommandLine cmdLine) {
		if(cmdLine.hasFlag(CAMERA_FLAG) && cmdLine.getFlagValue(CAMERA_FLAG) != null) {
			return cmdLine.getFlagValue(CAMERA_FLAG); 
		}
		return "Camera 1";  // "Camera 1" is default. NOTE: Any string returned will have 'VIGILANT_' prepended (eg:'VIGILANT_Camera 1') on the Acuity system.
	}

}