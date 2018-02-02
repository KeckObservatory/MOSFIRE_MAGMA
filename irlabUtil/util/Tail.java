package edu.ucla.astro.irlab.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;


//. the core algorithm of this code was taken from a berkeley message board, 
//. posted by Pankaj Jain (jpankaj@vayusphere.com) 23-April-2002


public class Tail {
	String filename;
	HashSet<TailListener> tailListeners = new HashSet<TailListener>(); 
	TailThread tailThread;
	boolean tailStarted = false;
	
	public Tail() {
		this("");
	}
	public Tail(String filename) {
		this.filename=filename;
		tailThread = new TailThread();
		tailThread.start();
	}
	public void addTailListener(TailListener listener) {
		tailListeners.add(listener);
	}
	public void removeTailListener(TailListener listener) {
		tailListeners.remove(listener);
	}
	public void setFilename(String filename) {
		this.filename = filename;
		stopReading();
	}
	public void startReading() throws FileNotFoundException {
		startReading(filename);
	}
	
	public void startReading(String name) throws FileNotFoundException {
		if (tailStarted) {
			resumeReading();
		} else {
			tailThread.setTailFilename(name);
			tailThread.startTail();
			tailStarted = true;
		}
	}
	public void resumeReading() {
		tailThread.startTail();
	}
	public void pauseReading() {
		tailThread.stopTail();
	}
	public void stopReading() {
		tailThread.stopTail();
		tailStarted=false;
	}
	void log(byte [] data) {
		System.out.println("<"+new String(data)+">");
	}
	
	class TailThread extends Thread {
		String name = "";
		volatile boolean doTail = false;
		volatile BufferedInputStream bis;
		volatile boolean threadRunning = true;
		
		public void setTailFilename(String filename) throws FileNotFoundException {
			name = filename;
			bis = new BufferedInputStream(new FileInputStream(name));
		}
		public void startTail() {
			doTail = true;
		}
		public void stopTail() {
			doTail = false;
		}
		public void stopThread() {
			threadRunning = false;
		}
		public void run() {
			int length = -1;
			int counter=0;
			threadRunning = true;
			while (threadRunning) {
				while(doTail) {
					try {
						length = bis.available();
						if(length == -1) throw new IOException("file ended");
						if(length <= 0) continue;
						byte [] data = new byte[length];
						if(bis.read(data, 0, length ) == -1) continue;
						if (tailListeners.isEmpty()) {
							log(data);								
						} else {
							Iterator<TailListener> itTL = tailListeners.iterator();
							while (itTL.hasNext()) {
								itTL.next().receiveNewData(data);
							}
						}
						counter++;
						// System.out.println("tail thread counter = "+counter);
						Thread.sleep(10);
					} catch(Exception ex) {
						//. todo: improve, but how?  exceptionListeners?
						ex.printStackTrace();
						doTail=false;
					}
				}
			}
		}
	}
	
	public static void main(String [] args) {
		Tail t = new Tail();
		t.setFilename(args[0]);
		try {
			t.startReading();
			Thread.currentThread().sleep(5000);
			t.stopReading();
			Thread.currentThread().sleep(5000);
			t.resumeReading();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
