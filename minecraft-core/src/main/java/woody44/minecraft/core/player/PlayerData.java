package woody44.minecraft.core.player;

import com.google.gson.Gson;

class PlayerData {
    public String latestProfile = "Default";

    public String Serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PlayerData Build(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, PlayerData.class);
    }
}