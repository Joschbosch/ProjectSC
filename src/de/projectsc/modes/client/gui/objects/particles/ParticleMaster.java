/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.objects.particles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;

public class ParticleMaster {

    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();

    public static void update(long delta, Vector3f camPosition) {
        Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> it = list.iterator();
            while (it.hasNext()) {
                Particle p = it.next();
                if (!p.update(delta / 1000f, camPosition)) {
                    it.remove();
                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
            Collections.sort(list);
        }

    }

    public static Map<ParticleTexture, List<Particle>> render() {
        return particles;
    }

    public static void dispose() {}

    public static void addParticle(Particle p) {
        List<Particle> list = particles.get(p.getTexture());
        if (list == null) {
            list = new LinkedList<>();
            particles.put(p.getTexture(), list);
        }
        list.add(p);
    }

}
