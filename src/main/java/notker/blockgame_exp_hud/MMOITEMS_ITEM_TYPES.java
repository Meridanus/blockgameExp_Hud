package notker.blockgame_exp_hud;

public enum MMOITEMS_ITEM_TYPES {
    ALL(""),
    RUNES("RUNECARVING");

    private final String tag;

    MMOITEMS_ITEM_TYPES(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return this.tag;
    }
}
