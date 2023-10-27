import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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
				break;
			default:
				error("prog");
		}
        int lnext_prog = code.newLabel();	// L0
        statlist(lnext_prog);				// 0
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
        	code.toJasmin();
        } catch(java.io.IOException e) {
        	System.out.println("IO error\n");
        };
    }

	private void statlist(int lnext_statlist) {
		switch (look.tag) {			// parto con stat
			case Tag.ASSIGN:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.WHILE:
			case Tag.IF:
			case '{':
				break;
			default:
				error("statlist");
		}
		int lnext_prev = lnext_statlist;
		lnext_statlist = code.newLabel();	// L1
		stat(lnext_statlist);				// 1
		code.emitLabel(lnext_statlist);
		statlistp(lnext_statlist);
		code.emit(OpCode.GOto, lnext_prev);
	}

	private void statlistp(int lnext_statlistp) {
		switch (look.tag) {
			case ';':
				match(';');
				lnext_statlistp = code.newLabel();	// L2
				stat(lnext_statlistp);				// 2
				code.emitLabel(lnext_statlistp);
				statlistp(lnext_statlistp);
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

    public void stat(int lnext_stat) {    
    	int ltrue, lfalse;
		switch (look.tag) {
			case Tag.ASSIGN:
				match(Tag.ASSIGN);
				expr();
				match(Tag.TO);
				idlist(Tag.ASSIGN);					// passo la label che stamperò alla fine di stat e segno che arrivo da ASSIGN
				code.emit(OpCode.GOto, lnext_stat);
				break;
			case Tag.PRINT:
				match(Tag.PRINT);
				match('(');
				exprlist('p');						// la print la faccio in exprlistp
				match(')');
				code.emit(OpCode.GOto, lnext_stat);
				break;
			case Tag.READ:
				match(Tag.READ);
				match('(');
				idlist(Tag.READ);					// passo la label che stamperò alla fine di stat e segno che arrivo da READ
				match(')');
				code.emit(OpCode.GOto, lnext_stat);
				break;
			case Tag.WHILE:
				lfalse = lnext_stat;
				lnext_stat = code.newLabel();
				ltrue = code.newLabel();
				code.emitLabel(lnext_stat);
				match(Tag.WHILE);
				match('(');
				bexpr(/*ltrue,*/ lfalse);
				match(')');
				code.emitLabel(ltrue);				// genero comunque label true per migliore leggibilità
				stat(lnext_stat);
				break;
			case Tag.IF:
				ltrue = code.newLabel();
				lfalse = code.newLabel();
				match(Tag.IF);
				match('(');
				bexpr(/*ltrue,*/ lfalse);
				match(')');
				code.emitLabel(ltrue);				// genero comunque label true per migliore leggibilità
				stat(lnext_stat);
				code.emitLabel(lfalse);
				statp(lnext_stat);
				break;
			case '{':
				match('{');
				statlist(lnext_stat);
				match('}');
				break;
			default:
				error("stat");
		}
     }

	private void statp(int lnext_statp) {
		switch (look.tag) {
			case Tag.END:
				match(Tag.END);
				break;
			case Tag.ELSE:
				match(Tag.ELSE);
				stat(lnext_statp);
				match(Tag.END);
				break;
			default:
				error("statp");
		}
	}

    private void idlist(int t) {						// read(a,b,c) -> invoke istore a, invoke istore b, invoke istore c // ASSIGN 10 TO (a,b,c)
		switch (look.tag) {
			case Tag.ID:
		    	int id_addr = st.lookupAddress(((Word)look).lexeme);
		        if (id_addr==-1) {
		            id_addr = count;
		            st.insert(((Word)look).lexeme,count++);
		        }
				match(Tag.ID);
				if(t == Tag.READ) 						// le read vanno fatte in ordine, quindi accoppio read + store
					code.emit(OpCode.invokestatic,0);	// 1->print, 0->read
				code.emit(OpCode.istore, id_addr);
				idlistp(id_addr,t);
				break;
			default:
				error("idlist");
		}
    }

	private void idlistp(int first_id, int t) {
		switch (look.tag) {
			case ',':
				match(',');
		    	int id_addr = st.lookupAddress(((Word)look).lexeme);
		        if (id_addr==-1) {
		            id_addr = count;
		            st.insert(((Word)look).lexeme,count++);
		        }
				match(Tag.ID);
				switch(t) {
					case Tag.READ:
						code.emit(OpCode.invokestatic,0);	// 1 -> print, 0 -> read
						break;
					case Tag.ASSIGN:
						code.emit(OpCode.iload, first_id);
						break;
					default:
						error("this isn't supposed to be possible -> error in idlistp, wrong Tag in inner switch");
				}
				code.emit(OpCode.istore, id_addr);
				idlistp(first_id, t);
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

	private void bexpr(/*int ltrue_bexpr,*/ int lfalse_bexpr) {
		switch (look.tag) {
			case Tag.RELOP:
				String s = ((Word)look).lexeme;
				match(Tag.RELOP);
				expr();
				expr();
				switch (s) {
					case "<":
						code.emit(OpCode.if_icmpge, lfalse_bexpr);	// genero codice per la condizione invertita e salto a false
						break;
					case ">":
						code.emit(OpCode.if_icmple, lfalse_bexpr);
						break;
					case "==":
						code.emit(OpCode.if_icmpne, lfalse_bexpr);
						break;
					case "<=":
						code.emit(OpCode.if_icmpgt, lfalse_bexpr);
						break;
					case "<>":
						code.emit(OpCode.if_icmpeq, lfalse_bexpr);
						break;
					case ">=":
						code.emit(OpCode.if_icmplt, lfalse_bexpr);
						break;
				}
				//code.emit(OpCode.GOto, lfalse_bexpr);
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
				exprlist('+');					// vado in exprlist con mode = '+' -> arrivo da somma
				match(')');
				break;
			case '*':
				match('*');
				match('(');
				exprlist('*');					// vado in exprlist con mode = '*' -> arrivo da prodotto
				match(')');
				break;
			case '-':
				match('-');
				expr();
				expr();
                code.emit(OpCode.isub);
				break;
			case '/':
				match('/');
				expr();
				expr();
                code.emit(OpCode.idiv);
				break;
			case Tag.NUM:
				code.emit(OpCode.ldc, ((NumberTok)look).num);
				match(Tag.NUM);
				break;
			case Tag.ID:
				int id_addr = st.lookupAddress(((Word)look).lexeme);
		        if (id_addr==-1) {
		            error("missing identificator during expr");
		        }
				match(Tag.ID);
				code.emit(OpCode.iload, id_addr);
				break;
			default:
				error("expr");
		}
    }

	private void exprlist(int mode) {
		switch (look.tag) {
			case '+':
			case '*':
			case '-':
			case '/':
			case Tag.NUM:
			case Tag.ID:
				break;
			default:
				error("exprlist");
		}
		expr();
		exprlistp(mode);
	}

	private void exprlistp(int mode) {
		if(mode == 'p')							// se arrivo da Tag.print aggiungi una print pre ricorsione, altrimenti stampa al contrario
			code.emit(OpCode.invokestatic, 1);
		switch (look.tag) {
			case ',':
				match(',');
				expr();
				exprlistp(mode);
				switch(mode){
					case '+':						// se arrivo da una serie di somme e ho più di un valore aggiungo un iadd
						code.emit(OpCode.iadd);
						break;
					case '*':						// se arrivo da una serie di prodotti e ho più di un valore aggiungo un imul
						code.emit(OpCode.imul);
						break;
				}
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
            Translator translator = new Translator(lex, br);
            translator.prog();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
