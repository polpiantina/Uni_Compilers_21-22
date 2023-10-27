import java.io.*; 
import java.util.*;

public class Lexer {		// LEXER DI ES 2.1 -> base

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
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
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
                
            case '/':
                peek = ' ';
                return Token.div;
                
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
                if (Character.isLetter(peek)) {							// caso lettere
            		String s = "";
					do {
						s+=peek;
						readch(br);
					} while(Character.isLetter(peek) || Character.isDigit(peek));
					
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
							return new Word(Tag.ID,s);
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
        String path = "test21.txt"; // il percorso del file da leggere
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

