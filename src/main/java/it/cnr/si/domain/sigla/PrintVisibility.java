package it.cnr.si.domain.sigla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PrintVisibility {
	P("PUBLICO"),

	U("UTENTE"),

	C("CDR"),

	O("UNITA_ORGANIZZATIVA"),

	S("CDS"),

	N("CNR");

	public static final List<PrintVisibility> ALL = Collections
			.unmodifiableList(Arrays.asList(values()));

	private static final Map<String, PrintVisibility> all = new HashMap<String, PrintVisibility>();

	public static final List<String> ALL_IDS;

	private final String value;

	private PrintVisibility(String value) {
		this.value = value;
	}

	public String getId() {
		return value;
	}

	static {
		List<String> ids = new ArrayList<String>();
		for (PrintVisibility o : values()) {
			String id = o.getId();
			all.put(id, o);
			ids.add(id);
		}
		ALL_IDS = Collections.unmodifiableList(ids);
	}

	public static PrintVisibility get(String value) {
		PrintVisibility o = all.get(value);
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
