package nz.ac.auckland.avatar.domain;

public enum Category {
	MAGE, ROUGE, BARBARIAN, MONK;
/*
 * Returns the category of the avatar
 */
	public static Category fromString(String input){
		if (input != null){
			for(Category cat : Category.values()){
				if (input.equalsIgnoreCase(cat.toString())){
					return cat;
				}
			}
		}
		return null;
	}
}
