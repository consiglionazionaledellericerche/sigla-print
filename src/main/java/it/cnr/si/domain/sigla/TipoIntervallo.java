/*
 * Copyright (C) 2020  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.si.domain.sigla;

import java.util.*;

public enum TipoIntervallo {
    G("GIORNI"),

    S("SETTIMANE"),

    M("MESI");

    public static final List<TipoIntervallo> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    public static final List<String> ALL_IDS;
    private static final Map<String, TipoIntervallo> all = new HashMap<String, TipoIntervallo>();

    static {
        List<String> ids = new ArrayList<String>();
        for (TipoIntervallo o : values()) {
            String id = o.getId();
            all.put(id, o);
            ids.add(id);
        }
        ALL_IDS = Collections.unmodifiableList(ids);
    }

    private final String value;

    TipoIntervallo(String value) {
        this.value = value;
    }

    public static TipoIntervallo get(String value) {
        TipoIntervallo o = all.get(value);
        if (o == null) {
            throw new IllegalArgumentException(value);
        }
        return o;
    }

    public String getId() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
