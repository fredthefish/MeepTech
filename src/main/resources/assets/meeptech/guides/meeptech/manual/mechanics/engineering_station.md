---
navigation:
    title: Engineering Station
    icon: meeptech:engineering_station
    parent: mechanics.md
    position: 4
item_ids:
- meeptech:engineering_station
categories:
- Mechanics
---
# Engineering Station
The material workstation is used to design machines.
## Crafting
The material workstation is crafted with 5 bricks and 4 blank modules. Blank modules are crafted with 1 cobblestone and 1 paper.
<RecipeFor id="meeptech:engineering_station" />
## Usage
Machines are organized in a tree structure, where each slot has a parent slot, up to the root, the hull.
- The hull you want to edit is to be inserted into the "edit" slot.
- To add modules or components to hulls or modules, put the module in the "in" slot and click on the module slot.
- To select modules in order to see their submodules, click on the module. To deselect, click again.
- To remove a module, right click it to extract to the "out" slot. This can only be done for the lowest level of the tree, meaning components, or modules that have no submodules or components.