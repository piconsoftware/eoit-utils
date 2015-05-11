/*
 * Copyright (C) 2014 Picon software
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package fr.eo.util.dumper.bean;

import java.util.*;

/**
 * @author picon.software
 */
public class TranslationDump {

	private static final int DE = 0;
	private static final int EN_US = 1;
	private static final int FR = 2;
	private static final int RU = 3;

    private static Map<String, Integer> languages = new HashMap<>();

    static {
        languages.put("DE", DE);
        languages.put("EN-US", EN_US);
        languages.put("FR", FR);
        languages.put("RU", RU);
    }

    private Map<String, Long> translations = new LinkedHashMap<>();
    private Set<TranslationKey> keys = new LinkedHashSet<>();

    public void add(int keyId, int tcId, String language, String text) {
		int languageId = languages.get(language);
        if(!translations.containsKey(text)) {
            long traId = Long.parseLong("" + keyId + tcId + languageId);
            keys.add(new TranslationKey(keyId, tcId, languageId, traId));
            translations.put(text, traId);
        } else {
            Long traId = translations.get(text);
            keys.add(new TranslationKey(keyId, tcId, languageId, traId));
        }
    }

    public Set<TranslationKey> getTranslationKey() {
        return keys;
    }

    public Set<Translation> getTranslations() {
        Set<Translation> result = new LinkedHashSet<>();
        for (Map.Entry<String, Long> traEntry : translations.entrySet()) {
            result.add(new Translation(traEntry.getValue(), traEntry.getKey()));
        }

        return result;
    }

    public static class Translation implements DumpableBean {

        public Long traId;
        public String text;

        public Translation(Long traId, String text) {
            this.traId = traId;
            this.text = text;
        }

        @Override
        public String getDumpName() {
            return "translation";
        }

        @Override
        public String dump() {
            return null;
        }

        @Override
        public List<Object> columns() {
            return Arrays.asList((Object) traId, text);
        }
    }

    public static class TranslationKey implements DumpableBean {
        public int keyId, tcId, languageId;
        public long traId;

        public TranslationKey(int keyId, int tcId, int languageId, long traId) {
            this.keyId = keyId;
            this.tcId = tcId;
            this.languageId = languageId;
            this.traId = traId;
        }

        @Override
        public String getDumpName() {
            return "translation_key";
        }

        @Override
        public String dump() {
            return null;
        }

        @Override
        public List<Object> columns() {
            return Arrays.asList((Object) keyId, tcId, languageId, traId);
        }
    }
}
