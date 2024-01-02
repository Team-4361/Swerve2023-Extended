package frc.test;


import frc.robot.util.io.IOManager;
import frc.robot.util.preset.PresetGroup;
import frc.robot.util.preset.PresetMap;
import frc.robot.util.preset.PresetMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static frc.robot.util.preset.PresetMode.PARALLEL;
import static frc.robot.util.preset.PresetMode.SEQUENTIAL;

public class PresetTest {
    @Test
    @DisplayName("Tests the creation of individual Presets.")
    @Order(0)
    public void PresetMap_TestHandling() {
        PresetMap<Integer> map = new PresetMap<>("Fake Element");
        map.put("ONE", 1);
        map.put("TWO", 2);
        map.put("THREE", 3);

        final AtomicReference<String> listMapName = new AtomicReference<>("");
        final AtomicInteger listMapValue = new AtomicInteger(0);
        map.addListener((mapName, value) -> {
            listMapName.set(mapName);
            listMapValue.set(value);
        });

        Assertions.assertEquals(0, map.getSelectedIndex());
        Assertions.assertEquals(map.size()-1, map.getMaxIndex());
        Assertions.assertTrue(map.nextPreset(false));

        // Make sure the listeners responded.
        Assertions.assertEquals(1, map.getSelectedIndex());
        Assertions.assertEquals(2, listMapValue.get());
        Assertions.assertEquals("TWO", listMapName.get());
    }

    @Test
    @DisplayName("Test the creation of Sequential Groups")
    @Order(1)
    public void PresetMap_TestSequential() {
        PresetGroup group = new PresetGroup("Fake Sequential", SEQUENTIAL);
        for (int i=0; i<5; i++) {
            PresetMap<Integer> map = new PresetMap<>("Fake Element" + i);
            map.put("ONE", 1);
            map.put("TWO", 2);
            map.put("THREE", 3);
            map.setFinishedDelay(500);
            group.add(map);
        }

        Assertions.assertTrue(group.nextPreset(false));
        long startMs = System.currentTimeMillis();
        while (true) {
            if (group.isFinished()) {
                long endMs = System.currentTimeMillis();
                long duration = endMs - startMs;

                Assertions.assertTrue(duration >= 2000 && duration <= 3000);
                return;
            }
            IOManager.run();
        }
    }

    @Test
    @DisplayName("Test the creation and groups and handling.")
    @Order(1)
    public void PresetGroup_TestParallel() {
        // Start by creating 50 PresetGroups and verify their calls.
        PresetGroup mainGroup = new PresetGroup("MainGroup", PARALLEL);

        AtomicInteger hits = new AtomicInteger(0);

        final int SUB_GROUPS = 10;
        final int MAP_COUNT = 10;

        // Test the fancy recursion aspect of grouping.
        for (int i=0; i<SUB_GROUPS; i++) {
            PresetGroup subGroup = new PresetGroup("SubGroup" + i, PARALLEL);
            for (int j=0; j<MAP_COUNT; j++) {
                PresetMap<Integer> map = new PresetMap<>("PresetGroup" + j);
                map.put("ONE", 1);
                map.put("TWO", 2);
                map.put("THREE", 3);
                map.addListener(((mapName, value) -> hits.set(hits.get()+1)));
                subGroup.add(map);
            }
            mainGroup.add(subGroup);
        }

        Assertions.assertTrue(mainGroup.nextPreset(false));
        Assertions.assertEquals(SUB_GROUPS * MAP_COUNT, hits.get());
    }
}
