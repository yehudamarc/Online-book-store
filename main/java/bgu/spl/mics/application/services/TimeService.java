package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Time;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private Time time;
	private int currentTick;
	private Timer myTimer;
	private CountDownLatch latch;

	public TimeService(Time time, CountDownLatch latch) {
		super("TimeService");
		this.time = time;
		this.latch = latch;
		currentTick = 1;
		myTimer = new Timer();
		/* Should it be with the thread name?
		 * How to make it not block? */
	}

	@Override
	protected void initialize() {
		try {
			latch.await();
		}
		catch (InterruptedException e){
			System.out.println("time service interrupted before it started");
		}

		// Update the current time and send time broadcast
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(currentTick < time.getDuration()) {
					sendBroadcast(new TickBroadcast(currentTick));
					currentTick++;
				}
				else {//if duration ends
					sendBroadcast(new TerminationBroadcast());
					myTimer.cancel();
					terminate();
				}
			}
		},0, time.getSpeed());
	}
}
