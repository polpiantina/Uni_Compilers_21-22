import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
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
		int expr_val;
		
		switch (look.tag){			// non partire con schifezze
			case Tag.NUM:
			case '(':
				break;
			default:
				error("start");
		}

    	expr_val = expr();
		match(Tag.EOF);

        System.out.println(expr_val);
    }

    private int expr() { 
		int term_val, exprp_val;

		switch (look.tag) {			// controllo term ( -> fact) in anticipo
			case Tag.NUM:
			case '(':
				break;
			default:
				error("expr");
		}

    	term_val = term();
		exprp_val = exprp(term_val);

		return exprp_val;
    }

    private int exprp(int exprp_i) {
		int term_val, exprp_val=0;
		switch (look.tag) {
			case '+':
		        match('+');
		        term_val = term();
		        exprp_val = exprp(exprp_i + term_val);
		        break;
			case '-':
				match('-');
				term_val = term();
				exprp_val = exprp(exprp_i - term_val);
				break;
			case ')':			// casi epsilon
			case Tag.EOF:
				exprp_val = exprp_i;
				break;
			default:
				error("exprp");
		}
		return exprp_val;
    }

    private int term() { 
		int fact_val, termp_val;
		
		switch (look.tag) {			// controllo fact in anticipo
			case Tag.NUM:
			case '(':
				break;
			default:
				error("term");
		}
		fact_val = fact();
		termp_val = termp(fact_val);
		return termp_val;
    }
    
    private int termp(int termp_i) { 
    	int fact_val, termp_val=0;
		switch (look.tag) {
			case '*':
				match('*');
				fact_val = fact();
				termp_val = termp(termp_i * fact_val);
				break;
			case '/':
				match('/');
				fact_val = fact();
				termp_val = termp(termp_i / fact_val);
				break;
			case '+':			// casi epsilon
			case '-':
			case ')':
			case Tag.EOF:
				termp_val = termp_i;
				break;
			default:
				error("termp");
		}
		return termp_val;
    }
    
    private int fact() { 
    	int expr_val=0;
		switch (look.tag) {
			case '(':
				match('(');
				expr_val = expr();
				match(')');
				break;
			case Tag.NUM:
				expr_val = ((NumberTok)look).num;
				match(Tag.NUM);
				break;
			default:
				error("fact");
		}
		return expr_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test23_31.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}

