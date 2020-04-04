package work.wengyuxian.thompson;

public class Trans {
    public int stateFrom, stateTo;
    public char transSymbol;

    public Trans(int v1, int v2, char sym) {
        this.stateFrom = v1;
        this.stateTo = v2;
        this.transSymbol = sym;
    }
}