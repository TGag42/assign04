package assign06;

import assign06.ArrayStack;
import assign06.LinkedListStack;
import assign06.Stack;
import timing.TimingExperiment;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Supplier;

/**
 * Runs timing experiments for push, pop, and peek on LinkedListStack and
 * ArrayStack, writes TSV result files, and generates HTML plots (Google Charts)
 * you can open in a browser.
 *
 * 
 */
public class StackTimingRunner {

	// ---------------- Configuration ----------------

	// Problem sizes = number of operations performed inside a single timed run.
	// Adjust as needed for your machine (keep them big enough to measure).
	private static final List<Integer> PROBLEM_SIZES = TimingExperiment.buildProblemSizes(20_000, 20_000, 10);
																											

	// Median taken over this many iterations per problem size (should be odd).
	private static final int ITERATIONS = 11;

	public static void main(String[] args) {
		// Suppliers (zero-arg constructors) for each stack under test.
		Supplier<Stack<Integer>> linkedListStackSupplier = LinkedListStack::new;
		Supplier<Stack<Integer>> arrayStackSupplier = ArrayStack::new;

		// ---------- PUSH ----------
		var pushLL = new PushExperiment("ops", PROBLEM_SIZES, ITERATIONS, linkedListStackSupplier,
				"LinkedListStack push");
		var pushArr = new PushExperiment("ops", PROBLEM_SIZES, ITERATIONS, arrayStackSupplier, "ArrayStack push");

		warmAndRun(pushLL, pushArr);

		// ---------- POP ----------
		var popLL = new PopExperiment("ops", PROBLEM_SIZES, ITERATIONS, linkedListStackSupplier, "LinkedListStack pop");
		var popArr = new PopExperiment("ops", PROBLEM_SIZES, ITERATIONS, arrayStackSupplier, "ArrayStack pop");

		warmAndRun(popLL, popArr);

		// ---------- PEEK ----------
		var peekLL = new PeekExperiment("ops", PROBLEM_SIZES, ITERATIONS, linkedListStackSupplier,
				"LinkedListStack peek");
		var peekArr = new PeekExperiment("ops", PROBLEM_SIZES, ITERATIONS, arrayStackSupplier, "ArrayStack peek");

		warmAndRun(peekLL, peekArr);

		// Console summary (per-operation medians)
		System.out.println();
		printPerOpSummary("push", pushLL, pushArr);
		printPerOpSummary("pop", popLL, popArr);
		printPerOpSummary("peek", peekLL, peekArr);
	}

	private static void warmAndRun(TimingExperiment... exps) {
		for (var e : exps)
			e.warmup(3);
		for (var e : exps)
			e.run();
	}

	private static void printPerOpSummary(String op, TimingExperiment ll, TimingExperiment arr) {
		var fmt = new DecimalFormat("0.00000E0");
		System.out.println("Per-operation medians for " + op + " (ns/op):");
		System.out.printf("%8s%15s%15s%n", "N", "LL", "ARR");
		
		var sizes = ll.getProblemSizes();
		var timesLL = ll.getMedianTimes();
		var timesArr = arr.getMedianTimes();

		for (int i = 0; i < sizes.size(); i++) {
			double nsPerOpLL = timesLL.get(i) / (double) sizes.get(i);
			double nsPerOpArr = timesArr.get(i) / (double) sizes.get(i);
			System.out.printf("%8d%15s%15s%n", sizes.get(i), fmt.format(nsPerOpLL), fmt.format(nsPerOpArr));
		}
		System.out.println();
	}

	// ---------------- Timing Experiments ----------------

	/**
	 * Base for experiments that construct a fresh stack via Supplier for each run.
	 */
	private static abstract class StackOpExperiment extends TimingExperiment {
		protected final Supplier<Stack<Integer>> supplier;
		protected final String label;

		protected Stack<Integer> stack;
		protected int N;

		public StackOpExperiment(String problemSizeName, List<Integer> problemSizes, int iterationCount,
				Supplier<Stack<Integer>> supplier, String label) {
			super(problemSizeName, problemSizes, iterationCount);
			this.supplier = supplier;
			this.label = label;
		}

		@Override
		protected void setupExperiment(int problemSize) {
			this.stack = supplier.get();
			this.N = problemSize;
			prepareState();
		}

		/** Prepare any pre-state for the operation (e.g., prefill for pop). */
		protected abstract void prepareState();
	}

	/** Measures N consecutive push operations on an empty stack. */
	private static final class PushExperiment extends StackOpExperiment {
		public PushExperiment(String psName, List<Integer> sizes, int iters, Supplier<Stack<Integer>> sup,
				String label) {
			super(psName, sizes, iters, sup, label);
		}

		@Override
		protected void prepareState() {
			/* empty stack */ }

		@Override
		protected void runComputation() {
			for (int i = 0; i < N; i++)
				stack.push(i);
		}
	}

	/**
	 * Measures N consecutive pop operations after pre-filling stack with N values.
	 */
	private static final class PopExperiment extends StackOpExperiment {
		public PopExperiment(String psName, List<Integer> sizes, int iters, Supplier<Stack<Integer>> sup,
				String label) {
			super(psName, sizes, iters, sup, label);
		}

		@Override
		protected void prepareState() {
			for (int i = 0; i < N; i++)
				stack.push(i);
		}

		@Override
		protected void runComputation() {
			for (int i = 0; i < N; i++)
				stack.pop();
		}
	}

	/** Measures N consecutive peek operations on a non-empty stack. */
	private static final class PeekExperiment extends StackOpExperiment {
		public PeekExperiment(String psName, List<Integer> sizes, int iters, Supplier<Stack<Integer>> sup,
				String label) {
			super(psName, sizes, iters, sup, label);
		}

		@Override
		protected void prepareState() {
			stack.push(42); // ensure non-empty
		}

		@Override
		protected void runComputation() {
			for (int i = 0; i < N; i++)
				stack.peek();
		}
	}
}