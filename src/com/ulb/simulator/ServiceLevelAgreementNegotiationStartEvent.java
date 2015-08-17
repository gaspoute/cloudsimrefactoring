package com.ulb.simulator;

public class ServiceLevelAgreementNegotiationStartEvent extends ServiceLevelAgreementNegotiationEvent {

    public ServiceLevelAgreementNegotiationStartEvent(Object source, Object target, ServiceLevelAgreement serviceLevelAgreement) {
        super(source, target, serviceLevelAgreement);
    }
}
