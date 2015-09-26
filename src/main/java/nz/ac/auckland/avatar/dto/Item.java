package nz.ac.auckland.avatar.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.avatar.domain.ItemType;

/**
 * Class to represent a particular criminal item. A item is made up 
 * of one or more Profile.Offence tags, the date of item, and a 
 * description of the item.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Item {
	
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
