package com.ulb.simulator;

import com.ulb.utility.Event;

public class ServiceLevelAgreementNegotiationEvent extends Event {
    private ServiceLevelAgreement serviceLevelAgreement;
    
    public ServiceLevelAgreementNegotiationEvent(Object source, Object target, ServiceLevelAgreement serviceLevelAgreement) {
        super(source, target);
        this.serviceLevelAgreement = serviceLevelAgreement;
    }
    
    public ServiceLevelAgreementNegotiationEvent(Object source, ServiceLevelAgreement serviceLevelAgreement) {
        super(source);
        this.serviceLevelAgreement = serviceLevelAgreement;
    }

    public ServiceLevelAgreement getServiceLevelAgreement() {
        return serviceLevelAgreement;
    }
}
