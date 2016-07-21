package com.mapdb;

import java.util.concurrent.ConcurrentMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class TestMapDB {

	public static void main(String[] args) {
		DB db = DBMaker.memoryDB().make();
		
	    ConcurrentMap<String, String> map = db.hashMap("map");
	    map.put("something", "here");
	    
	    System.out.println(map.get("something"));
	}
	
}
