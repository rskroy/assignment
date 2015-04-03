package com.router.filehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.router.DataStore;
import com.router.DataStoreBean;

/**
 * File scanner for type 1 files
 * @author Rahul
 *
 */
public class FileScannerType1 implements IScannerType{
	
	public void scanFile(File file){
		Integer currentSection = 0;
		FileInputStream inputStream = null;
		Scanner sc = null;
    	StringBuilder conveyor = new StringBuilder();
    	StringBuilder bags = new StringBuilder();
    	StringBuilder departures = new StringBuilder();
    	try {
			inputStream = new FileInputStream(file);
	    	
			sc = new Scanner(inputStream, "UTF-8");
		    while (sc.hasNextLine()) {
		        String line = sc.nextLine();
		        if(line.contains("Section: Conveyor System")){
					currentSection = 1;
				}else if(line.contains("Section: Departures")){
					currentSection = 2;
				}else if(line.contains("Section: Bags")){
					currentSection = 3;
				}else if(currentSection == 1){
					conveyor.append(line);
					conveyor.append("\n");
				}else if(currentSection == 2){
					departures.append(line);
					departures.append("\n");
				}else if(currentSection == 3){
					bags.append(line);
					bags.append("\n");
				}
		    }
		    DataStoreBean bean = new DataStoreBean(conveyor.toString(), departures.toString(), bags.toString(), 1);
		    try {
				DataStore.feedQueue.put(bean);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			file.delete();
			sc.close();
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
