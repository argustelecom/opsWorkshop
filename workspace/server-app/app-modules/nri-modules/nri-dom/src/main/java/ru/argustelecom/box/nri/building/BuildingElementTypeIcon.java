package ru.argustelecom.box.nri.building;

/**
 * Иконки для типов элементов строения
 * Created by s.kolyada on 01.09.2017.
 */
public enum  BuildingElementTypeIcon {

	FA_BUILDING("fa fa-building", "Building"),

	FA_BED("fa fa-bed", "Bed"),

	FA_CAR("fa fa-car", "Car"),

	FA_HOME("fa fa-home", "Home"),

	FA_ROOF("fa fa-eject", "Roof"),

	FA_BASEMENT("fa fa-codepen","Basement");

	/**
	 * Имя для испольщования в html
	 */
	private String faName;

	/**
	 * Заголовок иконки
	 */
	private String label;

	BuildingElementTypeIcon(String faName, String label) {
		this.faName = faName;
		this.label = label;
	}

	public String getFaName() {
		return faName;
	}

	public String getLabel() {
		return label;
	}
}
