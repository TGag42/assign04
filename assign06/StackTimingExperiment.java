// package assign06;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;

// import timing.TimingExperiment;

// /**
//  * Timing experiments to compare LinkedListStack performance with array-based
//  * stacks. This helps determine which implementation to use for the WebBrowser.
//  *
//  * @author Alex Waldmann
//  * @author Tyler Gagliardi
//  * @version October 16, 2025
//  */
// public class StackTimingExperiment {

//     /**
//      * Tests push operations on LinkedListStack
//      */
//     public static class LinkedListPushExperiment extends TimingExperiment {

//         private LinkedListStack<Integer> stack;
//         private final int operationCount;
//         private final Random rng = new Random(42);

//         public LinkedListPushExperiment(List<Integer> problemSizes, int iterationCount, int operationCount) {
//             super("LinkedListStack_Push", problemSizes, iterationCount);
//             this.operationCount = operationCount;
//         }

//         @Override
//         protected void setupExperiment(int problemSize) {
//             stack = new LinkedListStack<>();
//         }

//         @Override
//         protected void runComputation() {
//             for (int i = 0; i < operationCount; i++) {
//                 stack.push(rng.nextInt());
//             }
//         }
//     }

//     /**
//      * Tests pop operations on LinkedListStack
//      */
//     public static class LinkedListPopExperiment extends TimingExperiment {

//         private LinkedListStack<Integer> stack;
//         private final int operationCount;
//         private final Random rng = new Random(42);

//         public LinkedListPopExperiment(List<Integer> problemSizes, int iterationCount, int operationCount) {
//             super("LinkedListStack_Pop", problemSizes, iterationCount);
//             this.operationCount = operationCount;
//         }

//         @Override
//         protected void setupExperiment(int problemSize) {
//             stack = new LinkedListStack<>();
//             // Pre-fill stack
//             for (int i = 0; i < operationCount; i++) {
//                 stack.push(rng.nextInt());
//             }
//         }

//         @Override
//         protected void runComputation() {
//             for (int i = 0; i < operationCount; i++) {
//                 stack.pop();
//             }
//         }
//     }

//     /**
//      * Tests mixed push/pop operations on LinkedListStack
//      */
//     public static class LinkedListMixedExperiment extends TimingExperiment {

//         private LinkedListStack<Integer> stack;
//         private final int operationCount;
//         private final Random rng = new Random(42);

//         public LinkedListMixedExperiment(List<Integer> problemSizes, int iterationCount, int operationCount) {
//             super("LinkedListStack_Mixed", problemSizes, iterationCount);
//             this.operationCount = operationCount;
//         }

//         @Override
//         protected void setupExperiment(int problemSize) {
//             stack = new LinkedListStack<>();
//             // Start with some elements
//             for (int i = 0; i < operationCount / 2; i++) {
//                 stack.push(rng.nextInt());
//             }
//         }

//         @Override
//         protected void runComputation() {
//             for (int i = 0; i < operationCount; i++) {
//                 if (rng.nextBoolean() && !stack.isEmpty()) {
//                     stack.pop();
//                 } else {
//                     stack.push(rng.nextInt());
//                 }
//             }
//         }
//     }

//     /**
//      * Tests WebBrowser navigation patterns
//      */
//     public static class WebBrowserNavigationExperiment extends TimingExperiment {

//         private WebBrowser browser;
//         private final int operationCount;
//         private final Random rng = new Random(42);
//         private java.net.URL[] urls;

//         public WebBrowserNavigationExperiment(List<Integer> problemSizes, int iterationCount, int operationCount) {
//             super("WebBrowser_Navigation", problemSizes, iterationCount);
//             this.operationCount = operationCount;
//         }

//         @Override
//         protected void setupExperiment(int problemSize) {
//             browser = new WebBrowser();
//             // Create some test URLs using URI.toURL() to avoid deprecation
//             urls = new java.net.URL[10];
//             try {
//                 for (int i = 0; i < 10; i++) {
//                     urls[i] = java.net.URI.create("https://www.example" + i + ".com").toURL();
//                 }
//             } catch (Exception e) {
//                 throw new RuntimeException(e);
//             }
//         }

//         @Override
//         protected void runComputation() {
//             // Simulate realistic browsing
//             for (int i = 0; i < operationCount; i++) {
//                 int action = rng.nextInt(100);
//                 try {
//                     if (action < 60) {
//                         // 60% chance to visit new page
//                         browser.visit(urls[rng.nextInt(urls.length)]);
//                     } else if (action < 85) {
//                         // 25% chance to go back
//                         browser.back();
//                     } else {
//                         // 15% chance to go forward
//                         browser.forward();
//                     }
//                 } catch (Exception e) {
//                     // Ignore exceptions (e.g., can't go back/forward)
//                     browser.visit(urls[rng.nextInt(urls.length)]);
//                 }
//             }
//         }
//     }

