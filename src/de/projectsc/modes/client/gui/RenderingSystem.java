/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.animation.Joint;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.BoundingVolumeType;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.core.data.physics.boundings.Sphere;
import de.projectsc.core.data.utils.gltf.GLTFLoader;
import de.projectsc.core.data.utils.md5loader.MD5Loader;
import de.projectsc.core.data.utils.md5loader.MD5Processor;
import de.projectsc.core.events.entity.movement.NotifyTransformUpdateEvent;
import de.projectsc.core.events.entity.objects.CreateLightEvent;
import de.projectsc.core.events.entity.objects.RemoveLightEvent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.core.systems.physics.collision.OctreeNode;
import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.MeshRendererComponent;
import de.projectsc.modes.client.gui.components.ParticleSystemComponent;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.events.UpdateMeshRendererParameterEvent;
import de.projectsc.modes.client.gui.events.UpdateTextureEvent;
import de.projectsc.modes.client.gui.models.AnimatedModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.objects.particles.ParticleSystem;

/**
 * System for rendering everything.
 * 
 * @author Josch Bosch
 */
public class RenderingSystem extends DefaultSystem {

    private static final String NAME = "Rendering System";

    private HashMap<TexturedModel, List<String>> myModels;

    public RenderingSystem(EntityManager entityManager, EventManager eventManager) {
        super(NAME, entityManager, eventManager);
        eventManager.registerForEvent(NotifyTransformUpdateEvent.class, this);
        eventManager.registerForEvent(UpdateMeshRendererParameterEvent.class, this);
        eventManager.registerForEvent(UpdateTextureEvent.class, this);
        eventManager.registerForEvent(CreateLightEvent.class, this);
        eventManager.registerForEvent(RemoveLightEvent.class, this);

        myModels = new HashMap<>();
        List<TexturedModel> gltfmodels = new GLTFLoader().loadGLTF("simple.gltf");
        for (TexturedModel m : gltfmodels) {
            List<String> newList = new LinkedList<>();
            newList.add("gltf");
            myModels.put(m, newList);
        }

        List<TexturedModel> newModels =
            MD5Processor.process(MD5Loader.loadMD5MeshFile("monster.md5mesh"), MD5Loader.loadMD5AnimFile("monster.md5anim"));
        for (TexturedModel m : newModels) {
            List<String> newList = new LinkedList<>();
            newList.add("md5");
            myModels.put(m, newList);
        }

    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof EntityEvent) {
            processEvent((EntityEvent) e);
        }
    }

    /**
     * Process an entity event.
     * 
     * @param e event to process
     */
    public void processEvent(EntityEvent e) {
        if (entityManager.hasComponent(e.getEntityId(), MeshRendererComponent.class)) {
            MeshRendererComponent c =
                ((MeshRendererComponent) entityManager.getComponent(e.getEntityId(), MeshRendererComponent.NAME));
            if (e instanceof UpdateMeshRendererParameterEvent) {
                UpdateMeshRendererParameterEvent ev = (UpdateMeshRendererParameterEvent) e;
                c.setFakeLighting(ev.isFakeLightning());
                c.setIsTransparent(ev.isTransparent());
                c.setNumberOfRows(ev.getNumColums());
                c.setReflectivity(ev.getReflectivity());
                c.setShineDamper(ev.getShineDamper());
            } else if (e instanceof UpdateTextureEvent) {
                UpdateTextureEvent event = (UpdateTextureEvent) e;
                if (event.getTextureFile() != null) {
                    try {
                        c.loadAndApplyTexture(event.getTextureFile().getCanonicalPath());
                    } catch (IOException e1) {
                        // LOGGER.error(e1.getStackTrace());
                    }
                }
            }
        }
        if (e instanceof CreateLightEvent && hasComponent(e.getEntityId(), EmittingLightComponent.class)) {
            CreateLightEvent createNewLightEvent = (CreateLightEvent) e;
            if (createNewLightEvent.getPosition() != null) {
                getComponent(e.getEntityId(), EmittingLightComponent.class).addLight(createNewLightEvent.getEntityId(),
                    createNewLightEvent.getPosition(), createNewLightEvent.getLight());
                createNewLightEvent.getLight().setEntity(e.getEntityId());
            } else {

                Transform pos = getComponent(e.getEntityId(), TransformComponent.class).getTransform();
                getComponent(e.getEntityId(), EmittingLightComponent.class).addLight(createNewLightEvent.getEntityId(),
                    pos.getPosition(), createNewLightEvent.getLight());
            }
        }
        if (e instanceof RemoveLightEvent && hasComponent(e.getEntityId(), EmittingLightComponent.class)) {
            getComponent(e.getEntityId(), EmittingLightComponent.class).removeLight(((RemoveLightEvent) e).getLight());
        }
    }

    @Override
    public void update(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(ParticleSystemComponent.class);
        for (String e : entities) {
            updateParticleSystem(tick, e);
        }
    }

    /**
     * Update system.
     * 
     * @param elapsed time
     * @param entity to update
     */
    public void updateParticleSystem(long elapsed, String entity) {
        ParticleSystemComponent psc = getComponent(entity, ParticleSystemComponent.class);
        TransformComponent tc = getComponent(entity, TransformComponent.class);
        if (psc.getParticleSystems().size() == 0) {
            psc.addNewParticleSystem();
        }
        for (ParticleSystem s : psc.getParticleSystems()) {
            if (psc.getOffsets().get(s.getId()) != null) {
                Vector3f.add(tc.getPosition(), psc.getOffsets().get(s.getId()), s.getSystemCenter());
                s.generateParticles(elapsed);
            }
        }
    }

    /**
     * Creates the scene that will be rendered afterwards.
     * 
     * @param octTree for debugging
     * 
     * @return scene to render
     */
    public GUIScene createScene(OctreeNode octTree) {
        Set<String> entities = entityManager.getAllEntites();
        GUIScene scene = new GUIScene();

        scene.getPositions().put("gltf", new Vector3f(0, 0, 0));
        scene.getRotations().put("gltf", new Vector3f(-90, 0, 0));
        scene.getScales().put("gltf", new Vector3f(1f, 1f, 1f));
        scene.getModels().putAll(myModels);

        ((AnimatedModel) myModels.get("gltf")).getAnimationController().update();

        for (String entity : entities) {
            Transform transform = getComponent(entity, TransformComponent.class).getTransform();
            scene.getPositions().put(entity, transform.getPosition());
            scene.getRotations().put(entity, transform.getRotation());
            scene.getScales().put(entity, transform.getScale());

            if (hasComponent(entity, MeshRendererComponent.class)) {
                addMeshesToScene(entity, scene);
            }
            if (hasComponent(entity, EmittingLightComponent.class)) {
                addLightsToScene(entity, transform, scene);
            }
            if (hasComponent(entity, ColliderComponent.class)) {
                ColliderComponent cc = getComponent(entity, ColliderComponent.class);
                TransformComponent tc = getComponent(entity, TransformComponent.class);
                WireFrame wf = null;
                if (cc.getSimpleBoundingVolume().getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
                    wf = new WireFrame(WireFrame.CUBE,
                        Vector3f.add(cc.getSimpleBoundingVolume().getPositionOffset(), tc.getPosition(), null),
                        new Vector3f(), ((Vector3f) Maths.getSize(cc.getSimpleBoundingVolume()).scale(tc.getScale().x)));
                } else if (cc.getSimpleBoundingVolume().getType() == BoundingVolumeType.SPHERE) {
                    float radius = ((Sphere) cc.getSimpleBoundingVolume()).getRadius();
                    wf =
                        new WireFrame(WireFrame.SPHERE,
                            Vector3f.add(Vector3f.add(((Sphere) cc.getSimpleBoundingVolume()).getPositionOffset(), tc.getPosition(), null),
                                tc.getPosition(), null),
                            new Vector3f(), new Vector3f(radius, radius, radius));
                }
                wf.setColor(new Vector3f(1.0f, 0, 0));
                scene.getWireFrames().add(wf);
            }
            if (hasComponent(entity, EntityStateComponent.class) && hasComponent(entity, ColliderComponent.class)) {
                EntityStateComponent esc = getComponent(entity, EntityStateComponent.class);
                ColliderComponent cc = getComponent(entity, ColliderComponent.class);
                // if (esc.isSelectAble() && esc.isSelected()) {
                // commented out for testing!
                scene.getSelectedEntites().add(new Vector3f(
                    transform.getPosition().x,
                    transform.getPosition().z,
                    Maths.getSize(cc.getSimpleBoundingVolume()).x));
                // }
                if (esc.isHighlightAble() && esc.isHighlighted()) {
                    scene.getHightlightedEntites().add(new Vector3f(
                        transform.getPosition().x,
                        transform.getPosition().z,
                        Maths.getSize(cc.getSimpleBoundingVolume()).x));
                }
            }
            if (hasComponent(entity, ParticleSystemComponent.class)) {
                for (ParticleSystem s : getComponent(entity, ParticleSystemComponent.class).getParticleSystems()) {
                    WireFrame w = new WireFrame(WireFrame.SPHERE, s.getSystemCenter(), new Vector3f(), new Vector3f(1, 1, 1));
                    scene.getWireFrames().add(w);
                }
            }

        }

        if (octTree != null) {
            Map<Vector3f, Vector3f> boxes = new HashMap<>();
            octTree.traverseTree(boxes, 0, false);
            for (Vector3f box : boxes.keySet()) {
                WireFrame w =
                    new WireFrame(WireFrame.CUBE, box, new Vector3f(), boxes.get(box));
                w.setColor(new Vector3f(0, 1, 0));
                scene.getWireFrames().add(w);
            }
        }

        List<Joint> joints = new LinkedList<>();
        Joint t = new Joint();
        Matrix4f mat = new Matrix4f();
        mat.m03 = 4.0f;
        mat.m13 = 4.0f;
        mat.m23 = 4.0f;
        t.setWorldMatrix(mat);
        joints.add(t);
        Joint t2 = new Joint();
        Matrix4f mat2 = new Matrix4f();
        mat2.m03 = 2.0f;
        mat2.m13 = 2.0f;
        mat2.m23 = 2.0f;
        t2.setWorldMatrix(mat2);
        joints.add(t2);
        t.addChild(t2);
        for (Joint j : joints) {
            float radius = 0.5f;
            WireFrame wf =
                new WireFrame(WireFrame.SPHERE, j.getWorldPosition(), new Vector3f(), new Vector3f(radius, radius, radius));
            wf.setColor(new Vector3f(0, 0, 1.0f));
            scene.getWireFrames().add(wf);
            for (Joint child : j.getChildren()) {
                WireFrame wf2 =
                    new WireFrame(WireFrame.LINE, j.getWorldPosition(), child.getWorldPosition());
                wf2.setColor(new Vector3f(0, 0, 1.0f));
                scene.getWireFrames().add(wf2);
            }
        }

        return scene;
    }

    private void addLightsToScene(String entity, Transform transform, GUIScene scene) {
        EmittingLightComponent elc = getComponent(entity, EmittingLightComponent.class);
        scene.getLights().addAll(elc.getLights());
        for (Light l : elc.getLights()) {
            WireFrame w =
                new WireFrame(WireFrame.SPHERE, Vector3f.add(transform.getPosition(), l.getPosition(), null), new Vector3f(),
                    new Vector3f(1, 1, 1));
            scene.getWireFrames().add(w);
        }
    }

    private void addMeshesToScene(String entity, GUIScene scene) {
        MeshRendererComponent mrc = getComponent(entity, MeshRendererComponent.class);
        if (!mrc.isInitialized()) {
            mrc.load();
        }
        TexturedModel m = mrc.getTexturedModel();
        if (m != null && m.getRawModel() != null && m.getTexture() != null) {
            if (mrc.getModelTexture() != null) {
                mrc.getModelTexture().setActiveTextureIndex(mrc.getTextureIndex());
            }
            List<String> batch = scene.getModels().get(m);
            if (batch != null) {
                batch.add(entity);
            } else {
                List<String> newBatch = new ArrayList<>();
                newBatch.add(entity);
                scene.getModels().put(m, newBatch);
            }
        }
    }
}
