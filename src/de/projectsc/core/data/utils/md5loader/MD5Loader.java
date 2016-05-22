/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.md5loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.utils.md5loader.anim.MD5AnimHeader;
import de.projectsc.core.data.utils.md5loader.anim.MD5AnimModel;
import de.projectsc.core.data.utils.md5loader.anim.MD5BaseFrame;
import de.projectsc.core.data.utils.md5loader.anim.MD5BoundInfo;
import de.projectsc.core.data.utils.md5loader.anim.MD5Frame;
import de.projectsc.core.data.utils.md5loader.anim.MD5Hierarchy;
import de.projectsc.core.data.utils.md5loader.mesh.MD5Joints;
import de.projectsc.core.data.utils.md5loader.mesh.MD5Mesh;
import de.projectsc.core.data.utils.md5loader.mesh.MD5ModelHeader;

public class MD5Loader {

    private static final Log LOGGER = LogFactory.getLog(MD5Loader.class);

    public static void main(String[] args) {
        // loadMD5MeshFile("bob1.md5mesh");
        loadMD5AnimFile("bob1.md5anim");
    }

    public static MD5AnimModel loadMD5AnimFile(String filename) {
        String fullPath = "/models/animated/" + filename;
        MD5AnimModel model = loadAnimFile(fullPath);
        return model;
    }

    private static MD5AnimModel loadAnimFile(String fullPath) {
        InputStream animFile = MD5Loader.class.getResourceAsStream(fullPath);
        if (animFile != null) {
            try {
                List<String> content = IOUtils.readLines(animFile);
                if (content != null && !content.isEmpty()) {
                    MD5AnimModel newModel = new MD5AnimModel();
                    List<MD5Frame> frames = new LinkedList<>();
                    String currentFrame = "";
                    MD5Hierarchy hierarchy = null;
                    MD5BaseFrame baseframe = null;
                    MD5BoundInfo bounds = null;

                    boolean readingHeader = true;
                    boolean readingFrames = false;
                    boolean readingHierarchy = false;
                    boolean readingBounds = false;
                    boolean readingBaseFrame = false;

                    List<String> headerContent = new ArrayList<>();
                    List<String> framesContent = new ArrayList<>();
                    List<String> hierarchyContent = new ArrayList<>();
                    List<String> boundsContent = new ArrayList<>();
                    List<String> baseFrameContent = new ArrayList<>();

                    for (String line : content) {
                        if (line.startsWith("//")) {
                            continue;
                        } else if (line.matches("\\s*(baseframe(\\s)*\\{){1}\\s*")) {
                            if (readingFrames || readingHierarchy || readingBounds || readingBaseFrame) {
                                throw new IOException("Corrupt mesh file. Nested baseframe.");
                            } else {
                                readingHeader = false;
                                readingBaseFrame = true;
                            }
                        } else if (line.matches("\\s*(bounds(\\s)*\\{){1}\\s*")) {
                            if (readingFrames || readingHierarchy || readingBounds || readingBaseFrame) {
                                throw new IOException("Corrupt mesh file. Nested bounds.");
                            } else {
                                readingHeader = false;
                                readingBounds = true;
                            }
                        } else if (line.matches("\\s*(hierarchy(\\s)*\\{){1}\\s*")) {
                            if (readingFrames || readingHierarchy || readingBounds || readingBaseFrame) {
                                throw new IOException("Corrupt mesh file. Nested hierarchie.");
                            } else {
                                readingHeader = false;
                                readingHierarchy = true;
                            }
                        } else if (line.matches("\\s*(frame)(\\s)*(\\d+)(\\s)*(\\{){1}\\s*")) {
                            if (readingFrames || readingHierarchy || readingBounds || readingBaseFrame) {
                                throw new IOException("Corrupt mesh file. Nested frames.");
                            } else {
                                readingHeader = false;
                                readingFrames = true;
                                Matcher frameMatcher = Pattern.compile("\\s*(frame)(\\s)*(\\d+)(\\s)*(\\{){1}\\s*").matcher(line);
                                frameMatcher.matches();
                                currentFrame = frameMatcher.group(3);
                            }
                        } else if (line.matches("\\s*(\\}){1}\\s*")) {
                            if (readingHeader) {
                                throw new IOException("Corrupt mesh file. End block while reading header.");
                            } else if (readingFrames) {
                                readingHeader = true;
                                readingFrames = false;
                                frames.add(MD5Frame.parse(currentFrame, framesContent));
                                framesContent.clear();
                                currentFrame = "";
                            } else if (readingHierarchy) {
                                readingHeader = true;
                                readingHierarchy = false;
                                hierarchy = MD5Hierarchy.parse(hierarchyContent);
                                hierarchyContent.clear();
                            } else if (readingBaseFrame) {
                                readingHeader = true;
                                readingBaseFrame = false;
                                baseframe = MD5BaseFrame.parse(baseFrameContent);
                                baseFrameContent.clear();
                            } else if (readingBounds) {
                                readingHeader = true;
                                readingBounds = false;
                                bounds = MD5BoundInfo.parse(boundsContent);
                                baseFrameContent.clear();
                            }
                        } else {
                            if (readingHeader) {
                                headerContent.add(line);
                            } else if (readingBaseFrame) {
                                baseFrameContent.add(line);
                            } else if (readingBounds) {
                                boundsContent.add(line);
                            } else if (readingHierarchy) {
                                hierarchyContent.add(line);
                            } else if (readingFrames) {
                                framesContent.add(line);
                            } else {
                                throw new IOException("Corrupt mesh file. Reading nothing.");
                            }
                        }
                    }
                    if (!readingHeader) {
                        throw new IOException("Corrupt mesh file. File ended in block.");
                    }

                    MD5AnimHeader header = MD5AnimHeader.parse(headerContent);
                    newModel.setHeader(header);
                    newModel.setFrames(frames);
                    newModel.setBaseFrame(baseframe);
                    newModel.setBoundInfo(bounds);
                    newModel.setHierarchy(hierarchy);
                    return newModel;
                } else {
                    throw new IOException("Empty file");
                }
            } catch (IOException e) {
                LOGGER.error("Could not read mesh file: ", e);
            }
        } else {
            LOGGER.error("Could not read mesh file: " + fullPath);
        }
        return null;
    }

