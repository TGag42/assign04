package assign06;

import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for WebBrowser.
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version October 16, 2025
 */
public class WebBrowserTest {

    private WebBrowser browser;
    private URL url1, url2, url3, url4, url5;

    @BeforeEach
    void setUp() throws Exception {
        browser = new WebBrowser();
        url1 = URI.create("https://www.google.com").toURL();
        url2 = URI.create("https://www.github.com").toURL();
        url3 = URI.create("https://www.stackoverflow.com").toURL();
        url4 = URI.create("https://www.youtube.com").toURL();
        url5 = URI.create("https://www.reddit.com").toURL();
    }

    // ========== Constructor Tests ==========
    @Test
    void testDefaultConstructor() {
        WebBrowser newBrowser = new WebBrowser();
        assertNotNull(assertThrows(NoSuchElementException.class, () -> newBrowser.back()));
        assertNotNull(assertThrows(NoSuchElementException.class, () -> newBrowser.forward()));
    }

    @Test
    void testHistoryConstructorEmpty() {
        SinglyLinkedList<URL> history = new SinglyLinkedList<>();
        WebBrowser newBrowser = new WebBrowser(history);
        assertNotNull(assertThrows(NoSuchElementException.class, () -> newBrowser.back()));
    }

    @Test
    void testHistoryConstructorSinglePage() throws Exception {
        SinglyLinkedList<URL> history = new SinglyLinkedList<>();
        history.insertFirst(url1);
        WebBrowser newBrowser = new WebBrowser(history);

        // Should have no back history
        assertNotNull(assertThrows(NoSuchElementException.class, () -> newBrowser.back()));

        // History should contain only current page
        SinglyLinkedList<URL> retrievedHistory = newBrowser.history();
        assertEquals(1, retrievedHistory.size());
        assertEquals(url1.toString(), retrievedHistory.getFirst().toString());
    }

    @Test
    void testHistoryConstructorMultiplePages() throws Exception {
        SinglyLinkedList<URL> history = new SinglyLinkedList<>();
        history.insertFirst(url3); // Oldest
        history.insertFirst(url2); // Middle
        history.insertFirst(url1); // Current (most recent)

        WebBrowser newBrowser = new WebBrowser(history);

        // Verify we can go back
        assertEquals(url2.toString(), newBrowser.back().toString());
        assertEquals(url3.toString(), newBrowser.back().toString());

        // No more back
        assertNotNull(assertThrows(NoSuchElementException.class, () -> newBrowser.back()));
    }

    @Test
    void testHistoryConstructorNull() {
        WebBrowser newBrowser = new WebBrowser(null);
        assertNotNull(assertThrows(NoSuchElementException.class, () -> newBrowser.back()));
    }

    // ========== visit Tests ==========
    @Test
    void testVisitSinglePage() {
        browser.visit(url1);
        SinglyLinkedList<URL> history = browser.history();
        assertEquals(1, history.size());
        assertEquals(url1.toString(), history.getFirst().toString());
    }

