package com.kantyokudroid.softkeyboard;

import java.util.ArrayList;

public class KeyTable {
	public String charachter;
	public String keyTop;
	public KeyTable[] table;
	public boolean lastFlag;
	
	KeyTable(){	//create key which doesn't have defined strokes
//		this.table = new KeyTable[31];
		this.lastFlag = true;
	}
	KeyTable(String str){
		this(str, str);
	}
	KeyTable(String str, String keytop){	//single key with valid character(s) output
		this.keyTop = keytop;
		this.charachter = str;
		this.lastFlag = true;
	}
	KeyTable(KeyTable[] keys, String keytop){	// has next stroke(s), and not terminated
		this.keyTop = keytop;
		this.table = new KeyTable[31];
		for(int i = 0; i < keys.length || i < 31; i++){
			this.table[i] = keys[i];
		}
		this.lastFlag = false;
	}
	KeyTable(ArrayList<KeyTable> keys, String keytop){
		this.keyTop = keytop;
		this.table = new KeyTable[31];
		for(int i = 0; i < keys.size() || i < 31; i++){
			this.table[i] = keys.get(i);
		}
		this.lastFlag = false;
	}
	
//	public String toString(){
//		String retstr = "";
//		if (table != null){
//			retstr += "[" + table[0].toString();
//			for(int i=1; i<table.length; i++){
//				retstr += ", "+ table[i];
//			}
//			retstr += "]";
//		}else if(charachter != null){
//			retstr = "\""+charachter+"\"";
//		}
//		return retstr;
//	}
	
//	public boolean isLastStroke(){
//		return this.lastFlag;
//	}
//	public KeyTable getKeyTable(int keyNum){
//		if (this.table[keyNum] != null){
//			return this.table[keyNum];
//		}else{
//			return null;
//		}
//	}
//	public String getChar(){
//		if (this.isLastStroke()){
//			return this.charachter;
//		}else{
//			return null;
//		}
//	}
//	public String getKeyTop(){
//		return this.keyTop;
//	}
//	public void setTable(KeyTable key, int index){
//		this.table[index] = key;
//	}
//	public void setChar(String str){
//		this.charachter = str;
//		this.keyTop = str;
//		this.lastFlag = true;
//	}
//	public void setChar(String str, String keytop){
//		this.charachter = str;
//		this.keyTop = keytop;
//		this.lastFlag = true;
//	}
//	public void setKeyTop(String keytop){
//		this.keyTop = keytop;
//	}
}
