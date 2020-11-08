package bgu.spl.mics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private static class MessageBusImplInstanceHolder{
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private static final Logger logger = LogManager.getLogger(MessageBus.class.getSimpleName());
    private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> workersQueue;
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> workersMessages;
    private ConcurrentHashMap<Message, Future<?>> messagesFutures;

    private MessageBusImpl() {
        workersQueue = new ConcurrentHashMap<>();
        workersMessages = new ConcurrentHashMap<>();
        messagesFutures = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return MessageBusImplInstanceHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        subscribeTo(type, m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        subscribeTo(type, m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        ((Future<T>) messagesFutures.get(e)).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        logger.debug("Sending a broadcast message: " + b.getClass().getSimpleName());
        if (areSubscribedTo(b.getClass())) {
            synchronized (workersQueue.get(b.getClass())) {
                if (areListeningTo(b.getClass())) {
                    for (MicroService ms : workersQueue.get(b.getClass())) {
                        workersMessages.get(ms).add(b);
                    }
                }
            }
        } else {
            logger.warn("There aren't micro services that are listening to: " + b.getClass().getSimpleName());
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        // Verifying there is a micro service listening to the given event
        if(areSubscribedTo(e.getClass())) {
            synchronized (workersQueue.get(e.getClass())) {
                if (areListeningTo(e.getClass())) {

                    Future<T> future = new Future<>();
                    messagesFutures.put(e, future);

                    // Adding the event to the micro service at the head of the queue
                    workersMessages.get(workersQueue.get(e.getClass()).peek()).add(e);

                    // Implementing round robin - removing the head of the queue and adding it as the last element
                    workersQueue.get(e.getClass()).add(workersQueue.get(e.getClass()).poll());

                    return future;
                }
            }
        }

        logger.warn("There aren't micro services that are listening to: " + e.getClass().getSimpleName());
        return null;
    }

    @Override
    public void register(MicroService m) {
        logger.info("Register a micro service: " + m.getName());
        workersMessages.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        logger.info("Unregister a micro service: " + m.getName());
        for (Class<? extends Message> cl : workersQueue.keySet()) {
            if (isMicroServiceSubscribedTo(m, cl)) {
                synchronized (workersQueue.get(cl)) {
                    workersQueue.get(cl).remove(m);
                }
            }
        }

        if (hasMicroServiceBeenRegistered(m)) {
            for (Message message: workersMessages.get(m)) {
                messagesFutures.get(message).resolve(null);
            }
            workersMessages.remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (!hasMicroServiceBeenRegistered(m)) {
            throw new IllegalStateException();
        }

        logger.debug("Micro service " + m.getName() + " is waiting for messages");
        return workersMessages.get(m).take();
    }

    /**
     * Returns true if the given micro service has registered and was given a message-queue, false otherwise
     *
     * @param ms The micro service to verify that was registered to the message bus
     * @return True if the given micro service has registered and was given a message-queue, false otherwise
     */
    private boolean hasMicroServiceBeenRegistered(MicroService ms) {
        return workersMessages.containsKey(ms);
    }

    /**
     * Returns true if the given micro service has been subscribed to the given message type, false otherwise
     *
     * @param ms The micro service to verify that was subscribed to he given message type
     * @return True if the given micro service has been subscribed to the given message type, false otherwise
     */
    private boolean isMicroServiceSubscribedTo(MicroService ms, Class<? extends Message> messageType) {
        return areListeningTo(messageType) && workersQueue.get(messageType).contains(ms);
    }

    /**
     * Returns true if there are one or more micro services listening to the given message type, false otherwise
     *
     * @param messageType The message type to check if one or more micro services are listening to
     * @return True if there are one or more micro services listening to the given message type, false otherwise
     */
    private boolean areListeningTo(Class<? extends Message> messageType) {
        return !workersQueue.get(messageType).isEmpty();
    }

    private boolean areSubscribedTo(Class<? extends Message> messageType) {
        return workersQueue.containsKey(messageType);
    }

    /**
     * Putting the message type as the key of the micro services queue which are listening to this message type if absent,
     * and adding the given micro service to this queue.
     *
     * @param type The message type to subscribe to
     * @param m    The micro service that should be listening to the given message type
     */
    private void subscribeTo(Class<? extends Message> type, MicroService m) {
        this.workersQueue.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        this.workersQueue.get(type).add(m);
    }
}
