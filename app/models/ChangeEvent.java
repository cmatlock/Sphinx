package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.List;

/**
 * Created by Striker on 2/4/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeEvent extends Event{
	
	private static MongoCollection _events(){
		return PlayJongo.getCollection("events");
	}
	
	public void remove(){
        _events().remove(this.id);
    }

    public void removeAll(){
        _events().remove();
    }

    public ChangeEvent insert(){
        _events().save(this);
        return this;
    }


    public ChangeEvent(){}


    protected long eventDate;
    protected String changedBy;

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}