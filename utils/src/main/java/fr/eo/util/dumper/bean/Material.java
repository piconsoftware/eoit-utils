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

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.List;

/**
 * @author picon.software
 */
public class Material implements DumpableBean {

	public int blueprintId;
	public int activityId;

	public int itemId;
	public long quantity;
	public boolean consumed = true;

	@Override
	public String getDumpName() {
		return "blueprint_material";
	}

	@Override
	public String dump() {
		return "INSERT INTO blueprint_material VALUES (" +
				blueprintId + "," + activityId + "," + itemId +
				"," + quantity + "," + (consumed ? 1 : 0) + ");";
	}

	@Override
	public List<Object> columns() {
		return Arrays.asList((Object) blueprintId, activityId, itemId, quantity, consumed ? 1 : 0);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Material material = (Material) o;
		return Objects.equal(blueprintId, material.blueprintId) &&
				Objects.equal(activityId, material.activityId) &&
				Objects.equal(itemId, material.itemId) &&
				Objects.equal(quantity, material.quantity) &&
				Objects.equal(consumed, material.consumed);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(blueprintId, activityId, itemId, quantity, consumed);
	}
}
