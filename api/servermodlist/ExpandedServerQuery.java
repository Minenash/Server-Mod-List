package com.minenash.servermodlist;

import com.google.gson.JsonObject;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExpandedServerQuery {

    private static final Map<String, ExpandedServerQueryEntry> queries = new TreeMap<>();
    private static final Map<Integer, List<ExpandedServerQueryEntry>> queriesByPriority = new TreeMap<>();

    public static boolean register(String modid, ExpandedServerQueryRunner runner) {
        return register(modid, false, true, 100, runner);
    }
    public static boolean register(String modid, int priority, ExpandedServerQueryRunner runner) {
        return register(modid, false, true, priority, runner);
    }
    public static boolean register(String modid, boolean fullScope, ExpandedServerQueryRunner runner) {
        return register(modid, fullScope, true, 100, runner);
    }
    public static boolean register(String modid, boolean fullScope, int priority, ExpandedServerQueryRunner runner) {
        return register(modid, fullScope, true, priority, runner);
    }
    public static boolean register(String modid, boolean fullScope, boolean enable, ExpandedServerQueryRunner runner) {
        return register(modid, fullScope, enable, 100, runner);
    }
    public static boolean register(String modid, boolean fullScope, boolean enable, int priority, ExpandedServerQueryRunner runner) {
        if (queries.containsKey(modid)) return false;

        ExpandedServerQueryEntry entry = new ExpandedServerQueryEntry(modid, runner, enable, fullScope, priority);
        queries.put(modid, entry);
        addToQueriesByPriority(entry);
        return true;
    }

    public static boolean deregister(String modid) {
        ExpandedServerQueryEntry entry = queries.remove(modid);
        if (entry == null) return false;
        removeFromQueriesByPriority(entry);
        return true;
    }

    public static boolean enable(String modid) {
        if (!queries.containsKey(modid) || queries.get(modid).enabled) return false;
        queries.get(modid).enabled = true;
        addToQueriesByPriority(queries.get(modid));
        return true;

    }

    public static boolean disable(String modid) {
        if (!queries.containsKey(modid) || !queries.get(modid).enabled) return false;
        queries.get(modid).enabled = false;
        removeFromQueriesByPriority(queries.get(modid));
        return true;
    }

    private static void addToQueriesByPriority(ExpandedServerQueryEntry entry) {
        List<ExpandedServerQueryEntry> list = queriesByPriority.get(entry.priority);
        list.add(entry);
        queriesByPriority.put(entry.priority, list);
    }

    private static void removeFromQueriesByPriority(ExpandedServerQueryEntry entry) {
        List<ExpandedServerQueryEntry> list = queriesByPriority.get(entry.priority);
        list.remove(entry);
        queriesByPriority.put(entry.priority, list);
    }

    public static void execute(ClientConnection connection, ServerInfo info, JsonObject data) {
        for (List<ExpandedServerQueryEntry> entries : queriesByPriority.values()) {
            for (ExpandedServerQueryEntry entry : entries) {
                if (entry.fullScope)
                    entry.runner.run(info, data);
                else
                    entry.runner.run(info, data.getAsJsonObject(entry.modid));
            }
        }
    }


    private static class ExpandedServerQueryEntry {
        public String modid;
        public ExpandedServerQueryRunner runner;
        public boolean enabled;
        public boolean fullScope;
        public int priority;

        public ExpandedServerQueryEntry(String modid, ExpandedServerQueryRunner runner, boolean enabled, boolean fullScope, int priority) {
            this.modid = modid;
            this.runner = runner;
            this.enabled = enabled;
            this.fullScope = fullScope;
            this.priority = priority;
        }
    }

}
