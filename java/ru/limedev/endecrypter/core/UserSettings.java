package ru.limedev.endecrypter.core;

public class UserSettings {
    public static final String appPreferences = "edc_settings";

    public static final String appFriendsCountName = "friends_count";
    public static final String appPostsCountName = "posts_count";
    public static final String appDialogsCountName = "dialogs_count";
    public static final String appMyDialogMessagesCountName = "my_dialog_messages_count";
    public static final String appSwitchTurboVersionName = "switch_turbo_version";
    public static final String saveFileName = "save_file";
    public static final String saveAlgorithmName = "save_algorithm";

    public static final String mainTutorialName = "main_tutorial";
    public static final String algorithmTutorialName = "algorithm_tutorial";
    public static final String wallTutorialName = "wall_tutorial";
    public static final String myDialogTutorialName = "my_dialog_tutorial";

    public static int appFriendsCount = 1000;
    public static int appPostsCount = 100;
    public static int appDialogsCount = 20;
    public static int appMyDialogMessagesCount = 50;
    public static boolean appSwitchTurboVersion = false;
    public static String saveFile = "code.txt";
    public static String saveAlgorithm = "{\"0_XOR\":\"defaultKey\",\"1_AES\":\"defaultKey\",\"2_BASE64\":\"NONE\",\"3_REVERSE\":\"NONE\"}";
}
