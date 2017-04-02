package pillaihoc.facerecognizedvoting.pojo;

/**
 * Created by deepakgavkar on 20/03/17.
 */
public class VotingEventStruct {
    String id,eventname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    @Override
    public String toString() {
        return this.eventname;
    }
}
