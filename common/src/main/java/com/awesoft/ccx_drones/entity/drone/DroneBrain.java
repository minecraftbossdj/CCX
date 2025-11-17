package com.awesoft.ccx_drones.entity.drone;

public class DroneBrain {
    private final DroneEntity drone;
    public DroneBrain(DroneEntity drone) {
        this.drone = drone;
    }

    public DroneEntity getOwner() {
        return drone;
    }

    public int getSelectedSlot() {
        return 1;
    }
}

