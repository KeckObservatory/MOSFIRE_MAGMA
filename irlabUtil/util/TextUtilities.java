package edu.ucla.astro.irlab.util;

public class TextUtilities {
	public static String incrementLastLetterCaps(String currentLetter) {
		//. increments the last letter of a string (all caps).
		//. if last letter is a Z, then roll over the previous letter,
		//. adding a new A to the front if necessary
		//. A -> B, Z -> AA
		//. AA -> AB, AZ -> BA, ZZ -> AAA
		char[] charArr = currentLetter.toCharArray();
		for (int ii=charArr.length-1; ii>=0; ii--) {
			char currentChar = charArr[ii];
			if (currentChar == 'Z') {
				charArr[ii] = 'A';
				if (ii == 0) {
					return "A" + new String(charArr);
				}
			} else {
				currentChar++;
				charArr[ii] = currentChar;
				break;
			}
		}
		return new String(charArr);
	}
	public static String incrementLastLetterCaps(String currentLetter, int increment) {
		String test = new String(currentLetter);
		for (int ii=0; ii<increment;ii++) {
			test = incrementLastLetterCaps(test);
		}
		return test;
	}
	
	public static void main(String args[]) {
		System.out.println("A + 2 = <"+incrementLastLetterCaps("A", 2)+">");
		System.out.println("Y + 4 = <"+incrementLastLetterCaps("Y", 4)+">");
		System.out.println("YY + 44 = <"+incrementLastLetterCaps("YY", 44)+">");
	}
	
}

