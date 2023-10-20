package notker.blockgame_exp_hud.helper;

public enum MMOItemsItemTypesHelper {
    ALL(""),
    RUNES("RUNECARVING");

    private final String tag;

    MMOItemsItemTypesHelper(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return this.tag;
    }
}
