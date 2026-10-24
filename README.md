<div align="center">

# 🦟 Mosquitoes Reforged

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.12.2-blue.svg?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/)
[![Forge](https://img.shields.io/badge/Forge-14.23.5.2847+-orange.svg?style=for-the-badge)](https://files.minecraftforge.net/)
[![Cleanroom](https://img.shields.io/badge/Cleanroom-Compatible-success.svg?style=for-the-badge)](https://github.com/CleanroomMC)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

**An expansive, highly optimized 1.12.2 Forge downport and overhaul of [vemerion's Mosquitoes](https://github.com/vemerion/Mosquitoes).**  
*Adds full physical world mobs, leaping ticks, mob bloodsucking, environmental rules, armor protection, seamless audio, and polished first-person interactions!*

---

[Key Features](#-key-features) • [Differences & Benefits](#-differences--benefits-vs-original) • [Items & Mechanics](#-items--mechanics) • [Brewing & Citronella](#-brewing--citronella) • [Mod Compatibility](#-mod-compatibility) • [Credits](#-credits)

---

</div>

<br>

## 🌟 Overview

**Mosquitoes Reforged** brings everyone's favorite pests into Minecraft 1.12.2 with a major gameplay upgrade. Unlike the original 1.16+ mod where mosquitoes and ticks were purely first-person overlay effects, **Mosquitoes Reforged** implements a **dual-layer ecosystem**:

1. **3D Physical World Entities**: Encounter actual buzzing mosquitoes and stealthy ticks in the game world with custom AI, flight paths, and pouncing mechanics.
2. **Enhanced First-Person Mini-game**: Retains the immersive first-person swatting, arm-crawling ticks, and hand-waving mechanics, reworked with realistic scaling, responsive aiming, and smooth audio crossfading.

---

## ⚡ Differences & Benefits (vs. Original 1.16 Mod)

| Feature / Aspect | Original Mod (1.16.4) | Mosquitoes Reforged (1.12.2) | Benefits |
| :--- | :--- | :--- | :--- |
| **Physical World Mobs** | ❌ None (HUD overlay only) | ✔️ **`EntityMosquito` & `EntityTick`** | Visible in 3D world, interactive in multiplayer, spawn eggs included |
| **Living Mob Host Mounting** | ❌ None | ✔️ **Ticks latch onto animals & villagers** | Ticks jump onto livestock and villagers, sucking blood until removed |
| **Tick Leaping AI** | ❌ None | ✔️ **`EntityAITickLeap` AI Task** | Ticks ambush players and animals from nearby vegetation |
| **Swatter Weaponry** | ⚠️ Right-click RNG timer only | ✔️ **3D Radius Swat + Left-Click** | Holding right-click swats world & HUD mosquitoes; direct melee smack |
| **Tweezers Usability** | ⚠️ Strict 75-tick countdown | ✔️ **Continuous Hold + Active Pinch** | Hold indefinitely; left-click snip with metallic sound feedback |
| **World Plucking** | ❌ Self only | ✔️ **Extract ticks from animals & players** | Save your cows, sheep, and friends by plucking ticks off their backs |
| **Armor Protection** | ❌ Ignored | ✔️ **Deflection chance + Clink audio** | Full armor reduces bites (up to 3.2x) and tick latches (~5x) |
| **Spawning Boundaries** | ⚠️ Blacklist only; caves infested | ✔️ **Surface Only (`Y >= 50`), Overworld** | No mosquitoes in deep mines, Nether, End, or dimensions |
| **Nocturnal & Swamp Ecology**| ⚠️ Static rates | ✔️ **Nighttime, Swamp & Water weighting** | Realistic insect activity near water bodies and after dusk |
| **Vegetation Recognition** | ⚠️ Vanilla tall grass only | ✔️ **Universal `VegetationHelper`** | Native support for Biomes O' Plenty, Dynamic Trees, Botania, etc. |
| **First-Person Proportions** | ⚠️ `0.06f` scale (bird-sized) | ✔️ **`0.0075f` scale (8x smaller)** | Hyper-realistic insect proportions on your screen |
| **Camera Aim Dynamics** | ❌ Static screen coordinates | ✔️ **Dynamic look tracking** | Mosquitoes react to mouse movements for natural crosshair aiming |
| **Buzzing Sound Engine** | ⚠️ Single looping track | ✔️ **4-track crossfaded sound array** | Eliminates repetitive looping fatigue with smooth 2-tick overlaps |
| **Death Handling** | ⚠️ Tick survived player death | ✔️ **Spawns `EntityTick` into the world** | Drops where you died, ready to ambush your recovery run |
| **Citronella Generation** | ⚠️ Savannas only | ✔️ **Savannas, Deserts, Mesas, Sand** | Spawns across arid biomes; plantable on sand, clay, dirt, and grass |

---

## 🦟 Key Features

### 1. 🪰 Physical Mosquitoes (`EntityMosquito`)
- **Tiny Hitbox (`0.05 x 0.05`)**: Pixel-accurate scale makes swatting an authentic challenge.
- **Dynamic Flight Pattern**: Chaotic flight trajectories featuring high-frequency noise and sudden dart impulses.
- **Audio Crossfading**: Alternates across 4 recorded buzz clips every 20 ticks, providing dynamic pitch variations without jarring cuts.
- **Targeting**: Hunts players, livestock, and villagers. Inflicts direct armor-piercing damage (`sucking`) and can transmit **Malaria**.
- **Armor Defense**: Wearing armor reduces bite probability; deflected bites bounce off with a metallic clink.

### 2. 🕷️ Leaping Ticks (`EntityTick`)
- **Lurking & Pouncing**: Lurks quietly in bushes and tall grass before pouncing with its leap attack.
- **Parasitic Mounting**: Latches onto the backs and shoulders of bipedal entities (villagers, players) and the spine of quadruped animals (cows, pigs, horses, sheep).
- **Blood Draining**: Sucks blood every 8 seconds, dealing damage and slowing down host mobs.
- **World Removal**: Sneak up with tweezers to pluck ticks directly off livestock and villagers!

### 3. 🎯 Upgraded Tool Mechanics

<table>
  <tr>
    <td width="50%">
      <h3 align="center">🏸 Mosquito Swatter</h3>
      <ul>
        <li><b>Hold Right-Click</b>: Creates an active defensive barrier swatting any physical mosquito within 3 blocks.</li>
        <li><b>First-Person Swatting</b>: Left-click swings the swatter across your screen to squash attacking mosquitoes.</li>
        <li><b>Direct Combat</b>: Deals melee smack damage with custom sound effects.</li>
      </ul>
    </td>
    <td width="50%">
      <h3 align="center">✂️ Tick Tweezers</h3>
      <ul>
        <li><b>Hold Indefinitely</b>: No rigid countdown timer; examine your arm calmly for crawling ticks.</li>
        <li><b>Left-Click Snip</b>: Active pinch mechanic with instant audio feedback (metallic snip on miss, tearing sound on hit).</li>
        <li><b>Veterinary Care</b>: Left-click on infected animals or teammates to pluck ticks without injuring the host.</li>
      </ul>
    </td>
  </tr>
</table>

---

## 🧪 Brewing & Citronella

<div align="center">
  <img src="src/main/resources/assets/mosquitoes/textures/mob_effect/citronella_effect.png" width="48" height="48" alt="Citronella Effect">
  <img src="src/main/resources/assets/mosquitoes/textures/mob_effect/malaria_cure_effect.png" width="48" height="48" alt="Malaria Cure Effect">
  <img src="src/main/resources/assets/mosquitoes/textures/mob_effect/malaria_effect.png" width="48" height="48" alt="Malaria Effect">
</div>

### 🌿 Citronella Block & Oil
- **Citronella Flower**: Naturally generates across savannas, deserts, mesas, badlands, and arid biomes. Can be sustained on sand, dirt, farmland, and hardened clay.
- **Citronella Oil Potion**:
  - `Awkward Potion` + `Citronella Flower` ➔ **Potion of Citronella Oil (20:00)**
  - Extend with `Redstone` ➔ **Potion of Citronella Oil (40:00)**
  - **Effect**: Repels physical mosquitoes (causing them to flee) and permanently chases away HUD mosquitoes.

### 🦟 Malaria & The Cure
- Mosquito bites carry a chance of transmitting **Malaria**, periodically afflicting the victim with blindness, nausea, slowness, and poison.
- **Potion of Malaria Cure**:
  - `Awkward Potion` + `Mosquito Wing` ➔ **Potion of Malaria Cure**
  - Instantly cures malaria infections.

---

## 🛠️ Crafting Recipes

<div align="center">

```
Mosquito Swatter:                Tick Tweezers:
[Cobweb] [Cobweb] [Cobweb]        [   ] [Iron] [Iron]
[Cobweb] [Cobweb] [Cobweb]        [Iron] [   ] [Iron]
[      ] [Stick ] [      ]        [Iron] [Iron] [Iron]
```

</div>

---

## 🌲 Mod Compatibility

**Mosquitoes Reforged** is built to seamlessly fit into large 1.12.2 modpacks:
- **Cleanroom & Forge**: 100% compatible with modern Cleanroom 1.12.2 builds as well as standard Forge `14.23.5.2847+`.
- **Vegetation Mod Integration**: Automatically detects foliage and bushes from **Biomes O' Plenty**, **Dynamic Trees**, **Pam's HarvestCraft**, **Botania**, **Traverse**, and other world-gen mods.
- **Entity & Creature Whitelist**: Recognizes custom passive creatures, villagers, and animals from mods like **Animania**, **Primitive Mobs**, and **MCA**.

---

## 📜 Credits & License

- **Original Creator**: [vemerion](https://github.com/vemerion) for the original [Mosquitoes](https://github.com/vemerion/Mosquitoes) concept, models, and audio.
- **Port & Overhaul**: Developed by [Brilliafy](https://github.com/Brilliafy).
- **License**: MIT License. Feel free to use this in any modpack!
