public class Production {
    String left;
    String right;


    public Production(String left, String right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "Production{" +
                "left='" + left + '\'' +
                ", right='" + right + '\'' +
                '}';
    }
}
