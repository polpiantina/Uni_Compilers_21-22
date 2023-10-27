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

	public void prog() {
		switch (look.tag) {			// non partire con schifezze / parto con stat
			case Tag.ASSIGN:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.WHILE:
			case Tag.IF:
			case '{':
				statlist();
				match(Tag.EOF);
				break;
			default:
				error("prog");
		}
	}

	private void statlist() {
		switch (look.tag) {			// parto con stat
			case Tag.ASSIGN:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.WHILE:
			case Tag.IF:
			case '{':
				stat();
				statlistp();
				break;
			default:
				error("statlist");
		}
	}

	private void statlistp() {
		switch (look.tag) {
			case ';':
				match(';');
				stat();
				statlistp();
				break;
			case Tag.END:	// epsilon
			case Tag.ELSE:
			case Tag.EOF:
			case '}':
				break;
			default:
				error("statlistp");
		}
	}

	private void stat() {
		switch (look.tag) {
			case Tag.ASSIGN:
				match(Tag.ASSIGN);
				expr();
				match(Tag.TO);
				idlist();
				break;
			case Tag.PRINT:
				match(Tag.PRINT);
				match('(');
				exprlist();
				match(')');
				break;
			case Tag.READ:
				match(Tag.READ);
				match('(');
				idlist();
				match(')');
				break;
			case Tag.WHILE:
				match(Tag.WHILE);
				match('(');
				bexpr();
				match(')');
				stat();
				break;
			case Tag.IF:
				match(Tag.IF);
				match('(');
				bexpr();
				match(')');
				stat();
				statp();
				break;
			case '{':
				match('{');
				statlist();
				match('}');
				break;
			default:
				error("stat");
		}
	}

	private void statp() {
		switch (look.tag) {
			case Tag.END:
				match(Tag.END);
				break;
			case Tag.ELSE:
				match(Tag.ELSE);
				stat();
				match(Tag.END);
				break;
			default:
				error("statp");
		}
	}

	private void idlist() {
		switch (look.tag) {
			case Tag.ID:
				match(Tag.ID);
				idlistp();
				break;
			default:
				error("idlist");
		}
	}

	private void idlistp() {
		switch (look.tag) {
			case ',':
				match(',');
				match(Tag.ID);
				idlistp();
				break;
			case Tag.END:	// epsilon
			case Tag.ELSE:
			case Tag.EOF:
			case '}':
			case ')':
			case ';':
				break;
			default:
				error("idlistp");
		}
	}

	private void bexpr() {
		switch (look.tag) {
			case Tag.RELOP:
				match(Tag.RELOP);
				expr();
				expr();
				break;
			default:
				error("bexpr");
		}
	}

	private void expr() {
		switch (look.tag) {
			case '+':
				match('+');
				match('(');
				exprlist();
				match(')');
				break;
			case '*':
				match('*');
				match('(');
				exprlist();
				match(')');
				break;
			case '-':
				match('-');
				expr();
				expr();
				break;
			case '/':
				match('/');
				expr();
				expr();
				break;
			case Tag.NUM:
				match(Tag.NUM);
				break;
			case Tag.ID:
				match(Tag.ID);
				break;
			default:
				error("expr");
		}
	}

	private void exprlist() {
		switch (look.tag) {
			case '+':
			case '*':
			case '-':
			case '/':
			case Tag.NUM:
			case Tag.ID:
				expr();
				exprlistp();
				break;
			default:
				error("exprlist");
		}
	}

	private void exprlistp() {
		switch (look.tag) {
			case ',':
				match(',');
				expr();
				exprlistp();
				break;
			case ')':	// epsilon
				break;
			default:
				error("exprlistp");
		}
	}

	public static void main(String[] args) {
		Lexer lex = new Lexer();
		String path = "test23_32.txt"; // il percorso del file da leggere
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			Parser parser = new Parser(lex, br);
			parser.prog();
			System.out.println("Input OK");
			br.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
