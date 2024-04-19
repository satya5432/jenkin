package demo;

public class duplicate_char {
 public static void main(String[] args) {
	String s="welcome to java";
	char[] ch = s.toCharArray();
	for(int i=0;i<ch.length;i++) {
		for(int j=i+1;j<ch.length;j++) {
			if(ch[i]==ch[j]) {
				System.out.println("duplicate char="+ch[i]);
			}
		}
	}
}
	//welcome to jenkin toolS
}

