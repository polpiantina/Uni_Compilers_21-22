import java.io.*; 
import java.util.*;

public class Lexer {		// LEXER DI ES 2.3 -> con riconoscimento identificatori con underscore + riconoscimento commenti semplici e complessi

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r' || peek == '/') {
            if (peek == '\n') line++;
            
            if(peek == '/'){
            	readch(br);
		        if(peek == '/'){
		    		while(peek != '\n' && peek != (char)-1)	readch(br);	// continuo finché non trovo fine riga, aumento riga e poi leggerò nuovo char
		    		line++;
		    	} else	if(peek == '*') {
		    		peek = ' ';						// resetto peek in modo da entrare nel doppio ciclo while che si assicura di trovare doppia terminazione
		    		while(peek != '/'){
		    			while(peek != '*' && peek != (char)-1){			// appena trovo * esco dal ciclo while interno e leggo carattere. Se continuo a trovare * continuo a restare nel ciclo esterno
		    				readch(br);
						}
						if(peek == (char)-1){
				            System.err.println("End of comment not found");
				            return null;
						}	
						readch(br);					// appena trovo / dopo un * esco
					}								// ho trovato */ di fila e quindi finisco (e leggerò un nuovo carattere)
		    	} else {
		    		return Token.div;				// ritorno il token e mantengo il nuovo carattere salvo
		    	}
	    	} 
	    	
	    	readch(br);
            
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
                
            case '(':
                peek = ' ';
                return Token.lpt;
                
            case ')':
                peek = ' ';
                return Token.rpt;
                
            case '{':
                peek = ' ';
                return Token.lpg;
                
            case '}':
                peek = ' ';
                return Token.rpg;
                
            case '+':
                peek = ' ';
                return Token.plus;
                
            case '-':
                peek = ' ';
                return Token.minus;
                
            case '*':
                peek = ' ';
                return Token.mult;
                
            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;
	
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
	
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
	
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
				} else if (peek == '>') {
					peek = ' ';
					return Word.ne;
                } else {
                    return Word.lt;
                }
	
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }
	
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after = : "  + peek );
                    return null;
                }
          
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek) || peek == '_') {							// caso lettere
            		String s = "";
            		boolean found = false;
					do {
						s+=peek;
						if((Character.isLetter(peek) || Character.isDigit(peek)) && !found)	found = true;
						readch(br);
					} while(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_');
					
					switch (s) {
						case "assign":
							return Word.assign;
						case "to":
							return Word.to;
						case "if":
							return Word.iftok;
						case "else":
							return Word.elsetok;
						case "while":
							return Word.whiletok;
						case "begin":
							return Word.begin;
						case "end":
							return Word.end;
						case "print":
							return Word.print;
						case "read":
							return Word.read;
						default:
							if(found)	return new Word(Tag.ID,s);
							else{		
								System.err.println("Missing letter or number in identifier");
								return null;
							}
					}

                } else if (Character.isDigit(peek)) {					// caso numeri
					int n = 0;
					if(peek == '0') {
						readch(br);
						if(Character.isDigit(peek))	{
				            System.err.println("Erroneous character"
				                    + " after 0 : "  + peek );
				            return null;
						} else return new NumberTok(0);
					
					} else {
						do {
							n = n*10 + (int)peek - '0';
							readch(br);
						} while(Character.isDigit(peek));
						
						return new NumberTok(n);
					}
                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test23.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}

