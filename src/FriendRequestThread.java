import java.util.concurrent.Semaphore;

/**
 * FriendRequestThread class implements Runnable to handle friend requests between students.
 * It uses a semaphore to ensure thread-safe operations when sending friend requests.
 */
public class FriendRequestThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;
    //static semaphore to ensure thread-safe friend request operation
    private static final Semaphore semaphore = new Semaphore(1);
    /**
     * Constructor for FriendRequestThread.
     * @param sender The student sending the friend request.
     * @param receiver The student receiving the friend request.
     */
    public FriendRequestThread(UniversityStudent sender, UniversityStudent receiver) {
        // Constructor
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        // Method signature only
        try {
            semaphore.acquire();
            System.out.println("FriendRequest (Thread-Safe): " + sender.name + " sent a friend request to " + receiver.name);
        } catch (InterruptedException e) {
            // Handle exception
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("Thread interrupted: " + e.getMessage());
        } finally {
            semaphore.release();
        }
    }
}