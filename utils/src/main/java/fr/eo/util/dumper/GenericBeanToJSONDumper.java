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

package fr.eo.util.dumper;

import fr.eo.util.dumper.bean.DumpableBean;
import fr.eo.util.dumper.bean.JsonTableDump;
import fr.eo.util.dumper.bean.JsonTableWritable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

import static fr.eo.util.dumper.util.DumperUtil.getWriter;

/**
 * @author picon.software
 */
public final class GenericBeanToJSONDumper {

	private GenericBeanToJSONDumper() {
	}

	public static JsonTableDump dumpBeans(Set<? extends DumpableBean> beans, String mAssetFolder) {

		if (beans == null || beans.isEmpty()) {
			return null;
		}

		String dumpName = beans.iterator().next().getDumpName();
		BufferedWriter bw = null;

		JsonTableWritable dump = toTableDump(beans);

		try {
			System.out.println("Dumping " + dumpName + "...");
			bw = getWriter(dumpName, mAssetFolder);

			bw.append(dump.toJson());
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("done.");

		return dump;
	}

	public static JsonTableWritable toTableDump(Set<? extends DumpableBean> beans) {
		JsonTableWritable dump = new JsonTableWritable();

		dump.name = beans.iterator().next().getDumpName();
		for (DumpableBean bean : beans) {
			dump.addColumns(bean.columns()).commit();
		}

		return dump;
	}
}
