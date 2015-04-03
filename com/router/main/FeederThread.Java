package com.router.main;

import java.io.File;

import com.router.filehandler.IScannerType;

public class FeederThread implements Runnable {
	private File file;
	IScannerType scannerContext;
	public FeederThread(File file, IScannerType scannerContext){
		this.file = file;
		this.scannerContext = scannerContext;
	}
	
	@Override
	public void run() {
		this.scannerContext.scanFile(file);
	}

}
