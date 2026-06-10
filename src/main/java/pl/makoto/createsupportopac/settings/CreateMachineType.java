package pl.makoto.createsupportopac.settings;

public enum CreateMachineType {
    CONTRAPTION("contraption", "Contraption movement"),
    DEPLOYER("deployer", "Deployer"),
    HARVESTER("harvester", "Harvester"),
    DRILL("drill", "Drill / block-breaking contraption"),
    PLOUGH("plough", "Plough"),
    ARM("arm", "Mechanical Arm"),
    CANNON("cannon", "Schematicannon"),
    PIPE("pipe", "Open-ended pipe"),
    TRAIN("train", "Train controls / relocation"),
    SUPERGLUE("superglue", "Super Glue"),
    TOOLBOX("toolbox", "Toolbox"),
    ELEVATOR("elevator", "Elevator"),
    WRENCH("wrench", "Wrench (radial menu)"),
    ENTITY("entity", "Entity interactions"),
    MARKETPLACE("marketplace", "Marketplace shop");

    public final String id;
    public final String displayName;

    CreateMachineType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static CreateMachineType fromId(String id) {
        for (CreateMachineType t : values()) {
            if (t.id.equalsIgnoreCase(id)) return t;
        }
        return null;
    }
}
