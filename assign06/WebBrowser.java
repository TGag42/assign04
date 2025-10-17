package assign06;

import java.net.URL;
import java.util.NoSuchElementException;

/**
 * Simulates a simple web browser that supports visiting pages,
 * and navigating backward and forward through browsing history.
 * 
 * <p>This class uses two stacks — one for the back history and one
 * for the forward history — to model a browser's navigation behavior.</p>
 * 
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 16, 2025
 */
public class WebBrowser {

    /** Stack that maintains the history of previously visited pages. */
    private final Stack<URL> backStack;

    /** Stack that maintains the forward navigation history. */
    private final Stack<URL> forwardStack;

    /** The current webpage being viewed. */
    private URL currentPage;

    /**
     * Constructs a new {@code WebBrowser} with no previously visited
     * or forward pages.
     */
    public WebBrowser() {
        this.backStack = new LinkedListStack<>();
        this.forwardStack = new LinkedListStack<>();
        this.currentPage = null;
    }

    /**
     * Constructs a new {@code WebBrowser} initialized with a given browsing history.
     * The first URL in the provided list becomes the current page, and the remaining
     * URLs are added to the back history in order from most recently to least recently visited.
     *
     * @param history a list of {@code URL} objects representing browsing history
     */
    public WebBrowser(SinglyLinkedList<URL> history) {
        this.backStack = new LinkedListStack<>();
        this.forwardStack = new LinkedListStack<>();

        if (history == null || history.isEmpty()) {
            this.currentPage = null;
            return;
        }

        // The first page in the list is the current page
        this.currentPage = history.getFirst();

        // Remaining pages become the back stack history
        for (int i = 1; i < history.size(); i++) {
            backStack.push(history.get(i));
        }
    }

    /**
     * Simulates visiting a new webpage. The current page is pushed onto the back stack,
     * and the forward stack is cleared since no future pages exist after visiting a new one.
     *
     * @param webpage the {@code URL} to visit
     */
    public void visit(URL webpage) {
        if (currentPage != null) {
            backStack.push(currentPage);
        }
        currentPage = webpage;
        forwardStack.clear();
    }

    /**
     * Simulates using the back button to navigate to the previously visited page.
     * The current page is pushed to the forward stack and the previous page becomes current.
     *
     * @return the {@code URL} navigated to after going back
     * @throws NoSuchElementException if there is no previous page
     */
    public URL back() throws NoSuchElementException {
        if (backStack.isEmpty()) {
            throw new NoSuchElementException("No previous page to go back to");
        }
        if (currentPage != null) {
            forwardStack.push(currentPage);
        }
        currentPage = backStack.pop();
        return currentPage;
    }

    /**
     * Simulates using the forward button to return to a page that was
     * previously navigated away from using the back button.
     *
     * @return the {@code URL} navigated to after going forward
     * @throws NoSuchElementException if there is no forward page available
     */
    public URL forward() throws NoSuchElementException {
        if (forwardStack.isEmpty()) {
            throw new NoSuchElementException("No next page to go forward to");
        }
        if (currentPage != null) {
            backStack.push(currentPage);
        }
        currentPage = forwardStack.pop();
        return currentPage;
    }

    /**
     * Generates a browsing history list of visited pages.
     * The list is ordered from the most recently visited to the least recently visited,
     * including the current page, but excluding any forward pages.
     * 
     * <p>This method does not modify the state of the browser.</p>
     *
     * @return a {@code SinglyLinkedList<URL>} containing the browsing history
     * @throws NoSuchElementException if the browser has no history
     */
    public SinglyLinkedList<URL> history() {
        SinglyLinkedList<URL> historyList = new SinglyLinkedList<>();

        // Add the current page first
        if (currentPage != null) {
            historyList.insertFirst(currentPage);
        }

        // Use a temporary stack to reverse and restore the backStack
        Stack<URL> tempStack = new LinkedListStack<>();

        // Transfer all items to tempStack (reverses order)
        while (!backStack.isEmpty()) {
            tempStack.push(backStack.pop());
        }

        // Rebuild backStack and historyList
        while (!tempStack.isEmpty()) {
            URL page = tempStack.pop();
            backStack.push(page);
            historyList.insert(historyList.size(), page);
        }

        return historyList;
    }
}
