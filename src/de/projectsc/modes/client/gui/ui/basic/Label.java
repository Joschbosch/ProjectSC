/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.objects.text.Font;
import de.projectsc.modes.client.gui.objects.text.FontStore;
import de.projectsc.modes.client.gui.objects.text.FontType;
import de.projectsc.modes.client.gui.objects.text.GUIText;
import de.projectsc.modes.client.gui.objects.text.TextMaster;

public class Label extends ContainerElement {

    protected String text = "";

    private float fontSize = 1.0f;

    private FontType font;

    private boolean centered = false;

    private Vector3f color = new Vector3f(0, 0, 0);

    private GUIText guiText;

    private float borderWidth = 0.0f;

    private float borderEdge = 0.5f;

    private Vector3f outlineColor = new Vector3f(0.0f, 0.0f, 0.0f);

    private Vector2f shadowOffset = new Vector2f(0.0f, 0.0f);

    public Label(Container c, Vector2f position) {
        super(c, position);
        this.font = FontStore.getFont(Font.CANDARA);
        c.add(this);
        createNewText();
    }

    private void createNewText() {
        if (guiText != null) {
            TextMaster.removeText(guiText);
        }
        guiText = new GUIText(text, fontSize, font, getPosition(), container.getPositionAndSize().z, centered);
        guiText.setColor(color.x, color.y, color.z);
        guiText.setBorderEdge(borderEdge);
        guiText.setBorderWidth(borderWidth);
        guiText.setOutlineColor(outlineColor.x, outlineColor.y, outlineColor.z);
        guiText.setRenderOrder(zOrder);
        guiText.setShadowOffset(shadowOffset);
        TextMaster.loadText(guiText);
        this.positionAndSize = new Vector4f(getPosition().x, getPosition().y, guiText.getSize().x, guiText.getSize().y);
    }

    @Override
    public void render(UI ui) {
        if (!visible) {
            TextMaster.removeText(guiText);
        } else if (!TextMaster.hasText(guiText)) {
            TextMaster.loadText(guiText);
        }
    }

    public void setText(String text) {
        if (this.text == null || !this.text.equals(text)) {
            this.text = text;
            createNewText();
        }
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        createNewText();
    }

    public void setTextColor(float r, float g, float b) {
        this.color = new Vector3f(r, g, b);
        createNewText();
    }

    public void setBorderWidth(float f) {
        this.borderWidth = f;
        createNewText();
    }

    public void setOutlineColor(float r, float g, float b) {
        this.outlineColor = new Vector3f(r, g, b);
        createNewText();
    }

    public void setCentered(boolean value) {
        this.centered = value;
        createNewText();
    }

    public String getText() {
        return text;
    }

}
