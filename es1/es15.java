public class es15 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
				case 0:
					if(Character.isLetter(ch) && (ch>='L' && ch<='Z'))			state = 1;
					else if(Character.isLetter(ch) && (ch>='A' && ch<='K'))		state = 2;
					else														state = -1;
					break;
				case 1:													//B
					if(Character.isLetter(ch))									state = 1;
					else if(Character.isDigit(ch))								state = (int)(ch)%2==1 ? 3 : 4;
					else														state = -1;
					break;
				case 2:													//A
					if(Character.isLetter(ch))									state = 2;
					else if(Character.isDigit(ch))								state = (int)(ch)%2==1 ? 5 : 6;
					else														state = -1;
					break;
				case 3:													//B dispari
					if(Character.isDigit(ch))									state = (int)(ch)%2==1 ? 3 : 4;
					else														state = -1;
					break;	
				case 4:													//B pari
					if(Character.isDigit(ch))									state = (int)(ch)%2==1 ? 3 : 4;
					else														state = -1;
					break;
				case 5:													//A dispari
					if(Character.isDigit(ch))									state = (int)(ch)%2==1 ? 5 : 6;
					else														state = -1;
					break;
				case 6:													//A pari
					if(Character.isDigit(ch))									state = (int)(ch)%2==1 ? 5 : 6;
					else														state = -1;
					break;
			}
		}
		return state == 3 || state == 6;
	}
		
	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}
