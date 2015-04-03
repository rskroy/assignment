package com.router.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.router.DataStore;
import com.router.algo.IRouting;
import com.router.algo.OptimalRouting;
import com.router.filehandler.FileScannerType1;
import com.router.filehandler.IScannerType;

/**
 * To run:
 * Input files having "Type1" in the file name will be picked up processed from the project root directory.
 * 
 * Logic:
 * Main class to launch the application.
 * Exception handling is more of like not processing the corrupt file lines.
 * Thread pool has been used so that the application could scale in case there are more number of input files. Basically here 2 thread classes
 * have been used, 
 * 		1. Thread to pick the input files parse it and put it in the feed queue for further processing
 * 		2. Thread to pick the parsed text and apply the rule (shortest path algo) and display the output
 * This application has been designed to keep waiting for input files and process those asa available.
 * Shortest path is calculated using implementation of Dijkstra algorithm.
 * File parsing and data processing logic are very loosely coupled.  
 * @author rskroy
 *
 */
public class BaggageRouter {
	
	private static String inputFolder;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		inputFolder = args[0];
//		inputFolder = "C:\\Users\\RskRoy\\Documents\\Study\\EclipseWorkspace\\BarAssignment\\files";
		FilePicker pickerThread = new BaggageRouter().new FilePicker(inputFolder);
		pickerThread.run();
	}
	
	class FilePicker implements Runnable{
		
		public FilePicker(String threadName){
			
		}
		@Override
		public void run() {
			if(inputFolder == null){
				inputFolder = System.getProperty("user.dir");
			}
			File folderPath = new File(inputFolder);
			ExecutorService exec = Executors.newFixedThreadPool(4);
			FeederThread feeder = null;
			PickerThread picker = null;
			List<File> usedFile = new ArrayList<File>();
			while(true){
				if(folderPath.exists() && folderPath.isDirectory()){
					File[] listOfFiles = folderPath.listFiles();
					if(DataStore.feedQueue.remainingCapacity() > 0){
						// Don't want the threads to be WAITING at the PUT operation
						if(listOfFiles.length > 0){
							for (File file : listOfFiles) {
							    if (file.isFile() && !usedFile.contains(file)) {
							    	
							        // Get from thread pool and copy DataStoreBean in the feeder queue
							    	
							    	IScannerType scannerContext = getContextBasedOnFileName(file);
							    	if(scannerContext != null){
								    	feeder = new FeederThread(file, scannerContext);
								    	exec.execute(feeder);
								    	usedFile.add(file);
							    	}
							    }
							}
						}else{
							// Wait for new files
							try {
								System.out.println("Threads Waiting for files to arrive......");
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}else{
						try {
							System.out.println("Threads Waiting for Feeder Queue to be empty......");
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
				
				// Start Picker Thread, Using from the same Thread pool as the Feeder thread
				if(!DataStore.feedQueue.isEmpty()){
					picker = new PickerThread(getRoutingType());
					exec.execute(picker);
				}
				
			} // End While
		}
		
		private IRouting getRoutingType() {
			// TODO: Add dummy logic here to pick the Routing type, should be user defined logic
			return new OptimalRouting();
		}
		/**
		 * Get File scanner type based on the File name
		 * @param file
		 * @return
		 */
		private IScannerType getContextBasedOnFileName(File file) {
			IScannerType scannerContext = null;
			if(file.getName().contains("type1")){
				scannerContext = new FileScannerType1();
			}
			return scannerContext;
		}

	}

}
