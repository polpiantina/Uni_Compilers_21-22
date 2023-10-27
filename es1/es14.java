public class es14 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
				case 0:
					if(Character.isDigit(ch))			state = (int)(ch)%2==1 ? 1 : 2;		//se è dispari vado in 1, altrimenti in 2
					else								state = -1;
					break;
				case 1:
					if(Character.isDigit(ch))			state = (int)(ch)%2==1 ? 1 : 2;
					else if(ch == ' ')					state = 3;
					else if(Character.isLetter(ch))		state = ch>='L' && ch<='Z' ? 5 : -1;
					else								state = -1;
					break;
				case 2:
					if(Character.isDigit(ch))			state = (int)(ch)%2==1 ? 1 : 2;
					else if(ch == ' ')					state = 4;
					else if(Character.isLetter(ch))		state = ch>='A' && ch<='K' ? 5 : -1;
					else								state = -1;
					break;
				case 3:
					if(ch == ' ')						state = 3;
					else if(Character.isLetter(ch))		state = ch>='L' && ch<='Z' ? 5 : -1;
					else								state = -1;
					break;
				case 4:
					if(ch == ' ')						state = 4;
					else if(Character.isLetter(ch))		state = ch>='A' && ch<='K' ? 5 : -1;
					else								state = -1;
					break;
				case 5:
					if(ch == ' ')						state = 6;
					else if(Character.isLetter(ch))		state = 5;
					else								state = -1;
					break;
				case 6:
					if(ch == ' ')						state = 6;
					else if(Character.isLetter(ch))		state = Character.isUpperCase(ch) ? 5 : -1;
					else								state = -1;
					break;
			}
		}
		return state == 5 || state == 6;
	}
		
	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}
