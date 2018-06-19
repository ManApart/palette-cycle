package rak.pixellwp.example.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rak.pixellwp.cycling.models.Palette;

//From Timeline class
public class ForEachExample {

    private Map<Integer, Palette> parseEntries(Map<String, String> entries, List<Palette> palettes){
        Map<Integer, Palette> newMap = new HashMap<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Palette value = findPaletteWithId(entry.getValue(), palettes);
            newMap.put(Integer.parseInt(entry.getKey()), value);
        }
        return newMap;
    }

    private Palette findPaletteWithId(String id, List<Palette> palettes){
        for (Palette it : palettes){
            if (it.getId().equals(id)){
                return it;
            }
        }
        return null;
    }
}
