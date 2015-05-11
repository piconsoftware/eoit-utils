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
package fr.eo.util.dumper.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author picon.software
 *
 */
public class RequestDefinitionParser {

	private static final String PROP_FILE="requetes";
	private static final int MAX_REQS = 20;
	private static final int MAX_FIELDS = 30;

	private RequestDefinitionParser() {
	}

    public static String getAppBaseDir(String appName) {
        ResourceBundle bundle = ResourceBundle.getBundle(PROP_FILE + "-" + appName);

        return bundle.getString("global.app.base.dir");
    }

    public static List<RequestDefinitionBean> getRequests(String appName) {
		ResourceBundle bundle = ResourceBundle.getBundle(PROP_FILE + "-" + appName);

		List<RequestDefinitionBean> result = new ArrayList<>();
		for(int id = 1; id < MAX_REQS; id++) {
			RequestDefinitionBean request = new RequestDefinitionBean();
			request.id = id;
			try {
				if(bundle.getString("req" + id + ".name") != null) {
					request.name = bundle.getString("req" + id + ".name");
					request.disabled = Boolean.parseBoolean(bundle.getString("req" + id + ".disabled"));
					request.sql = bundle.getString("req" + id + ".sql");
					request.fields = new ArrayList<>();
					for(int i = 0; i < MAX_FIELDS; i++) {
						try {
							String fieldName = bundle.getString("req" + id + ".field" + i);
							if(fieldName != null) {
								request.fields.add(fieldName);
							}
						} catch (MissingResourceException ignored) { }
					}
					request.table = bundle.getString("req" + id + ".table");
					request.fieldStrPos = getIntegerListFromBundle(bundle, "req" + id + ".fieldStrPos");
					request.compressedStrPos = getIntegerListFromBundle(bundle, "req" + id + ".compressedStrPos");
                    request.numberPos = getIntegerListFromBundle(bundle, "req" + id + ".numberPos");

					result.add(request);
				}
			} catch (MissingResourceException ignored) { }
		}

		return result;
	}

	private static List<Integer> getIntegerList(String value) {
		List<Integer> list = new ArrayList<>();

		for(String intValue : value.split(",")) {
			list.add(Integer.parseInt(intValue));
		}

		return list;
	}

    private static List<Integer> getIntegerListFromBundle(ResourceBundle bundle, String key) {
        try {
            return getIntegerList(bundle.getString(key));
        } catch (MissingResourceException e) {
            return Collections.emptyList();
        }
    }
}
