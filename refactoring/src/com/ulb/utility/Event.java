package com.ulb.utility;


public class Event implements Comparable<Event> {
    private final ObjectId id = new ObjectId();
    private Object source;
    private Object target;
    private long when;

    public Event(Object source) {
        this.source = source;
    }
    
    public Event(Object source, Object target) {
        this.source = source;
        this.target = target;
    }

    public Event(Object source, long when) {
        this.source = source;
        this.when = when;
    }
    
    public Event(Object source, Object target, long when) {
        this.source = source;
        this.target = target;
        this.when = when;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+" from "+source.getClass().getSimpleName()+(target != null ? " to "+target.getClass().getSimpleName() : "");
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !(object instanceof Event)) {
            return false;
        }
        Event event = (Event) object;
        return id.equals(event.id);
    }

    @Override
    public int compareTo(Event event) {
        if (when < event.when) {
            return -1;
        }
        if (when > event.when) {
            return 1;
        }
        return id.compareTo(event.id);
    }
    
    public boolean isFrom(Object anotherSource) {
        return source == null && anotherSource == null || source != null && source.equals(anotherSource);
    }
    
    public boolean isTo(Object anotherTarget) {
        return target == null && anotherTarget == null || target != null && target.equals(anotherTarget);
    }
    
    public Object getSource() {
        return source;
    }

    public long getWhen() {
        return when;
    }
}