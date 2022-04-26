package woody44.minecraft.core.player;

import com.google.gson.Gson;

public class PlayerAbilitiesData {

    public String Serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PlayerAbilitiesData Build(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, PlayerAbilitiesData.class);
    }
}
