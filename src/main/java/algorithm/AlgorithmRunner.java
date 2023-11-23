package algorithm;

public class AlgorithmRunner {

    public AlgorithmRunner(IGraphAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void run() {
        algorithm.run();
    }
    private final IGraphAlgorithm algorithm;
}
