package es.uji.belfern.statistics;

public class Estimate {
    public final String label;
    public final double probability;

    public Estimate() {
        super();
        label = "None";
        probability = 0;
    }

    public Estimate(String label, double probability) {
        this.label = label;
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "Estimate{" +
                "label='" + label + '\'' +
                ", probability=" + probability +
                '}';
    }
}