    public static MD5Model loadMD5MeshFile(String filename) {
        String fullPath = "/models/animated/" + filename;
        MD5Model model = loadMeshFile(fullPath);
        return model;
    }

    private static MD5Model loadMeshFile(String fullPath) {
        InputStream meshFile = MD5Loader.class.getResourceAsStream(fullPath);
        if (meshFile != null) {
            try {
                List<String> content = IOUtils.readLines(meshFile);
                if (content != null && !content.isEmpty()) {
                    MD5Model newModel = new MD5Model();
                    List<MD5Mesh> meshes = new LinkedList<>();
                    MD5Joints joints = null;
                    boolean readingHeader = true;
                    boolean readingJoints = false;
                    boolean readingMeshes = false;
                    List<String> headerContent = new ArrayList<>();
                    List<String> jointsContent = new ArrayList<>();
                    List<String> meshesContent = new ArrayList<>();
                    for (String line : content) {
                        if (line.startsWith("//")) {
                            continue;
                        } else if (line.matches("\\s*(mesh(\\s)*\\{){1}\\s*")) {
                            if (readingMeshes || readingJoints) {
                                throw new IOException("Corrupt mesh file. Nested meshes.");
                            } else {
                                readingHeader = false;
                                readingMeshes = true;
                            }
                        } else if (line.matches("\\s*(joints(\\s)*\\{){1}\\s*")) {
                            if (readingMeshes || readingJoints) {
                                throw new IOException("Corrupt mesh file. Nested joints.");
                            } else {
                                readingHeader = false;
                                readingJoints = true;
                            }
                        } else if (line.matches("\\s*(\\}){1}\\s*")) {
                            if (readingHeader) {
                                throw new IOException("Corrupt mesh file. End block while reading header.");
                            } else if (readingJoints) {
                                readingHeader = true;
                                readingJoints = false;
                                joints = MD5Joints.parse(jointsContent);
                                jointsContent.clear();
                            } else if (readingMeshes) {
                                readingHeader = true;
                                readingMeshes = false;
                                meshes.add(MD5Mesh.parse(meshesContent));
                                meshesContent.clear();
                            }
                        } else {
                            if (readingHeader) {
                                headerContent.add(line);
                            } else if (readingJoints) {
                                jointsContent.add(line);
                            } else if (readingMeshes) {
                                meshesContent.add(line);
                            } else {
                                throw new IOException("Corrupt mesh file. Reading nothing.");
                            }
                        }
                    }
                    if (!readingHeader) {
                        throw new IOException("Corrupt mesh file. File ended in block.");
                    }

                    MD5ModelHeader header = MD5ModelHeader.parse(headerContent);
                    newModel.setHeader(header);
                    newModel.setMeshes(meshes);
                    newModel.setJoints(joints);
                    return newModel;
                } else {
                    throw new IOException("Empty file");
                }
            } catch (IOException e) {
                LOGGER.error("Could not read mesh file: ", e);
            }
        } else {
            LOGGER.error("Could not read mesh file: " + fullPath);
        }
        return null;
    }

}
