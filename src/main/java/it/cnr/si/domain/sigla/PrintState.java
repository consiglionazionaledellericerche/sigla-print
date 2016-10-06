package it.cnr.si.domain.sigla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PrintState {
	C("STATO_IN_CODA"),

	X("STATO_IN_ESECUZIONE"),

	E("STATO_IN_ERRORE"),

	S("STATO_ESEGUITA");

	public static final List<PrintState> ALL = Collections
			.unmodifiableList(Arrays.asList(values()));

	private static final Map<String, PrintState> all = new HashMap<String, PrintState>();

	public static final List<String> ALL_IDS;

	private final String value;

	private PrintState(String value) {
		this.value = value;
	}

	public String getId() {
		return value;
	}

	static {
		List<String> ids = new ArrayList<String>();
		for (PrintState o : values()) {
			String id = o.getId();
			all.put(id, o);
			ids.add(id);
		}
		ALL_IDS = Collections.unmodifiableList(ids);
	}

	public static PrintState get(String value) {
		PrintState o = all.get(value);
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
