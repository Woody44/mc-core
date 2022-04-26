package woody44.minecraft.core.player;

import com.google.gson.Gson;

public class QuestsData {

    public String Serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static QuestsData Build(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, QuestsData.class);
    }
}
