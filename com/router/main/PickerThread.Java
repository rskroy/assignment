package com.router.main;

import com.router.DataStore;
import com.router.algo.IRouting;

/**
 * Thread to pick up parsed input DataStoreBean for processing/routing of bags
 * @author rskroy
 *
 */
public class PickerThread implements Runnable {
	private IRouting routingType;
	public PickerThread(IRouting routingType){
		this.routingType = routingType;
	}
	
	@Override
	public void run() {
		try {
			this.routingType.route(DataStore.feedQueue.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

}
