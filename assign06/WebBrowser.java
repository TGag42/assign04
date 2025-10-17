package assign06;

import java.net.URL;
import java.util.NoSuchElementException;

/**
 * Simulates a web browser with back and forward navigation functionality. Uses
 * two stacks to maintain browsing history and forward history.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 16, 2025
 */
public class WebBrowser {

    /**
     * Stack to maintain history of previously visited pages (for back button).
     */
    private final Stack<URL> backStack;

    /**
     * Stack to maintain forward navigation history (for forward button).
     */
    private final Stack<URL> forwardStack;

    /**
     * The current webpage being viewed.
     */
    private URL currentPage;

    /**
     * Constructs a new web browser with no history.
     */
    public WebBrowser() {
        this.backStack = new LinkedListStack<>();
        this.forwardStack = new LinkedListStack<>();
        this.currentPage = null;
    }

    /**
     * Constructs a new web browser with a preloaded history of visited
     * webpages. The first webpage in the list is the "current" page, and the
     * remaining webpages are ordered from most recently visited to least
     * recently visited.
     *
     * @param history list of URLs representing browsing history
     */
    public WebBrowser(SinglyLinkedList<URL> history) {
        this.backStack = new LinkedListStack<>();
        this.forwardStack = new LinkedListStack<>();

        if (history == null || history.isEmpty()) {
            this.currentPage = null;
            return;
        }

        // First element becomes current page
        this.currentPage = history.getFirst();

        // Remaining elements go into back stack in reverse order
        // (push from last to first, so when we pop we get them in correct order)
        for (int i = history.size() - 1; i >= 1; i--) {
            backStack.push(history.get(i));
        }
    }

    /**
     * Simulates visiting a new webpage. This clears the forward stack since
     * visiting a new page means there are no longer any "next" pages to visit.
     *
     * @param webpage the URL to visit
     */
    public void visit(URL webpage) {
        // If we already have a current page, push it to back stack
        if (currentPage != null) {
            backStack.push(currentPage);
        }

        // Set the new current page
        currentPage = webpage;

        // Clear forward stack (like a real browser)
        forwardStack.clear();
    }

    /**
     * Simulates clicking the back button. Moves to the previously visited
     * webpage.
     *
     * @return the URL that is now current after going back
     * @throws NoSuchElementException if there is no previously-visited URL
     */
    public URL back() throws NoSuchElementException {
        if (backStack.isEmpty()) {
            throw new NoSuchElementException("No previous page to go back to");
        }

        // Push current page to forward stack
        if (currentPage != null) {
            forwardStack.push(currentPage);
        }

        // Pop from back stack and make it current
        currentPage = backStack.pop();

        return currentPage;
    }

    /**
     * Simulates clicking the forward button. Moves to the next webpage in
     * history.
     *
     * @return the URL that is now current after going forward
     * @throws NoSuchElementException if there is no URL to visit next
     */
    public URL forward() throws NoSuchElementException {
        if (forwardStack.isEmpty()) {
            throw new NoSuchElementException("No next page to go forward to");
        }

        // Push current page to back stack
        if (currentPage != null) {
            backStack.push(currentPage);
        }

        // Pop from forward stack and make it current
        currentPage = forwardStack.pop();

        return currentPage;
    }

    /**
     * Generates a history of URLs visited, as a list ordered from most recently
     * visited to least recently visited (including the current page). Forward
     * links are not included. This method does not alter the browser's state.
     *
     * Time complexity: O(N) where N is the number of URLs in history
     *
     * @return a list of URLs in browsing history
     */
    public SinglyLinkedList<URL> history() {
        SinglyLinkedList<URL> historyList = new SinglyLinkedList<>();

        // Add current page first
        if (currentPage != null) {
            historyList.insertFirst(currentPage);
        }

        // We need to get all items from back stack without modifying it
        // Use a temporary stack to preserve order
        Stack<URL> tempStack = new LinkedListStack<>();

        // Pop all items from back stack to temp stack, adding to history as we go
        while (!backStack.isEmpty()) {
            URL page = backStack.pop();
            historyList.insert(historyList.size(), page); // Add to history in correct order
            tempStack.push(page);
        }

        // Restore back stack from temp stack
        while (!tempStack.isEmpty()) {
            backStack.push(tempStack.pop());
        }

        return historyList;
    }
}
