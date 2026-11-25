package f0;

public class DarkPiece {
    public ColorType color;
    public String name;
    public int rank; // 大小等級
    public boolean isFaceUp = false;

    public DarkPiece(ColorType color, String name, int rank) {
        this.color = color;
        this.name = name;
        this.rank = rank;
    }
}