package demo;

public class reverseEachWord {
public static void main(String[] args) {
	String s="Rupesh is simple man";
	String[] word = s.split(" ");
	String output="";
	for(String wd:word)	{
		if(wd.equals("simple")) {
		String rev="";
		for(int i=wd.length()-1;i>=0;i--) {
			rev=rev+wd.charAt(i);
		}
		output=output+rev+" ";
	}
	}

	System.out.println(s.replaceFirst("simple", output));
}
}