    @Test
    void testVisitMultiplePages() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
        assertEquals(url3.toString(), history.get(0).toString()); // Most recent
        assertEquals(url2.toString(), history.get(1).toString());
        assertEquals(url1.toString(), history.get(2).toString()); // Oldest
    }

    @Test
    void testVisitClearsForwardStack() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);

        // Go back twice
        browser.back();
        browser.back();

        // Now we should be able to go forward
        browser.forward(); // Should work

        // Visit new page - this should clear forward stack
        browser.visit(url4);

        // Now forward should throw exception
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.forward()));
    }

    @Test
    void testVisitAfterBackAndForward() {
        browser.visit(url1);
        browser.visit(url2);
        browser.back();
        browser.forward();
        browser.visit(url3);

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
        assertEquals(url3.toString(), history.get(0).toString());
    }

    // ========== back Tests ==========
    @Test
    void testBackOnEmpty() {
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.back()));
    }

    @Test
    void testBackOnSinglePage() {
        browser.visit(url1);
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.back()));
    }

    @Test
    void testBackOnTwoPages() {
        browser.visit(url1);
        browser.visit(url2);

        assertEquals(url1.toString(), browser.back().toString());
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.back()));
    }

    @Test
    void testBackMultipleTimes() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.visit(url4);

        assertEquals(url3.toString(), browser.back().toString());
        assertEquals(url2.toString(), browser.back().toString());
        assertEquals(url1.toString(), browser.back().toString());
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.back()));
    }

    @Test
    void testBackUpdatesForwardStack() {
        browser.visit(url1);
        browser.visit(url2);
        browser.back();

        // Should now be able to go forward
        assertEquals(url2.toString(), browser.forward().toString());
    }

    // ========== forward Tests ==========
    @Test
    void testForwardOnEmpty() {
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.forward()));
    }

    @Test
    void testForwardWithoutBack() {
        browser.visit(url1);
        browser.visit(url2);
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.forward()));
    }

    @Test
    void testForwardAfterBack() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);

        browser.back(); // Go to url2
        browser.back(); // Go to url1

        assertEquals(url2.toString(), browser.forward().toString());
        assertEquals(url3.toString(), browser.forward().toString());
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.forward()));
    }

    @Test
    void testForwardMultipleTimes() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.visit(url4);

        browser.back();
        browser.back();
        browser.back();

        assertEquals(url2.toString(), browser.forward().toString());
        assertEquals(url3.toString(), browser.forward().toString());
        assertEquals(url4.toString(), browser.forward().toString());
    }

    // ========== history Tests ==========
    @Test
    void testHistoryEmpty() {
        SinglyLinkedList<URL> history = browser.history();
        assertEquals(0, history.size());
    }

    @Test
    void testHistorySinglePage() {
        browser.visit(url1);
        SinglyLinkedList<URL> history = browser.history();
        assertEquals(1, history.size());
        assertEquals(url1.toString(), history.getFirst().toString());
    }

    @Test
    void testHistoryMultiplePages() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
        assertEquals(url3.toString(), history.get(0).toString()); // Current
        assertEquals(url2.toString(), history.get(1).toString());
        assertEquals(url1.toString(), history.get(2).toString()); // Oldest
    }

    @Test
    void testHistoryAfterBack() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.back();

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(2, history.size());
        assertEquals(url2.toString(), history.get(0).toString()); // Current after back
        assertEquals(url1.toString(), history.get(1).toString());
    }

    @Test
    void testHistoryDoesNotIncludeForward() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.back(); // Current is url2, forward stack has url3
        browser.back(); // Current is url1, forward stack has url2 and url3

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(1, history.size()); // Only current page
        assertEquals(url1.toString(), history.get(0).toString());
    }

    @Test
    void testHistoryDoesNotModifyBrowser() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);

        // Get history
        SinglyLinkedList<URL> history1 = browser.history();

        // Browser should still work normally
        assertEquals(url2.toString(), browser.back().toString());
        assertEquals(url3.toString(), browser.forward().toString());

        // Get history again
        SinglyLinkedList<URL> history2 = browser.history();

        // Should be the same
        assertEquals(history1.size(), history2.size());
    }

    @Test
    void testHistoryAfterComplexNavigation() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.back();
        browser.back();
        browser.forward();
        browser.visit(url4);

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
        assertEquals(url4.toString(), history.get(0).toString());
        assertEquals(url2.toString(), history.get(1).toString());
        assertEquals(url1.toString(), history.get(2).toString());
    }

    // ========== Complex Navigation Scenarios ==========
    @Test
    void testBackAndForwardSequence() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);

        browser.back(); // url2
        browser.back(); // url1
        browser.forward(); // url2
        browser.forward(); // url3

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(url3.toString(), history.get(0).toString());
    }

    @Test
    void testVisitClearsForwardHistory() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.back(); // url2
        browser.visit(url4); // Should clear url3 from forward

        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.forward()));
    }

    @Test
    void testAlternatingBackForward() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.visit(url4);

        browser.back(); // url3
        browser.forward(); // url4
        browser.back(); // url3
        browser.back(); // url2
        browser.forward(); // url3

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
        assertEquals(url3.toString(), history.get(0).toString());
    }

    @Test
    void testLongBrowsingSession() {
        // Visit many pages
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);
        browser.visit(url4);
        browser.visit(url5);

        // Go back multiple times
        browser.back();
        browser.back();
        browser.back();

        // Forward a bit
        browser.forward();
        browser.forward();

        // Visit new page (clears forward)
        browser.visit(url1);

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(5, history.size());
        assertEquals(url1.toString(), history.get(0).toString());
    }

    @Test
    void testRealBrowserBehavior() {
        // Simulate real browsing
        browser.visit(url1); // google
        browser.visit(url2); // github
        browser.visit(url3); // stackoverflow

        // Realize you wanted to go back to github
        browser.back(); // back to github

        // Actually meant google
        browser.back(); // back to google

        // Now forward to github
        browser.forward(); // forward to github

        // Visit new site (this clears stackoverflow from forward)
        browser.visit(url4); // youtube

        // Try to go forward - should fail
        assertNotNull(assertThrows(NoSuchElementException.class, () -> browser.forward()));

        // Verify history
        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
        assertEquals(url4.toString(), history.get(0).toString());
        assertEquals(url2.toString(), history.get(1).toString());
        assertEquals(url1.toString(), history.get(2).toString());
    }

    // ========== Edge Cases ==========
    @Test
    void testHistoryWithSameURLMultipleTimes() {
        browser.visit(url1);
        browser.visit(url1);
        browser.visit(url1);

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
    }

    @Test
    void testBackToFirstPage() {
        browser.visit(url1);
        browser.visit(url2);

        assertEquals(url1.toString(), browser.back().toString());

        // Verify history shows we're on first page
        SinglyLinkedList<URL> history = browser.history();
        assertEquals(1, history.size());
        assertEquals(url1.toString(), history.get(0).toString());
    }

    @Test
    void testForwardToLastPage() {
        browser.visit(url1);
        browser.visit(url2);
        browser.visit(url3);

        browser.back();
        browser.back();

        browser.forward();
        browser.forward();

        // Should be on url3
        SinglyLinkedList<URL> history = browser.history();
        assertEquals(3, history.size());
        assertEquals(url3.toString(), history.get(0).toString());
    }

    @Test
    void testMultipleHistoryCalls() {
        browser.visit(url1);
        browser.visit(url2);

        SinglyLinkedList<URL> history1 = browser.history();
        SinglyLinkedList<URL> history2 = browser.history();
        SinglyLinkedList<URL> history3 = browser.history();

        // All should be equal and browser should still work
        assertEquals(history1.size(), history2.size());
        assertEquals(history2.size(), history3.size());

        browser.back(); // Should still work
        assertEquals(url1.toString(), browser.history().get(0).toString());
    }
}
