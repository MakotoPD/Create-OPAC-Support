package pl.makoto.createsupportopac.settings;

public enum ClaimRelationship {
    STRANGER, MEMBER, ALLY;

    public String displayName() {
        return switch (this) {
            case STRANGER -> "Strangers";
            case MEMBER -> "Party Members";
            case ALLY -> "Allies";
        };
    }
}
