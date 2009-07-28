package com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Entry {

	private String type;
	private String weight;
	private String data;
	private int index;
	private Map<String, Integer> mapIndex = new HashMap<String, Integer>();
	
	private List<String> listData = new ArrayList<String>();
	
	private List<String> dataList;
	
	public Entry(String _type, String _weight, String _data) {
		this.type = _type;
		this.weight = _weight;
		this.data = _data;
		parseEntry(false);
	}
	
	public String getType() {
		return type;
	}

	public String getWeight() {
		return weight;
	}
	
	public String getData(){
		return data;
	}

	public List<String> getDataList() {
		return dataList;
	}
	
	public boolean equals(Object aEntry){
		
		if(this == aEntry) return true;
		
		if(aEntry == null) return false;
		
		if(getClass() != aEntry.getClass()) return false;
		
		Entry entry = (Entry) aEntry;
		
		return equalsWithThis(entry);

	}


	private boolean equalsWithThis(Entry entry) {

		if(dataList.size() != entry.getDataList().size()) return false;
		
		if (data.equals("EGFR(dimer!0,ligand,tail~u),EGFR(dimer!0,ligand,tail~u)")) {
			boolean is = true;
			is = is && is;
			System.out.println(is);
		}
		
		boolean result = true;

//		for (int i = 0; i < dataList.size(); i++) {
//			
//			result = result && 
//			
//		}
		StringBuffer tmp1 = new StringBuffer();
		StringBuffer tmp2 = new StringBuffer();
		
		for(String dataComponent : dataList) {
			tmp1.append(dataComponent);
		}
		
		for(String dataComponent : entry.getDataList()) {
			tmp2.append(dataComponent);
		}
		
		return tmp1.toString().equals(tmp2.toString());
		
//		Iterator<String> itL = dataList.listIterator();
//		
//		while (itL.hasNext()) {
//			String type = itL.next();
//			if(!entry.getDataList().contains(type)) return false;
////			itL.remove();
//			entry.getDataList().remove(type);
//			
//		}
		
		
//		for(String dataComponent : dataList) {
//			if(!entry.getDataList().contains(dataComponent)) return false;
//		}
//		return true;
	}

	public void parseEntry(boolean isIncrement) {

		dataList = new ArrayList<String>();
		
		String step1This[] = this.data.split("[)],");

		for(String elementStep1 : step1This) {
	
			
			listData.add(elementStep1);

		}
		
		String[] dataArray = new String[listData.size()];
		
		Arrays.sort(listData.toArray(dataArray) , new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				
				String o1Split[] = o1.split("[(]");
				String o2Split[] = o2.split("[(]");
				
				if ((o1Split.length == 2) && (o2Split.length == 2)) {

					return (o1Split[0] + o1Split[1].length()).compareTo((o2Split[0] + o1Split[0].length()));
					
				}
				return 0;
			}
		});
		
		for(String elements : dataArray) {
		
			String step3This[] = elements.split("[(]");
			
			if (step3This.length == 2 ) {
			
				StringBuffer component = new StringBuffer();
			
				component.append(step3This[0] + "(");
		
				String step3aThis[] = step3This[1].split("[)]");
				String step4This[] = step3aThis[0].split(",");
				
				List<String> componentInside = new ArrayList<String>();
				
				for(String element : step4This){
				
					String step5This[] = element.split("!");
				
					if (step5This.length == 2 ) {
						
						String newElement = step5This[0] + "!" + newIndex(step5This[1]);
						componentInside.add(newElement);
						
					} else {
						componentInside.add(element);
					}	
				} 					
			
				Arrays.sort(componentInside.toArray());
								
				for(String componentInsideString : componentInside) {
					component.append(componentInsideString + ",");			
				}
			
				component.delete(component.length() - 1,component.length());
				component.append(")");
				dataList.add(component.toString());
				
			}

		}
	}
	
	private String incrementIntbyString(String value) {
		
		Integer stringToInteger = Integer.valueOf(value);
		
		int incrementInteget = stringToInteger.intValue() + 1;
		
		return String.valueOf(incrementInteget);
		
		
	}
	
	private String newIndex(String value) {
		
		if(!mapIndex.containsKey(value)) {
			
			index++;
			
			mapIndex.put(value, Integer.valueOf(index));
			
			return Integer.valueOf(index).toString();
			
		} 
		
		
		return mapIndex.get(value).toString();
		
		
	}
	
}
