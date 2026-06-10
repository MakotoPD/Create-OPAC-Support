package pl.makoto.createsupportopac.settings;

import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class CreateClaimSettings {

    private final Map<CreateMachineType, Boolean> strangerAllowed = new EnumMap<>(CreateMachineType.class);
    private final Map<CreateMachineType, Boolean> memberAllowed   = new EnumMap<>(CreateMachineType.class);
    private final Map<CreateMachineType, Boolean> allyAllowed     = new EnumMap<>(CreateMachineType.class);

    public CreateClaimSettings() {
        for (CreateMachineType t : CreateMachineType.values()) {
            strangerAllowed.put(t, true);
            memberAllowed.put(t, true);
            allyAllowed.put(t, true);
        }
    }

    public boolean isAllowed(CreateMachineType type, ClaimRelationship rel) {
        return map(rel).getOrDefault(type, true);
    }

    public boolean isAllowed(CreateMachineType type) {
        return isAllowed(type, ClaimRelationship.STRANGER);
    }

    public void setAllowed(CreateMachineType type, ClaimRelationship rel, boolean value) {
        map(rel).put(type, value);
    }

    public void setAllowed(CreateMachineType type, boolean value) {
        setAllowed(type, ClaimRelationship.STRANGER, value);
    }

    public void reset() {
        for (CreateMachineType t : CreateMachineType.values()) {
            strangerAllowed.put(t, true);
            memberAllowed.put(t, true);
            allyAllowed.put(t, true);
        }
    }

    private Map<CreateMachineType, Boolean> map(ClaimRelationship rel) {
        return switch (rel) {
            case STRANGER -> strangerAllowed;
            case MEMBER   -> memberAllowed;
            case ALLY     -> allyAllowed;
        };
    }

    // Flat array encoding: for each machine type in values() order:
    //   index * 3 + 0 = stranger, +1 = member, +2 = ally
    public boolean[] toArray() {
        CreateMachineType[] types = CreateMachineType.values();
        boolean[] arr = new boolean[types.length * 3];
        for (int i = 0; i < types.length; i++) {
            arr[i * 3]     = strangerAllowed.getOrDefault(types[i], true);
            arr[i * 3 + 1] = memberAllowed.getOrDefault(types[i], true);
            arr[i * 3 + 2] = allyAllowed.getOrDefault(types[i], true);
        }
        return arr;
    }

    public static CreateClaimSettings fromArray(boolean[] arr) {
        CreateClaimSettings s = new CreateClaimSettings();
        CreateMachineType[] types = CreateMachineType.values();
        for (int i = 0; i < types.length; i++) {
            int base = i * 3;
            if (base + 2 < arr.length) {
                s.strangerAllowed.put(types[i], arr[base]);
                s.memberAllowed.put(types[i], arr[base + 1]);
                s.allyAllowed.put(types[i], arr[base + 2]);
            }
        }
        return s;
    }

    public Map<CreateMachineType, Boolean> getAllSettings() {
        return new EnumMap<>(strangerAllowed);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        for (CreateMachineType t : CreateMachineType.values()) {
            tag.putBoolean(t.id + "_s", strangerAllowed.getOrDefault(t, true));
            tag.putBoolean(t.id + "_m", memberAllowed.getOrDefault(t, true));
            tag.putBoolean(t.id + "_a", allyAllowed.getOrDefault(t, true));
        }
        return tag;
    }

    public static CreateClaimSettings load(CompoundTag tag) {
        CreateClaimSettings s = new CreateClaimSettings();
        for (CreateMachineType t : CreateMachineType.values()) {
            if (tag.contains(t.id + "_s")) {
                s.strangerAllowed.put(t, tag.getBoolean(t.id + "_s"));
                s.memberAllowed.put(t, tag.getBoolean(t.id + "_m"));
                s.allyAllowed.put(t, tag.getBoolean(t.id + "_a"));
            } else if (tag.contains(t.id)) {
                // backward compat: old single-value format
                boolean v = tag.getBoolean(t.id);
                s.strangerAllowed.put(t, v);
                s.memberAllowed.put(t, v);
                s.allyAllowed.put(t, v);
            }
        }
        return s;
    }
}
