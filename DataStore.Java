package com.router;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataStore {
	public static BlockingQueue<DataStoreBean> feedQueue = new ArrayBlockingQueue<DataStoreBean>(10);
}
