package it.cnr.si.domain.sigla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TipoIntervallo {
	G("GIORNI"),

	S("SETTIMANE"),

	M("MESI");

	public static final List<TipoIntervallo> ALL = Collections
			.unmodifiableList(Arrays.asList(values()));

	private static final Map<String, TipoIntervallo> all = new HashMap<String, TipoIntervallo>();

	public static final List<String> ALL_IDS;

	private final String value;

	private TipoIntervallo(String value) {
		this.value = value;
	}

	public String getId() {
		return value;
	}

	static {
		List<String> ids = new ArrayList<String>();
		for (TipoIntervallo o : values()) {
			String id = o.getId();
			all.put(id, o);
			ids.add(id);
		}
		ALL_IDS = Collections.unmodifiableList(ids);
	}

	public static TipoIntervallo get(String value) {
		TipoIntervallo o = all.get(value);
		if (o == null) {
			throw new IllegalArgumentException(value);
		}
		return o;
	}

	@Override
	public String toString() {
		return value;
	}

}
