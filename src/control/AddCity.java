package control;

import app.City;
import app.WorldMap;


public class AddCity extends Control {
	WorldMap map;
	City city;
	public AddCity(WorldMap _map,City _city){
		map =_map;
		city=_city;
	}

	public void execute() {
		map.addCity(city);
		map.selectCity(city);
	}

	public void undo() {
		for(int j=0;j<city.linkCities.length;j++){
			if(city.linkCities[j]!=null)
				city.linkCities[j].linkCities[city.linkDoor[j]]=null;
		}
		map.removeCity(city);
		map.selectCity((City)null);
	}

}
