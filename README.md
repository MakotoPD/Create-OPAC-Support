# Create OPAC

**Create machine permissions for Open Parties and Claims land claims.**

A NeoForge mod for Minecraft 1.21.1 that integrates [Create](https://modrinth.com/mod/create) with [Open Parties and Claims (OPAC)](https://modrinth.com/mod/open-parties-and-claims). Claim owners can control exactly which Create machines are allowed to operate on their claimed chunks, and for whom.

---

## Requirements

| Dependency | Version | Required |
|---|---|---|
| Minecraft | 1.21.1 | ✅ |
| NeoForge | 21.1.228+ | ✅ |
| [Create](https://modrinth.com/mod/create) | 6.0.0+ | ✅ |
| [Open Parties and Claims](https://modrinth.com/mod/open-parties-and-claims) | 0.26.0+ | ✅ |
| [LuckPerms](https://luckperms.net/) | 5.4+ | ⚪ Optional |

---

## Features

### Per-claim machine permissions

Every claim owner can individually allow or deny each Create machine type for three groups of players:

| Group | Description |
|---|---|
| **Strangers** | Players with no relation to the claim owner |
| **Party Members** | Members of the owner's OPAC party |
| **Allies** | Players whose parties are allied with the owner's party |

### Supported machine types

| Machine | What it controls |
|---|---|
| Contraption | Movement of any assembled contraption |
| Deployer | Deployer arms placing/using items |
| Harvester | Harvester blades collecting crops |
| Drill | Drills and block-breaking contraptions |
| Plough | Plough tilling soil |
| Mechanical Arm | Mechanical Arm interactions |
| Schematicannon | Schematicannon firing |
| Open-ended Pipe | Fluid extraction via open-ended pipes |
| Train | Driving and relocating trains |
| Super Glue | Applying/removing Super Glue |
| Toolbox | Remote toolbox access |
| Elevator | Using elevator contacts |
| Wrench (radial menu) | Wrench radial menu on contraption parts |
| Entity interactions | General entity interactions in the claim |
| Marketplace | TableCloth shops, Blaze Burner vendors, entities sitting on Create seats |

### Marketplace support

Players visiting a claim can interact with Create's shop infrastructure when **Marketplace** is enabled:
- Right-clicking **TableCloth** to browse items
- Right-clicking a **Blaze Burner** vendor
- Paying at an **entity sitting on a Create seat** (e.g. using a Shopping List item)

### Train ownership

Train owners can always drive and relocate **their own trains**, regardless of the claim's Train permission setting. This prevents players from being locked out of trains they built themselves.

### Admin configuration panels

Server operators and players with admin access can manage global config profiles in addition to their own claim settings:

| Panel | Purpose |
|---|---|
| Server Claims Config | Default settings applied to all server-owned claims |
| Expired Claims Config | Settings for claims whose owner has been inactive |
| Wilderness Config | Settings for unclaimed chunks |
| Default Player Config | Fallback settings used when a player has no personal config |
| Edit Player Settings | Override settings for a specific player |

---

## How to use

1. Open the **OPAC main menu** in-game (OPAC keybind or `/pac` command).
2. Click the **"Create Settings"** button (bottom-left corner).
3. The **Create Machine Settings** screen opens with the available panels.
4. Select a panel, then toggle permissions per machine type and player group.

Changes are saved on the server immediately and apply to all incoming contraption interactions.

---

## Admin access

A player is considered an admin (and can see the additional config panels) if **any** of the following is true:

- They have vanilla operator level ≥ 2 (`/op`)
- They have OPAC admin mode enabled (`/pac admin on`)
- They have the LuckPerms permission node `createsupportopac.admin`

---

## LuckPerms permission node

```
createsupportopac.admin
```

Grant this node to allow a player to manage server-wide Create OPAC config panels without giving them full server operator access.

---

## Compatibility

- Fully server-side authoritative — clients do not need the mod to have permissions enforced on the server.
- Compatible with [easy_npc](https://modrinth.com/mod/easy-npc) — NPC entities from that mod bypass the Entity interaction check automatically.
- Does not conflict with OPAC's own protection settings; both systems can coexist.

---

## Author

**Makoto**

---

## License

All Rights Reserved.
