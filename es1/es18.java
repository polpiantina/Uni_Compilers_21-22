public class es18 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
				case 0:
					if(ch == '+' || ch == '-')		state = 1;
					else if(Character.isDigit(ch))	state = 2;
					else if(ch == '.')				state = 3;
					else							state = -1;
					break;
				case 1:
					if(Character.isDigit(ch))		state = 2;
					else if(ch == '.')				state = 3;
					else							state = -1;
					break;
				case 2:													//numeri
					if(Character.isDigit(ch))		state = 2;
					else if(ch == '.')				state = 3;
					else if(ch == 'e')				state = 5;
					else							state = -1;
					break;
				case 3:
					if(Character.isDigit(ch))		state = 4;
					else							state = -1;
					break;	
				case 4:													//numeri con virgola
					if(Character.isDigit(ch))		state = 4;
					else if(ch == 'e')				state = 5;
					else							state = -1;
					break;
				case 5:
					if(ch == '+' || ch == '-')		state = 6;
					else if(Character.isDigit(ch))	state = 7;
					else if(ch == '.')				state = 8;
					else							state = -1;
					break;
				case 6:
					if(Character.isDigit(ch))		state = 7;
					else if(ch == '.')				state = 8;
					else							state = -1;
					break;
				case 7:													//numeri con esponente
					if(Character.isDigit(ch))		state = 7;
					else if(ch == '.')				state = 8;
					else							state = -1;
					break;
				case 8:
					if(Character.isDigit(ch))		state = 9;
					else							state = -1;
					break;	
				case 9:													//numeri con esponente dopo la virgola
					if(Character.isDigit(ch))		state = 9;
					else							state = -1;
					break;
			}
		}
		return state == 2 || state == 4 || state == 7 || state == 9;
	}
		
	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}
