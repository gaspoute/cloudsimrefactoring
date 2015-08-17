package com.ulb.utility;


public class TargetFilter implements Filter {
    private Object target;
    
    public TargetFilter(Object target) {
        this.target = target;
    }
    
    @Override
    public boolean accepts(Event event) {
        return event.isTo(target);
    }
}
