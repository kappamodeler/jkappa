package com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Entry {

	private String type;
	private String weight;
	private String data;
	private List<String> listData = new ArrayList<String>();

	private int index;
	private List<String> dataList;

	
	public Entry(String _type, String _weight, String _data) {
		this.type = _type;
		this.weight = _weight;
		this.data = _data;
		parseEntry();
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

	public List<String> getListData() {
		return listData;
	}
	
	public boolean equals(Object aEntry){
		
		if(this == aEntry) return true;
		
		if(aEntry == null) return false;
		
		if(getClass() != aEntry.getClass()) return false;
		
		Entry entry = (Entry) aEntry;
		
		return equalsWithThis(entry);

	}


	private boolean equalsWithThis(Entry entry) {

		if(listData.size() != entry.getListData().size()) return false;

		if(data.equals(entry.data)) return true;
			
		List<String> listNode = saveIndex(entry.listData);
		
		replaceNodeSitesAndCreateDataList(listNode);
				
		StringBuffer tmp1 = new StringBuffer();
		StringBuffer tmp2 = new StringBuffer();
		
		for(String dataComponent : dataList) {
			tmp1.append(dataComponent);
		}
		
		for(String dataComponent : entry.listData) {
			tmp2.append(dataComponent);
		}
		
		final boolean isEqualString = tmp1.toString().equals(tmp2.toString());

		return isEqualString;

	}

	private void parseEntry() {
		
		createRawListData();
		sortList(listData);
		
	}

	private List<String> saveIndex(List<String> aListData) {
		
		List<String> listNode = new ArrayList<String>();
		
		for(String elements : aListData) {
			
			String step1This[] = elements.split("[(]");
			
			if (step1This.length == 2 ) {
		
				String step1aThis[] = step1This[1].split("[)]");
				String step2This[] = step1aThis[0].split(",");
				
				for(String element : step2This){
				
					String step3This[] = element.split("!");
				
					if (step3This.length == 2 ) {

						listNode.add(step3This[1]);

					} 					
				}
			}
		}
		
		return listNode;
		
	}
	
	private void replaceNodeSitesAndCreateDataList(List<String> listNode) {
		
		dataList = new ArrayList<String>();
		index = 0;
		
		for(String elements : listData) {
		
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
						
						String newElement = step5This[0] + "!" + getIndex(listNode);
						componentInside.add(newElement);
						
					} else {
						componentInside.add(element);
					}	
				} 					
			
				for(String componentInsideString : componentInside) {
					component.append(componentInsideString + ",");			
				}
			
				component.delete(component.length() - 1,component.length());
				component.append(")");
				dataList.add(component.toString());
				
			}

		}
	}
	
	private String getIndex(List<String> listNode) {
		
		if(index >= listNode.size()) {
			return "";
		}
		
		String result = listNode.get(index);
		index++;
			
		return result; 
	}

	private void createRawListData() {
		
		String[] step1This = this.data.split("[)],");

		for(String elementStep1 : step1This) {
			
			String[] subStep1 = elementStep1.split("[)]");
			
			String[] step2 = subStep1[0].split("[(]");
		
			String literStep2 = step2[0];
			
			String[] sites = step2[1].split(",");
			
			List<String> sitesList = new ArrayList<String>();
			
			sortSites(sites, sitesList);

			StringBuffer newElement = new StringBuffer();
			
			newElement.append(literStep2);
			newElement.append("(");
						
			for(String site : sitesList) {
				
				newElement.append(site);
				newElement.append(",");
				
			}
			
			newElement.delete(newElement.length() - 1, newElement.length());
			newElement.append(")");
			listData.add(newElement.toString());

		}
	}

	private void sortList(List<String> list) {
		
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				
				String[] subElements1 = o1.split("[(]");
				String[] subElements2 = o2.split("[(]");
				
				String liter1 = subElements1[0];
				String liter2 = subElements2[0];
				
				final int compareLiter = liter1.compareTo(liter2);
				
				if(compareLiter == 0) {
					
					int amount1 = calcNumIndex(subElements1[1]);
					int amount2 = calcNumIndex(subElements2[1]);
					
					final int compareAmountIndex = String.valueOf(amount1).compareTo(String.valueOf(amount2));
					
					if(compareAmountIndex == 0) {
					
						String sites1 = sitesStringWhithoutNumber(subElements1[1]);
						String sites2 = sitesStringWhithoutNumber(subElements2[1]);
						
						final int compareSitesWhithoutIndex = sites1.compareTo(sites2);
						
						if(compareSitesWhithoutIndex == 0) {
						
							return subElements1[1].compareTo(subElements2[1]);
						
						}
						
						return compareSitesWhithoutIndex;
					
					}
					
					return compareAmountIndex;
	
				}
				
				return compareLiter;
			}

			private int calcNumIndex(String elements) {
				
				String[] sites1 = elements.split("[)]");
				String[] sites = sites1[0].split(",");
				
				int count = 0;
				
				for (String site : sites) {
					
					String[] node = site.split("!");
					
					if(node.length == 2) {
						count++;
					}
					
				}
				
				return count;
				
			}
			
			private String sitesStringWhithoutNumber(String elements) {
				
				String[] sites1 = elements.split("[)]");
				String[] sites = sites1[0].split(",");
				
				StringBuffer newSitesString = new StringBuffer();
				
				for (String site : sites) {
					
					String[] node = site.split("!");

					newSitesString.append(node[0]);
					
					newSitesString.append(",");
				}
				
				newSitesString.delete(newSitesString.length() - 1, newSitesString.length());
				
				return newSitesString.toString();
				
			}

		
		});
	}

	private void sortSites(String[] sites, List<String> sitesList) {
		
		for (String site : sites) {
			
			sitesList.add(site);
			
		}
		
		Collections.sort(sitesList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				
				String[] siteSub1 = o1.split("!");
				String[] siteSub2 = o2.split("!");
				
				String literSite1 = siteSub1[0];
				String literSite2 = siteSub2[0];
				
				final int compareLiter = literSite1.compareTo(literSite2);
				
				if(compareLiter == 0) {
					
					String numNode1 = String.valueOf(siteSub1.length - 1);
					String numNode2 = String.valueOf(siteSub2.length - 1); 
					
					final int compareNode = numNode1.compareTo(numNode2);
					
					return compareNode;
					
					
				}
				
				return compareLiter;
			}

			
		});
	}
}
