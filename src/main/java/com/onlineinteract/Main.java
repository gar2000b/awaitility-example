package com.onlineinteract;

import static org.awaitility.Awaitility.*;
import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.Callable;

/**
 * 
 * Awaitility example. Main app has two tests to determine message
 * availability by simulating asynchronous system calls:
 * 
 * 1. without awaitility. 2. with awaitility.
 * 
 * @author Digilogue
 *
 */
public class Main {

	private String message = null;

	public Main() {
	}

	public void publish(String messageIn) {
		this.message = messageIn;
	}

	public static void main(String[] args) throws InterruptedException {
		Main main = new Main();
		main.test1();
		main.setMessage(null);
		main.test2();
	}

	/**
	 * Test: simulates injecting data into an asynchronous system.
	 * Artificially guessing a set length in time before asserting
	 * against system.
	 * 
	 * @throws InterruptedException
	 */
	private void test1() throws InterruptedException {

		System.out.println("test1():");

		/**
		 * Spawn independent thread to publish a message after 5 secs.
		 */
		new Thread(() -> {
			try {
				Thread.sleep(5000);
				publish("test");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		System.out.println("Fetching message straight away: " + getMessage());
		Thread.sleep(6000);
		System.out.println("Fetching message after 6 secs: " + getMessage());
		System.out.println();
	}

	/**
	 * Test: simulates injecting data into an asynchronous system.
	 * Uses awaitility as a means to determine when the message is
	 * available for consumption / assertion.
	 * 
	 * Awaitility acts like a blocking call which is useful for
	 * testing asynchronous delivery to a system.
	 * 
	 * @throws InterruptedException
	 */
	private void test2() throws InterruptedException {

		System.out.println("test2():");

		/**
		 * Spawn independent thread to publish a message after 5 secs.
		 */
		new Thread(() -> {
			try {
				Thread.sleep(5000);
				publish("test");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		await().atMost(20, SECONDS).until(messageReceived());

		System.out.println("Fetching message once received: " + getMessage());
	}

	private Callable<Boolean> messageReceived() {
		return new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return getMessage() != null && getMessage().length() > 0;
			}
		};
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
