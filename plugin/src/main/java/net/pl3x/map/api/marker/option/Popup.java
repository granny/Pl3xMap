package net.pl3x.map.api.marker.option;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.JsonSerializable;
import net.pl3x.map.api.marker.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Popup properties of a marker.
 */
public class Popup implements JsonSerializable {
    private String content = "";
    private String pane;
    private Point offset;
    private Integer maxWidth;
    private Integer minWidth;
    private Integer maxHeight;
    private Boolean autoPan;
    private Point autoPanPaddingTopLeft;
    private Point autoPanPaddingBottomRight;
    private Point autoPanPadding;
    private Boolean keepInView;
    private Boolean closeButton;
    private Boolean autoClose;
    private Boolean closeOnEscapeKey;
    private Boolean closeOnClick;

    /**
     * Create a popup rule.
     *
     * @param content popup content
     */
    public Popup(@NotNull String content) {
        setContent(content);
    }

    /**
     * Get the string of this popup rule.
     *
     * @return popup content
     */
    @NotNull
    public String getContent() {
        return this.content;
    }

    /**
     * Set the string for this popup rule.
     * <p>
     * HTML is valid here.
     *
     * @param content popup content
     * @return this popup rule
     */
    @NotNull
    public Popup setContent(@NotNull String content) {
        Preconditions.checkNotNull(content, "Popup content is null");
        this.content = content;
        return this;
    }

    @Nullable
    public String getPane() {
        return this.pane;
    }

    @NotNull
    public Popup setPane(@Nullable String pane) {
        this.pane = pane;
        return this;
    }

    /**
     * Get offset of this popup rule.
     *
     * @return popup offset from marker point
     */
    @Nullable
    public Point getOffset() {
        return this.offset;
    }

    /**
     * Set offset of this popup rule from marker point
     *
     * @param offset popup offset
     * @return this popup rule
     */
    @NotNull
    public Popup setOffset(@Nullable Point offset) {
        this.offset = offset;
        return this;
    }

    @Nullable
    public Integer getMaxWidth() {
        return this.maxWidth;
    }

