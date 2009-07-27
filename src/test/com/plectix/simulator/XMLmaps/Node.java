package com.plectix.simulator.XMLmaps;

import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private Data data;
	private String id;
	private Data name;
	private Data text;
	private String type;
	
	public String getData() {
		return data.dataStr;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name.dataStr;
	}
	public String getText() {
		return text.dataStr;
	}
	public String getType() {
		return type;
	}
	
	Node(String dataStr, String id, String name, String text, String type){
		this.data = new Data(dataStr.replaceAll("=>", "->"));
		this.id = id;
		this.name = new Data(name);
		this.text = new Data(text.replaceAll("=>", "->"));
		this.type = type;
		
	}

	Boolean equals(Node node){
//		if ((node.data.equals(this.data))&&
		if(	(node.id.equals(this.id))&&
			(node.name.equals(this.name))&&
			(node.text.equals(this.text))&&
			(node.type.equals(this.type)))
			return true;
		
		return false;
	}
	
	
	class Data{
		private String dataStr;
		
		Data(String dataStr){
			if (dataStr!=null){
//				dataStr = dataStr.replace(" ", "");
//				dataStr = dataStr.replace("=", "-");
//				dataStr = dataStr.replace("~", "-");
//				if (dataStr == null)	dataStr = ""; 
//				if (dataStr == "null")	dataStr = ""; 
			}
			this.dataStr = dataStr;
		}
		
		Boolean equals(Data data){
			boolean first = true;
			if (data.dataStr.equals(this.dataStr)){
				return true;
			}
			StringBuffer d1, d2;
			List<Buffer> list = new ArrayList<Buffer>();
			d2 = new StringBuffer(data.dataStr);
			d1 = new StringBuffer(this.dataStr);
//			if (d1.length() == d2.length()){
//				
//				for (int i = 0; i < d1.length(); i++) {
//					if (d1.charAt(i)!=d2.charAt(i))
//						return false;
//					if (d1.charAt(i) == '!'){
//						i++;
//						if (first){
//							list.add(new Buffer(d1.charAt(i), d2.charAt(i), i));
//							first = false;
//						} else {
//							Buffer buf = found(d1.charAt(i), list);
//							if (buf!=null){
//								if (buf.c2 == d2.charAt(i))
//									buf.append(i);
//								else return false;
//							} else {
//								list.add(new Buffer(d1.charAt(i), d2.charAt(i), i));
//							}
//						}
//					}
//				}
//				return true;
//			}
			
			return false;
		}
		

		private Buffer found(char c, List<Buffer> list) {
			for (Buffer buffer : list) {
				if (buffer.c1 == c){
					return buffer;
				}
			}
			return null;
		}


		class Buffer{
			private char c1, c2;
			private int index1, index2;
			Buffer(char c1, char c2, int index1){
				this.c1 = c1;
				this.c2 = c2;
				this.index1 = index1;
				this.index2 = -1;
			}
			char getc1(){
				return c1;
			}
			void append(int i){
				this.index2 = i;
			}
			
			
		}
	}	
	
	
}
