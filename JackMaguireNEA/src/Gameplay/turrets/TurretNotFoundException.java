package Gameplay.turrets;

public class TurretNotFoundException extends Exception { //exception for when the turret has not yet been found

    public TurretNotFoundException() {
        super("TURRET LOCATION NOT FOUND");
    }
}
