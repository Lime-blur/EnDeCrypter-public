package ru.limedev.endecrypter.core;

public final class Constants {
    public static final int PICKFILE_RESULT_CODE = 0;
    public static final String INTENT_TYPE = "*/*";
    public static final String TXT_EXT = ".txt";
    public static final String ME = "Me";

    public static final String XOR = "XOR";
    public static final String BASE64 = "BASE64";
    public static final String AES = "AES";
    public static final String REVERSE = "REVERSE";

    public static final boolean DEFAULT_MAIN_TUTORIAL = false;
    public static final boolean DEFAULT_ALGORITHM_TUTORIAL = false;
    public static final boolean DEFAULT_WALL_TUTORIAL = false;
    public static final boolean DEFAULT_MY_DIALOG_TUTORIAL = false;
    public static final int DEFAULT_FRIENDS_COUNT = 1000;
    public static final int DEFAULT_POST_COUNT = 100;
    public static final int DEFAULT_DIALOGS_COUNT = 20;
    public static final int DEFAULT_DIALOG_MESSAGES_COUNT = 50;
    public static final boolean DEFAULT_SWITCH_TURBO_VERSION = false;
    public static final String DEFAULT_FILE_SAFE = "code.txt";
    public static final String DEFAULT_ALGORITHM_SAFE = "{\"0_XOR\":\"defaultKey\",\"1_AES\":\"defaultKey\",\"2_BASE64\":\"NONE\",\"3_REVERSE\":\"NONE\"}";
    public static final String NONE_KEY_TEXT = "NONE";

    public static final String EN_DE_CRYPTER = "EnDeCrypter";
    public static final String ERROR_CRYPT = "Error encryption algorithm.";
    public static final String ERROR_ENCRYPT = "Error encrypt!";
    public static final String ERROR_DECRYPT = "Error decrypt!";

    public static final String COLOR_WHITE = "#FFFFFF";
    public static final String VALID_WORD_REGEX = "[a-zA-Z0-9]*";
    public static final String VALID_FILENAME_REGEX = "[a-zA-Z0-9,.;:_'\\\\s-]*";
    public static final String BASE64_REGEX = "[a-zA-Z0-9+=/]*";
    public static final String SPLITTER = "\\|";
    public static final String UNDERSCORE = "_";
    public static final String ELLIPSIS = "...";
    public static final String DATE_FORMAT = "dd.MM HH:mm";

    public static final String TEXT_FROM_WALL = "TextFromWall";
    public static final String TEXT_TO_WALL = "TextToWall";
    public static final String HISTORY_FOR_USER = "HistoryLastMessageUser";

    public static final int MARGIN_SIDES = 10;
    public static final int MARGIN_TOP_BOTTOM = 20;
    public static final int MAX_LAST_MESSAGE_LENGTH = 50;
    public static final int MAX_DIALOG_ENCRYPT_LENGTH = 2048;
    public static final int MAX_CRYPT_STEPS = 6;

    public static final int MAX_CALLS_BEFORE_UPDATE = 10;
    public static final int VK_RUNNABLE_DELAY = 800;
    public static final int VK_CALL_DELAY = 2500;
    public static final int VK_CALL_DELAY_TURBO = 500;

    public static final String FILE_SAVED = "Файл сохранён: ";
    public static final String UNWRITABLE_STORAGE = "Нельзя сделать запись в текущую дерикторию!";
    public static final String READ_CODE_ERROR = "Ошибка распознавания исходного кода алгоритма!";
    public static final String CAN_NOT_WRITE_VALUES = "Невозможно записать значения: некорректные символы в значениях!";
    public static final String SETTINGS_SAVED = "Настройки сохранены!";
    public static final String EMPTY_TEXT_FIELD = "Текстовое поле должно быть заполнено!";
    public static final String ERROR_DIALOG_ENCRYPT_LENGTH = "Шифрование в диалоге не поддерживает" +
            " более чем " + MAX_DIALOG_ENCRYPT_LENGTH + " символов.";
    public static final String ERROR_MESSAGE_LENGTH = "Длина сообщения не должна превышать 4096 символов.";
    public static final String ERROR_STEPS = "Количество шагов не должно превышать " + MAX_CRYPT_STEPS + ".";

    public static final String WELCOME = "Добро пожаловать!";
    public static final String FIRST_ENTER_TEXT = "\nПервым делом создайте свой уникальный алгоритм шифрования, либо " +
            "воспользуйтесь алгоритмом по умолчанию, который уже активирован.";
    public static final String CREATE_ENCRYPT_ALGORITHM_TEXT = "Создание алгоритма шифрования";
    public static final String KEY_INFO_TEXT = "\nЗаполните поле с ключом, выберите вид шифрования из списка и добавьте " +
            "шаг алгоритма. Создавая новые шаги, вы создаёте свою уникальную последовательность шифрования!";
    public static final String WALL_ENCRYPT_TEXT = "Дешифрование стены";
    public static final String WALL_ENCRYPT_INFO_TEXT = "\nНайдите в списке нужного вам друга и нажмите на " +
            "него, чтобы увидеть записи на его стене. Текст любой записи на стене друга можно " +
            "зашифровать и расшифровать!";
    public static final String MESSAGES_ENCRYPT_TEXT = "Шифрование сообщений";
    public static final String MESSAGES_ENCRYPT_INFO_TEXT = "\nВведите ваше сообщение и нажмите эту кнопку, " +
            "чтобы зашифровать сообщение. Нажав на зашифрованное сообщение в списке можно расшифровать его. " +
            "Помните: для того, чтобы ваш собеседник понял сообщение, с ним нужно поделиться исходным кодом " +
            "вашего алгоритма!";

    public static final String CLOSE = "Закрыть";
    public static final String GO = "Приступим!";
    public static final String DECRYPT = "Расшифровать";
    public static final String SHARE_ALGORITHM_TITLE = "Поделиться алгоритмом";
    public static final String ATTACHMENT = "Вложение (скрыто)";

    public static final String QUERY_SEND = "Запрос передан успешно!";
    public static final String SUCCESSFULLY_LOGIN = "Авторизация VK прошла успешно!";
    public static final String UNSUCCESSFULLY_LOGIN = "Авторизация VK не выполнена!";
}
