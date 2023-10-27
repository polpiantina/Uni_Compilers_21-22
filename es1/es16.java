public class es16 {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
				case 0:
					if(Character.isDigit(ch))									state = (int)(ch)%2==1 ? 1 : 2;
					else														state = -1;
					break;
				case 1:													//zona penultimo dispari
					if(Character.isDigit(ch))									state = (int)(ch)%2==1 ? 3 : 4;
					else														state = -1;
					break;
				case 2:													//zona penultimo pari
					if(Character.isDigit(ch))									state = (int)(ch)%2==1 ? 5 : 6;
					else														state = -1;
					break;
				case 3:													//dispari da dispari
					if(Character.isLetter(ch) && (ch>='L' && ch<='Z'))			state = 7;
					else if(Character.isDigit(ch))								state = (int)(ch)%2==1 ? 3 : 4;
					else														state = -1;
					break;	
				case 4:													//pari da dispari
					if(Character.isLetter(ch) && (ch>='L' && ch<='Z'))			state = 7;
					else if(Character.isDigit(ch))								state = (int)(ch)%2==1 ? 5 : 6;
					else														state = -1;
					break;
				case 5:													//dispari da pari
					if(Character.isLetter(ch) && (ch>='A' && ch<='K'))			state = 7;
					else if(Character.isDigit(ch))								state = (int)(ch)%2==1 ? 3 : 4;
					else														state = -1;
					break;
				case 6:													//pari da pari
					if(Character.isLetter(ch) && (ch>='A' && ch<='K'))			state = 7;
					else if(Character.isDigit(ch))								state = (int)(ch)%2==1 ? 5 : 6;
					else														state = -1;
					break;
				case 7:													//finale
					if(Character.isLetter(ch) && (ch>='a' && ch<='z'))			state = 7;
					else														state = -1;
					break;
			}
		}
		return state == 7;
	}
		
	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}
