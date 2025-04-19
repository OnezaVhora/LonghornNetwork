import java.util.concurrent.Semaphore;
/**
 * ChatThread class implements Runnable to handle chat messages between students.
 * It uses a semaphore to ensure thread-safe operations when sending messages.
 */
public class ChatThread implements Runnable {
    
    private UniversityStudent sender;
    private UniversityStudent receiver;
    private String message;
    //Static semaphore to ensure thread-safe chat operation
    private static final Semaphore semaphore = new Semaphore(1);
    
    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        // Constructor
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    public void run() {
        // Method signature only
        try{
            semaphore.acquire();    
            System.out.println("Chat (Thread-Safe): " + sender.getName() + " to " + receiver.getName() + ": " + message);        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("Chat interrupted: " + e.getMessage());
        } finally {
            semaphore.release();
        }
    }
}
