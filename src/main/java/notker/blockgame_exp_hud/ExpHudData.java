package notker.blockgame_exp_hud;

public class ExpHudData {
    // Toggle for the Exp Hud Render
    public static boolean hideOverlay = false;
    // Toggle for switching between Total and Sample
    //TODO Change to hour based system
    public static boolean showGlobal = true;


    public static final Integer DEFAULT_MAX_SAMPLE_VALUE = 128;
    public static final Integer DEFAULT_BASE_BONUS_EXP = 10;

    public static int DEFAULT_TEXT_HEIGHT = 8;

    public static final String DEFAULT_RUNE_ITEM_TYPE_TAG = "MMOITEMS_ITEM_TYPE";



    public static final String[] professionNames = {
            "Archaeology",
            "Fishing",
            "Herbalism",
            "Logging",
            "Mining",
            "Runecarving",
            "Einherjar"};
    public static  final String[] professionSetNames = {
            "ARCHAEOLOGIST_",
            "FISHERMAN_",
            "BOTANIST_",
            "LUMBERJACK_",
            "MINER_"};

    public static int[] professionExpIndexes = new int[professionNames.length];
    public static float[] professionTotalExpValues = new float[professionNames.length];
    public static float[] professionTotalAverageValues = new float[professionNames.length];

    public static float[] professionSampleTotalExpValues = new float[professionNames.length];
    public static float[] professionSampleAverages = new float[professionNames.length];
    public static float[][] professionLastExpValues = new float[professionNames.length][DEFAULT_MAX_SAMPLE_VALUE];

    public static float[] professionLevelValues = new float[professionNames.length];


    public static final String[] nbtKeyNames = {
            AttributeTags.ADD_EXP_ARCHAEOLOGY.tag(),
            AttributeTags.ADD_EXP_FISHING.tag(),
            AttributeTags.ADD_EXP_HERBALISM.tag(),
            AttributeTags.ADD_EXP_LOGGING.tag(),
            AttributeTags.ADD_EXP_MINING.tag(),
            AttributeTags.ADD_EXP_RUNECARVING.tag(),
            AttributeTags.ADD_EXP.tag()
    };

    public static float[] equipmentBonusExpValues = new float[professionNames.length];




    public static int coins = 0;

    public static float baseClassExp = 1f;

    public static int getHudBackgroundBorderSize() {
        return DEFAULT_TEXT_HEIGHT / 2;
    }
}
