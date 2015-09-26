package nz.ac.auckland.avatar.dto;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import nz.ac.auckland.avatar.domain.Item;

@XmlRootElement(name="bag")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bag {

    @XmlElementWrapper(name="items")
    @XmlElement(name="item")
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
	
	
}
