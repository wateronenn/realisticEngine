# Legends of ProgMeth

Legends of ProgMeth is a **turn-based strategy RPG** developed using **Java and JavaFX**.  
Players assemble a team of heroes and battle progressively stronger monsters through strategic skill usage, cooldown management, and elemental advantages.

---

# Game Overview

Legends of ProgMeth is a turn-based combat game where players choose heroes and fight monsters across multiple stages.

Players must strategically manage:

- Hero abilities
- Skill cooldowns
- Element advantages
- Team survival

As stages increase, monsters become stronger, requiring better planning and coordination between heroes.

---

# Core Gameplay Loop

1. Player selects **3 heroes out of 4 available heroes**
2. Player enters battle
3. Heroes perform actions
4. Monsters perform actions
5. Stage is cleared
6. Heroes scale and prepare for the next stage
7. Loop continues until the team is defeated

---

# Heroes

There are **4 hero classes** in the game.

## Archer
A high-damage scaling attacker.

Abilities:
- **Skill** – increases bow stack
- **Ultimate** – deals area damage based on bow stacks

Strengths:
- Extremely high damage potential
- Powerful AoE attacks

Weaknesses:
- Low survivability

---

## Caster
A magic damage dealer that buffs itself.

Abilities:
- **Skill** – deals damage and increases own attack
- **Ultimate** – deals AoE magic damage

Strengths:
- Strong burst damage
- Self-buff capability

Weaknesses:
- Low defense

---

## Fighter
A sustain damage dealer.

Abilities:
- **Skill** – deals damage and heals self
- **Ultimate** – deals damage based on enemy maximum HP

Strengths:
- Self-healing
- High durability

Weaknesses:
- Lower burst damage

---

## Tank
A defensive support hero.

Abilities:
- **Skill** – heals a single ally
- **Ultimate** – gives shield and attack buff to team

Strengths:
- Team protection
- Healing support

Weaknesses:
- Low damage output

---

# Monster System

Monsters are generated dynamically using a **MonsterFactory** system.

Monster types:

| Type | Description |
|-----|-------------|
| Type1 | High HP monster |
| Type2 | High attack monster |
| Type3 | Balanced monster |

Each stage increases monster stats through scaling formulas.

---
# How to Run the Project

## Requirements

- Java **17 or newer**
- Gradle

---

## Run with Gradle

### Windows

```
gradlew run
```

### Mac / Linux

```
./gradlew run
```

---

# VM Setup (Course Environment)

To ensure the project runs correctly on the course virtual machine.

## Install Java

```
sudo apt update
sudo apt install openjdk-17-jdk
```

Verify installation:

```
java -version
```

---

## Install JavaFX

```
sudo apt install openjfx
```

---

## Clone the Repository

```
git clone <repository-url>
cd LegendsOfProgMeth
```

---

## Run the Game

```
./gradlew run
```
---
## Gradle JVM Graphics Configuration
```
application {
    applicationDefaultJvmArgs = [
        "-Dprism.order=es2,sw",
        "-Dprism.verbose=true"
    ]
}
```
---

# Controls

During battle:

1. Select hero action
2. Select target
3. Execute ability

Heroes act first, followed by monsters.

---

# Development Team


- 6833290021 Abhabhichaya B.
- 6833073221 Naphat H.
- 6833135521 Norraphat R.
- 6833203521 Puttisan K.

Course: **Programming Methodology**

---

# Future Improvements

Possible extensions for the project:

- Equipment system
- More hero classes
- More monster types
- Save/load system
- PvP mode
- Difficulty modes
- Expanded stage system

---

# License

This project was developed for educational purposes as part of the Programming Methodology course.