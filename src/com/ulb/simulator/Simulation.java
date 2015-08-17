package com.ulb.simulator;

import com.ulb.utility.EventService;

public class Simulation {
    private Broker broker;

    public Simulation(Broker broker) {
        this.broker = broker;
    }

    public void run() {
        EventService.getInstance().publishEvent(new SimulationStartEvent(this, broker));
        EventService.getInstance().dispatch();
    }
}
