package woody44.minecraft.core.npcs;

import javax.faces.view.Location;

public class NPC {
    
    private Location startLocation;
    private NPC(){

    }

    public static NPC createNpc(String id, String name){
        
        return new NPC();
    }
}