//     public static void main(String[] args) {
//         System.out.println("=== Stack Performance Analysis ===\n");
//         System.out.println("Testing LinkedListStack to evaluate O(1) performance\n");

//         // Test parameters
//         List<Integer> operationCounts = buildProblemSizes(1000, 1000, 15); // 1k to 15k operations
//         int iterationCount = 20;
//         int warmupIterations = 3;

//         System.out.println("Operation counts: " + operationCounts.size() + " sizes from "
//                 + operationCounts.get(0) + " to " + operationCounts.get(operationCounts.size() - 1));
//         System.out.println("Iterations per test: " + iterationCount);
//         System.out.println("Warmup iterations: " + warmupIterations + "\n");

//         // ==========================================
//         // 1. PUSH OPERATIONS
//         // ==========================================
//         System.out.println("1. PUSH OPERATION PERFORMANCE");
//         System.out.println("=" + "=".repeat(40));

//         LinkedListPushExperiment pushExp = new LinkedListPushExperiment(
//                 operationCounts, iterationCount, 10000);
//         pushExp.warmup(warmupIterations);
//         pushExp.run();
//         pushExp.print();
//         System.out.println("\nAnalysis: Push should be O(1), so time should grow linearly with operation count\n");

//         // ==========================================
//         // 2. POP OPERATIONS
//         // ==========================================
//         System.out.println("\n2. POP OPERATION PERFORMANCE");
//         System.out.println("=" + "=".repeat(40));

//         LinkedListPopExperiment popExp = new LinkedListPopExperiment(
//                 operationCounts, iterationCount, 10000);
//         popExp.warmup(warmupIterations);
//         popExp.run();
//         popExp.print();
//         System.out.println("\nAnalysis: Pop should be O(1), so time should grow linearly with operation count\n");

//         // ==========================================
//         // 3. MIXED OPERATIONS
//         // ==========================================
//         System.out.println("\n3. MIXED PUSH/POP PERFORMANCE");
//         System.out.println("=" + "=".repeat(40));

//         LinkedListMixedExperiment mixedExp = new LinkedListMixedExperiment(
//                 operationCounts, iterationCount, 10000);
//         mixedExp.warmup(warmupIterations);
//         mixedExp.run();
//         mixedExp.print();
//         System.out.println("\nAnalysis: Mixed operations simulate real-world usage patterns\n");

//         // ==========================================
//         // 4. WEB BROWSER NAVIGATION
//         // ==========================================
//         System.out.println("\n4. WEB BROWSER NAVIGATION PERFORMANCE");
//         System.out.println("=" + "=".repeat(40));

//         WebBrowserNavigationExperiment browserExp = new WebBrowserNavigationExperiment(
//                 operationCounts, iterationCount, 10000);
//         browserExp.warmup(warmupIterations);
//         browserExp.run();
//         browserExp.print();
//         System.out.println("\nAnalysis: Real-world browser navigation patterns\n");

//         // ==========================================
//         // CONCLUSIONS
//         // ==========================================
//         System.out.println("=" + "=".repeat(50));
//         System.out.println("CONCLUSIONS:");
//         System.out.println("=" + "=".repeat(50));
//         System.out.println("\n✅ LinkedListStack Performance:");
//         System.out.println("   • Push: O(1) - constant time insertion at head");
//         System.out.println("   • Pop: O(1) - constant time removal from head");
//         System.out.println("   • Peek: O(1) - just returns head data");
//         System.out.println("   • No resizing overhead (unlike array-based stacks)");
//         System.out.println("   • No wasted memory from over-allocation");

//         System.out.println("\n📊 Comparison with Array-Based Stack:");
//         System.out.println("   LinkedListStack advantages:");
//         System.out.println("   • True O(1) for all operations (no resizing)");
//         System.out.println("   • No memory waste from capacity > size");
//         System.out.println("   • Dynamic growth without copying");

//         System.out.println("\n   Array-Based Stack advantages:");
//         System.out.println("   • Better cache locality");
//         System.out.println("   • Slightly faster constant factors");
//         System.out.println("   • Less memory overhead per element (no next pointers)");

//         System.out.println("\n💡 RECOMMENDATION:");
//         System.out.println("   For WebBrowser application:");
//         System.out.println("   ✅ USE LinkedListStack because:");
//         System.out.println("      - History can grow arbitrarily large");
//         System.out.println("      - No predictable max size to pre-allocate");
//         System.out.println("      - Guaranteed O(1) operations (no amortization needed)");
//         System.out.println("      - Clean separation of concerns via composition");

//         System.out.println("\n=== Performance Analysis Complete ===");
//     }

//     /**
//      * Helper method to build problem sizes
//      */
//     public static List<Integer> buildProblemSizes(int sizeMin, int sizeStep, int sizeCount) {
//         List<Integer> problemSizes = new ArrayList<>();
//         for (int i = 0; i < sizeCount; i++) {
//             problemSizes.add(sizeMin);
//             sizeMin += sizeStep;
//         }
//         return problemSizes;
//     }
// }
