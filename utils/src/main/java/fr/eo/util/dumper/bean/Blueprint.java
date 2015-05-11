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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * @author picon.software
 */
public class Blueprint implements DumpableBean {

	public int id;
	public int maxProductionLimit;
	public Collection<BlueprintActivity> activities = new TreeSet<>();

	public void addActivity(BlueprintActivity activity) {
		activities.add(activity);
		activity.blueprintId = id;
	}

	@Override
	public String getDumpName() {
		return "blueprint";
	}

	@Override
	public String dump() {
		return "INSERT INTO blueprint VALUES (" +
				id + "," + maxProductionLimit + ",NULL,NULL,NULL,NULL,NULL);";
	}

	@Override
	public List<Object> columns() {
		return Arrays.asList((Object) id, maxProductionLimit, null, null, null, null, null, null);
	}
}
