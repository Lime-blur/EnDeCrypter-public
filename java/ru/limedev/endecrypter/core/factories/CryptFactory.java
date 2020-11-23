package ru.limedev.endecrypter.core.factories;

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

import android.os.Build;

import androidx.annotation.RequiresApi;

import ru.limedev.endecrypter.core.impl.crypters.AES;
import ru.limedev.endecrypter.core.impl.crypters.CustomBase64;
import ru.limedev.endecrypter.core.impl.crypters.Reverse;
import ru.limedev.endecrypter.core.impl.crypters.XOR;
import ru.limedev.endecrypter.core.ifaces.CryptDAO;

public class CryptFactory {

    private static CryptDAO cryptDAO = null;

    private enum sources {
        XOR {
            @Override
            public CryptDAO getInstance() {
                return new XOR();
            }
        },
        AES {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public CryptDAO getInstance() {
                return new AES();
            }
        },
        BASE64 {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public CryptDAO getInstance() {
                return new CustomBase64();
            }
        },
        REVERSE {
            @Override
            public CryptDAO getInstance() {
                return new Reverse();
            }
        };

        public abstract CryptDAO getInstance();
    }

    public static void init(String cryptType) {
        cryptDAO = CryptFactory.sources.valueOf(cryptType.toUpperCase()).getInstance();
    }

    public static CryptDAO getClassFromFactory() {
        return cryptDAO;
    }
}
