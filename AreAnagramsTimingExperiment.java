package assign04;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

public class AreAnagramsTimingExperiment extends TimingExperiment {

	private final Random rng = new Random(42);
	private String s1, s2;

	public static void main(String[] args) {
		String problemSizeName = "Number of Elements";
		List<Integer> problemSizes = buildProblemSizes(1_000, 1_000, 20);
		int iterationCount = 10;

		TimingExperiment timing = new AreAnagramsTimingExperiment(problemSizeName, problemSizes, iterationCount);
		timing.warmup(5);
		timing.run();
		timing.print();
	}

	public AreAnagramsTimingExperiment(String problemSizeName, List<Integer> problemSizes, int iterationCount) {
		super(problemSizeName, problemSizes, iterationCount);

	}

	@Override
	protected void setupExperiment(int problemSize) {
		this.s1 = randomString(problemSize);
		if (rng.nextBoolean()) {
			this.s2 = randomPermutation(this.s1);
		} else {
			this.s2 = randomString(problemSize);
		}
	}

	@Override
	protected void runComputation() {
		AnagramChecker.areAnagrams(s1, s2);
	}
	
	private String randomString(int n) {
        char[] a = new char[n];
        for (int i = 0; i < n; i++) a[i] = (char)('a' + rng.nextInt(26));
        return new String(a);
    }

    private String randomPermutation(String s) {
        char[] a = s.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            char tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        return new String(a);
    }
}