    @NotNull
    public Popup setMaxWidth(@Nullable Integer maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    @Nullable
    public Integer getMinWidth() {
        return this.minWidth;
    }

    @NotNull
    public Popup setMinWidth(@Nullable Integer minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    @Nullable
    public Integer getMaxHeight() {
        return this.maxHeight;
    }

    @NotNull
    public Popup setMaxHeight(@Nullable Integer maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    @Nullable
    public Boolean shouldAutoPan() {
        return this.autoPan;
    }

    @NotNull
    public Popup setShouldAutoPan(@Nullable Boolean autoPan) {
        this.autoPan = autoPan;
        return this;
    }

    @Nullable
    public Point getAutoPanPaddingTopLeft() {
        return this.autoPanPaddingTopLeft;
    }

    @NotNull
    public Popup setAutoPanPaddingTopLeft(@Nullable Point autoPanPaddingTopLeft) {
        this.autoPanPaddingTopLeft = autoPanPaddingTopLeft;
        return this;
    }

    @Nullable
    public Point getAutoPanPaddingBottomRight() {
        return this.autoPanPaddingBottomRight;
    }

    @NotNull
    public Popup setAutoPanPaddingBottomRight(@Nullable Point autoPanPaddingBottomRight) {
        this.autoPanPaddingBottomRight = autoPanPaddingBottomRight;
        return this;
    }

    @Nullable
    public Point getAutoPanPadding() {
        return this.autoPanPadding;
    }

    @NotNull
    public Popup setAutoPanPadding(@Nullable Point autoPanPadding) {
        this.autoPanPadding = autoPanPadding;
        return this;
    }

    @Nullable
    public Boolean shouldKeepInView() {
        return this.keepInView;
    }

    @NotNull
    public Popup setShouldKeepInView(@Nullable Boolean keepInView) {
        this.keepInView = keepInView;
        return this;
    }

    @Nullable
    public Boolean hasCloseButton() {
        return this.closeButton;
    }

    @NotNull
    public Popup setCloseButton(@Nullable Boolean closeButton) {
        this.closeButton = closeButton;
        return this;
    }

    @Nullable
    public Boolean shouldAutoClose() {
        return this.autoClose;
    }

    @NotNull
    public Popup setShouldAutoClose(@Nullable Boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    @Nullable
    public Boolean shouldCloseOnEscapeKey() {
        return this.closeOnEscapeKey;
    }

    @NotNull
    public Popup setShouldCloseOnEscapeKey(@Nullable Boolean closeOnEscapeKey) {
        this.closeOnEscapeKey = closeOnEscapeKey;
        return this;
    }

    @Nullable
    public Boolean shouldCloseOnClick() {
        return this.closeOnClick;
    }

    @NotNull
    public Popup setShouldCloseOnClick(@Nullable Boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(getContent());
        json.add(getPane());
        json.add(point(getOffset()));
        json.add(getMaxWidth());
        json.add(getMinWidth());
        json.add(getMaxHeight());
        json.add(bool(shouldAutoPan()));
        json.add(point(getAutoPanPaddingTopLeft()));
        json.add(point(getAutoPanPaddingBottomRight()));
        json.add(point(getAutoPanPadding()));
        json.add(bool(shouldKeepInView()));
        json.add(bool(hasCloseButton()));
        json.add(bool(shouldAutoClose()));
        json.add(bool(shouldCloseOnEscapeKey()));
        json.add(bool(shouldCloseOnClick()));
        return json;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Popup other = (Popup) o;
        return getContent().equals(other.getContent())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOffset(), other.getOffset())
                && Objects.equals(getMaxWidth(), other.getMaxWidth())
                && Objects.equals(getMinWidth(), other.getMinWidth())
                && Objects.equals(getMaxHeight(), other.getMaxHeight())
                && Objects.equals(shouldAutoPan(), other.shouldAutoPan())
                && Objects.equals(getAutoPanPaddingTopLeft(), other.getAutoPanPaddingTopLeft())
                && Objects.equals(getAutoPanPaddingBottomRight(), other.getAutoPanPaddingBottomRight())
                && Objects.equals(getAutoPanPadding(), other.getAutoPanPadding())
                && Objects.equals(shouldKeepInView(), other.shouldKeepInView())
                && Objects.equals(hasCloseButton(), other.hasCloseButton())
                && Objects.equals(shouldAutoClose(), other.shouldAutoClose())
                && Objects.equals(shouldCloseOnEscapeKey(), other.shouldCloseOnEscapeKey())
                && Objects.equals(shouldCloseOnClick(), other.shouldCloseOnClick());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getPane(), getOffset(), getMaxWidth(), getMinWidth(), getMaxHeight(),
                shouldAutoPan(), getAutoPanPaddingTopLeft(), getAutoPanPaddingBottomRight(), getAutoPanPadding(),
                shouldKeepInView(), hasCloseButton(), shouldAutoClose(), shouldCloseOnEscapeKey(), shouldCloseOnClick());
    }

    @Override
    public String toString() {
        return "Popup{"
                + ",content=" + getContent()
                + ",pane=" + getPane()
                + ",offset=" + getOffset()
                + ",maxWidth=" + getMaxWidth()
                + ",minWidth=" + getMinWidth()
                + ",maxHeight=" + getMaxHeight()
                + ",autoPan=" + shouldAutoPan()
                + ",autoPanPaddingTopLeft=" + getAutoPanPaddingTopLeft()
                + ",autoPanPaddingBottomRight=" + getAutoPanPaddingBottomRight()
                + ",autoPanPadding=" + getAutoPanPadding()
                + ",keepInView=" + shouldKeepInView()
                + ",closeButton=" + hasCloseButton()
                + ",autoClose=" + shouldAutoClose()
                + ",closeOnEscapeKey=" + shouldCloseOnEscapeKey()
                + ",closeOnClick=" + shouldCloseOnClick()
                + "}";
    }
}
