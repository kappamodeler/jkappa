package com.plectix.simulator.parser.regex;

import junit.framework.Assert;

import org.junit.Test;

import com.plectix.simulator.parser.util.AgentFormatChecker;

public class TestAgentFormatChecker {
	
	private String[] correct = {"a()",
			"A(x)", 
			"a12()",
			"a12(zxc)",
			"a12ggv23234vhgvvh1v212123123123vhvgvh123123123123kbnjn32jbjb23()",
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa()",
			"a(xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx)",
			"a(xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx!1)",
			"a(xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx~u)",
			"a(xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx~u!1)",
			"a(uhbhbjabsuyxgbsahb87126312h3ib1jbqdt872gdhb23d7ytd7gdnxn987yrubkd)",
			"a(uhbhbjabsuyxgbsahb87126312h3ib1jbqdt872gdhb23d7ytd7gdnxn987yrubkd!1)",
			"a(uhbhbjabsuyxgbsahb87126312h3ib1jbqdt872gdhb23d7ytd7gdnxn987yrubkd~u)",
			"a(uhbhbjabsuyxgbsahb87126312h3ib1jbqdt872gdhb23d7ytd7gdnxn987yrubkd~u!1)",
			"a(x~uhbhbjabsuyxgbsahb87126312h3ib1jbqdt872gdhb23d7ytd7gdnxn987yrubkd)",
			"a(x~uhbhbjabsuyxgbsahb87126312h3ib1jbqdt872gdhb23d7ytd7gdnxn987yrubkd!1)",
			"a(x!13453543)",
			"a(x~d!1123123)",
			"a12b15(aas!12,d~uuytuyt)",
			"ghgt______(aaasxs123!12,d~uuytuyt!2)",
			"ghgt_UFGHV_1234_(aaasxs123!12,d~uuytuyt!2)",
			"ASDFGHHJKKLLKLKUGUGJHGHVNB(qwert~j)",
			"A(qwertyqwertyqwertyuio~j)",
//			"AS(qw1~j, qw2~j, qw3~j, qw4~j, qw5~j, qw6~j, qw7~j, qw8~j, qw10~j, qw11~j, qw12~j, qw13~j, qw14~j, qw15~j)",
			"ADFGHHJKKLLKLKUGUGJHGHVNB(qw1~j!2,qw2~j!2,qw3~j!2,qw4~j!2,qw5~j!2,qw6~j!2,qw7~j!2,qw8~j!2,qw9~j!2,qw10~j!2,qw11S~j!2)",
			"ASDFGHHJKKLLKLKUGUGJHGHVNB(qwertyqwertyqwertyuio~j)",
//			"ASDFGHHJKKLLKLKUGUGJHGHVNB(qwertyqwertyqwertyuio~j, lksmxkasmlmxkmkamx)",
			"ASDFGHHJKKLLKLKUGUGJHGHVNB(qwertyqwertyqwertyuio~j!12312312312)",
			"adasdasd(asdadadasd~ASsasAS,SDSDSD!1)",
			"RTK(tyrosine~unphospho,ligand!1)",
			"RTK(tyrosine~unphospho,ligand!1"
			};

	private String[] uncorrect = {
			"",
			"a)",
//			"b(",
//			"12()",
//			"98a()",
			"A((x)", 
			"A((x))", 
//			"a12(5)",
//			"a123(66zxc)",
			"a12b15(aas!12,!d~uuytuyt)",
			"ghgt_UFGHV_1234_(aaasxs123!12;d~uuytuyt!2)",
			"ghgt__!____(aaasxs123!12,d~uuytuyt!2)"
	};
	
	
	@Test
	public void test(){
		testTime(correct, true);
		testTime(uncorrect, false);
	}
	
	
	
	public void testTime( String[] lines, boolean expectRes){
		
		Long [] stopwatch = new Long[lines.length];
		for (int i = 0; i < lines.length; i++) {
			stopwatch[i] = measureTime(i, lines[i], expectRes);
//			System.out.println((i+1) + ")\t" + lines[i].length() + "\t" + stopwatch[i]+ "\t"  + lines[i] );
		}
		
	}

	
	private long measureTime(int i, String line, boolean expected) {
		long timestamp = System.currentTimeMillis();
		boolean actual = AgentFormatChecker.check(line);
		timestamp  = System.currentTimeMillis() - timestamp;
		Assert.assertEquals(line, expected, actual);
		Assert.assertTrue(line + ": too long", timestamp < 1000);
		return timestamp;
	}

}
