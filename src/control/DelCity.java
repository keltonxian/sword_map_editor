package control;

import java.util.ArrayList;

import app.City;
import app.WorldMap;


public class DelCity extends Control {
	WorldMap map;
	ArrayList deleteList ;
	public DelCity(WorldMap _map){
		map =_map;
		deleteList= new ArrayList(map.selectedCities);
	}

	public void execute() {
		City c;
		for(int i=0;i<deleteList.size();i++){
			c =(City)deleteList.get(i);
			for(int j=0;j<c.linkCities.length;j++){
				if(c.linkCities[j]!=null)
					c.linkCities[j].linkCities[c.linkDoor[j]]=null;
			}
			map.removeCity(c);
		}
		map.selectCity((City)null);
	}

	public void undo() {
		City c;
		for(int i=0;i<deleteList.size();i++){
			c =(City)deleteList.get(i);
			for(int j=0;j<c.linkCities.length;j++){
				if(c.linkCities[j]!=null)
					c.linkCities[j].linkCities[c.linkDoor[j]]=c;
			}
			map.addCity(c);
		}
		map.selectedCities.clear();
		map.selectedCities.addAll(deleteList);
	}

}
