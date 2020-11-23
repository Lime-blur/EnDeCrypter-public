package ru.limedev.endecrypter.core;

/*
 * MIT License
 *
 * Copyright (c) 2020 Tim Meleshko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class UserSettings {
    public static final String appPreferences = "edc_settings";

    public static final String appFriendsCountName = "friends_count";
    public static final String appPostsCountName = "posts_count";
    public static final String appDialogsCountName = "dialogs_count";
    public static final String appMyDialogMessagesCountName = "my_dialog_messages_count";
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
    public static String saveFile = "code.txt";
    public static String saveAlgorithm = "{\"0_XOR\":\"defaultKey\",\"1_AES\":\"defaultKey\",\"2_BASE64\":\"NONE\",\"3_REVERSE\":\"NONE\"}";
}
