# Fishing by Ori

my fishing plugin with custom augments, totems, mods and blah blah blah

## Please support PyroTempus

First note is **please support and purchase** [PyroFishing by PyroTempus](https://www.spigotmc.org/resources/pyrofishingpro-1-14-x-1-21-x-1-fishing-plugin-new-tournament-rework.60729/), He deserves credit and praise for the initial project concept and many of the initial ideas presented.

## Why?

While working at CraftYourTown in Dec. 2025, I found myself frustrated with the way that PyroFishingPro worked on the server.
Since that day, I decided to create my own version of the plugin, that aimed to be free of the barriers that I felt within the plugin.

This is one of my biggest projects to date, and I was very excited to create something to this scale along with any challenges that were presented.
****

### What were these frustrations?

**An open sourced plugin with a good API and trust for your community can fix these issues, bring a sense of community within the group of people who have supported your project.**

- PyroFishingPro is a closed source with little to no api availability
- Several features that are not finished would be pushed into production builds
    - If your plugin is on several large servers/networks, You should be utilising branches and not publishing unfinished content onto official releases with no warning. Git has branches for a reason... use them
- Certain augments are not available outside seasonal events which makes refunding lost/broken fishing rods difficult (This is due to the developer not wishing for servers to sell them outside Crabmas)
    - Buyers of the plugin would sell "Crabmas" Augments outside of the event, A decision was made to where this is no longer possible, Allegedly due to pyro personally disliking people doing that.
    - If it's true, I don't agree with policing how features of your plugin is utilised
    - **Admin Note** - Run the command below, apply the firework star to a basic rod & then strip augments. You should now have a regular snow globe augment that you should probably keep in supply somewhere in case you need it.
    - `/minecraft:give @p firework_star[custom_data={PublicBukkitValues:{"pyrofishingpro:augment":"SNOW_GLOBE"}}]`
- Some balance and features is at the developer's mercy; granting little agency to the server owners/developers over what happens on their server
    - What If I want new augments, skills, totem upgrades?
    - What if I want a community totem for use?
    - What if I want new rarities for each fish and more conditions?
    - What if I wanted to make the process of catching fish into a minigame
    - What if I wanted to add additional loot and mobs within the plugin
    - What if I wanted to add more seasonal events aside from Crabmas
    - What if I want to do complete overhaul of \<insert feature here>
- `"This purchase is for one server or network and cannot be used on multiple servers."` is cringe!
- I said how I didn't like the event cause it took hours n got banned from Pyro's discord lol

**On a more direct note: Pyro, You've got a wonderful plugin here, but for whatever reason, you do not grant space for your project to grow beyond your own ability and ideas. I understand having your own vision for something you've dedicated a lot of time into, but you have to place trust in your community to be able to help your vision or their own.**

****

## Plugin Requirements

This plugin will always be compiled against the latest Paper API Versions (as of writing, its 26.2) with no goal in supporting anything lower than latest. this is because i want to be stubborn and defiant, and because their api changes go hard.

Folia is also supported too :)
## Resource Pack

This plugin has a resource pack available within the `/resources/` folder within the plugin. This resource pack does not apply by default.

**Artists**

- [MythicSloth](https://pixelsloth.net/) - Created the icons used for totem upgrades

## Future Features/Ideas

a bunch of stuff i do want to add :3

### Augments

Augments can be applied to fishing rods to grant them unique buffs to aid the user in catching additional fish, These augments will take up slot capacities on the fishing rod depending on how far the augment has been upgraded.

If an augment is applied to a rod and causes the slot capacity to be exceeded, It will not be allowed on the rod (If a player manages to apply an augment that exceeds the capacity, The augment cannot be used)

#### Augment Ideas

- Biome Blend (Chance to ignore biome restrictions)
- Enlightened (Increases the fisher experience gained while catching fish)
- Failure (Meme Augment; Strikes the player with lightning when they miss their augment)
- Fine Slicing (Increases the entropy gained from gutting a fish)
- Genius (Increases the minecraft experience gained from catching fish)
- Hotspot (While the weather is sunny, allows the user to catch multiple fish at once)
- Indulge (Chance to increase the player's saturation level)
- Make It Rain (Chance to rain multiple fish on the player at once)
- Rain Dance (While the weather is raining, allows the user to catch multiple fish at once)

### Totem

Totems are entities placed by players to provide a buff to nearby users while activated. Totems have a limited range, duration and cooldown along with several upgrades which are obtained by levelling up the totem and spending entropy.

#### Upgrade Ideas

- Gravity Well (Pulls nearby items towards the totem)
- Vacuum (Automatically picks up nearby fish in a withdrawal only inventory)
- Weather Flux (Increased By Speed by adding fake rain)

### Skills