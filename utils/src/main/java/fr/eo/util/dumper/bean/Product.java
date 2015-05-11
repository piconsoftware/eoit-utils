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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author picon.software
 */
public class Product implements DumpableBean {

	private static final NumberFormat nf = new DecimalFormat("0.#",
			new DecimalFormatSymbols(Locale.US));

	public int blueprintId;
	public int activityId;

	public int itemId;
	public float probability;
	public long quantity;

	@Override
	public String getDumpName() {
		return "blueprint_product";
	}

	@Override
	public String dump() {
		return "INSERT INTO blueprint_product VALUES (" +
				blueprintId + "," + activityId + "," +
				itemId + "," + nf.format(probability) + "," + quantity + ");";
	}

	@Override
	public List<Object> columns() {
		return Arrays.asList((Object) blueprintId, activityId, itemId, probability, quantity);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Product product = (Product) o;
		return Objects.equal(blueprintId, product.blueprintId) &&
				Objects.equal(activityId, product.activityId) &&
				Objects.equal(itemId, product.itemId) &&
				Objects.equal(probability, product.probability) &&
				Objects.equal(quantity, product.quantity);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(blueprintId, activityId, itemId, probability, quantity);
	}
}
