public class NumberTok extends Token {
	public int num;
	public NumberTok(int n) { super(Tag.NUM); num=n; }
    public String toString() { return "<" + Tag.NUM + ", " + num + ">"; }
}

