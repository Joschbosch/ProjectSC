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

public class Label extends BasicGUIElement {

    protected String text = "";

    private float fontSize = 1.0f;

    private FontType font;

    private Vector2f position = new Vector2f(0, 0);

    private float maxLineLength = 1.0f;

    private boolean centered = false;

    private Vector3f color = new Vector3f(0, 0, 0);

    private GUIText guiText;

    private float borderWidth = 0.0f;

    private float borderEdge = 0.5f;

    private Vector3f outlineColor = new Vector3f(0.0f, 0.0f, 0.0f);

    private Vector2f shadowOffset = new Vector2f(0.0f, 0.0f);

    private int renderOrder = 0;

    private boolean visible = true;

    public Label(Container c, Vector2f position) {
        c.add(this);
        this.font = FontStore.getFont(Font.CANDARA);
        this.position = position;
        createNewText();
    }

    private void createNewText() {
        if (guiText != null) {
            TextMaster.removeText(guiText);
        }
        guiText = new GUIText(text, fontSize, font, position, maxLineLength, centered);
        guiText.setColor(color.x, color.y, color.z);
        guiText.setBorderEdge(borderEdge);
        guiText.setBorderWidth(borderWidth);
        guiText.setOutlineColor(outlineColor.x, outlineColor.y, outlineColor.z);
        guiText.setRenderOrder(renderOrder);
        guiText.setShadowOffset(shadowOffset);
        TextMaster.loadText(guiText);
        this.positionAndSize = new Vector4f(position.x, position.y, guiText.getSize().x, guiText.getSize().y);
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

    public void setMaxLineLength(float length) {
        this.maxLineLength = length;
        createNewText();
    }

    public void setRenderOrder(int orderNumber) {
        this.renderOrder = orderNumber;
        guiText.setRenderOrder(orderNumber);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean value) {
        this.visible = value;
    }

    public int getRenderOrder() {
        return renderOrder;
    }

    public String getText() {
        return text;
    }

}
