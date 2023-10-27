public class es13 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
				case 0:
					if(Character.isDigit(ch))			state = (int)(ch)%2==1 ? 1 : 2;
					else								state = -1;
					break;
				case 1:
					if(Character.isDigit(ch))			state = (int)(ch)%2==1 ? 1 : 2;
					else if(Character.isLetter(ch))		state = ch>='L' && ch<='Z' ? 3 : -1;
					else								state = -1;
					break;
				case 2:
					if(Character.isDigit(ch))			state = (int)(ch)%2==1 ? 1 : 2;
					else if(Character.isLetter(ch))		state = ch>='A' && ch<='K' ? 3 : -1;
					else								state = -1;
					break;
				case 3:
					if(Character.isLetter(ch))			state = 3;
					else								state = -1;
					break;				
			}
		}
		return state == 3;
	}
		
	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}
