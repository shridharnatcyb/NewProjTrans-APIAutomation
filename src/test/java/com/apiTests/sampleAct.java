
package com.apiTests;

import org.testng.annotations.Test;

public class sampleAct {

	@Test public void mainval(){
		String[] str = new String[]{"asdsad"};
		//String[] str = new String[2];
		//str[1]="asdsaff";
		
		System.out.println(str.length);
		System.out.println(str[0]);
		System.out.println(str.toString());

	}

}
