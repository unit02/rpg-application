package nz.ac.auckland.avatar.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/** 
 *Represents the avatar's bag. Holds items.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Bag {

	@XmlElement(name="items")
	private Set<Item> _items;
	
	public Bag() {
		_items = new HashSet<Item>();
	}
	
	public void addItem(Item item) {
		_items.add(item);
	}
	
	public void removeItem(Item item) {
		_items.remove(item);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Bag))
            return false;
        if (obj == this)
            return true;

        Bag bag = (Bag) obj;
        return new EqualsBuilder().
            append(_items, bag._items).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_items).
	            toHashCode();
	}
	
	@Override
	public String toString() {
		Set<Item> allItems = new HashSet<Item>();
		for(Item item : _items) {
			allItems.add(item);
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Items stored in bag: ");
		
		for(Item item : allItems) {
			buffer.append(item.getItemType() +  " Description:" + item.getDescription() 
			+ "Increases relevant stats by " + item.getStatIncreaseAmount()+ " points, ");
		}
		buffer.delete(buffer.length()-2, buffer.length()-1);
				
		return buffer.toString();
			
	}
	
	public enum ItemType {
		MANA_POTION, HEALTH_POTION, STAFF, SWORD, SHIELD,;
	}
	
	/**
	 * Class to represent a particular criminal item. A item is made up 
	 * of one or more Profile.Offence tags, the date of item, and a 
	 * description of the item.
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Item {
		
		@XmlElement(name="item_type")
		private ItemType _itemType;

		
		@XmlElement(name="stat_increase_amount")
		private int _statIncreaseAmount;
		
		@XmlElement(name="item_description")
		private String _description;
		
		protected Item() {
			// Required by JAXB.
			this((Integer) null,null,null);
		}
		
		public Item(int increaseAmount,
				String description,
				ItemType itemType) {
			_statIncreaseAmount = increaseAmount;
			_description = description;
			_itemType = itemType;

		}

		
		public int getStatIncreaseAmount() {
			return _statIncreaseAmount;
		}
		
		public String getDescription() {
			return _description;
		}
		public ItemType getItemType(){
			return _itemType;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Item))
	            return false;
	        if (obj == this)
	            return true;

	        Item i = (Item) obj;
	        return new EqualsBuilder().
	            append(_statIncreaseAmount, i._statIncreaseAmount).
	            append(_description, i._description).
	            append(_itemType,i._itemType).
	            isEquals();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 31). 
		            append(_statIncreaseAmount).
		            append(_description).
		            append(_itemType).
		            toHashCode();
		}
	}
}
