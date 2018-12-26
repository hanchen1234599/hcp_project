package com.hc.test;

import java.util.ArrayList;

import com.hc.share.util.Trace;

public class Main8 {
	public static void main(String[] args) {
		ArrayList<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		Trace.info(list.get(1));
	}
}
