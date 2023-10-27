import java.io.*;

public class Parser {
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;
	
	public Parser(Lexer l, BufferedReader br) {
		lex = l;
		pbr = br;
		move();
	}

	void move() {
		look = lex.lexical_scan(pbr);
		System.out.println("token = " + look);
	}

	void error(String s) {
		throw new Error("near line " + lex.line + ": " + s);
	}

	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF) move();
		} else error("syntax error");
	}

	public void start() {
		switch (look.tag){			// non partire con schifezze
			case Tag.NUM:
			case '(':
				break;
			default:
				error("start");
		}
		expr();
		match(Tag.EOF);
	}

	private void expr() {
		switch (look.tag) {			// controllo term ( -> fact) in anticipo
			case Tag.NUM:
			case '(':
				break;
			default:
				error("expr");
		}
		term();
		exprp();
	}

	private void exprp() {
		switch (look.tag) {
			case '+':
				match('+');
				term();
				exprp();
				break;
			case '-':
				match('-');
				term();
				exprp();
				break;
			case ')':			// casi epsilon
			case Tag.EOF:
				break;
			default:
				error("exprp");
		}
	}

	private void term() {
	switch (look.tag) {			// controllo fact in anticipo
			case Tag.NUM:
			case '(':
				break;
			default:
				error("term");
	}
		fact();
		termp();
	}

	private void termp() {
		switch (look.tag) {
			case '*':
				match('*');
				fact();
				termp();
				break;
			case '/':
				match('/');
				fact();
				termp();
				break;
			case '+':			// casi epsilon
			case '-':
			case ')':
			case Tag.EOF:
				break;
			default:
				error("termp");
		}
	}

	private void fact() {
		switch (look.tag) {
			case '(':
				match('(');
				expr();
				match(')');
				break;
			case Tag.NUM:
				match(Tag.NUM);
				break;
			default:
				error("fact");
		}
	}

	public static void main(String[] args) {
		Lexer lex = new Lexer();
		String path = "test23_31.txt"; // il percorso del file da leggere
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			Parser parser = new Parser(lex, br);
			parser.start();
			System.out.println("Input OK");
			br.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
