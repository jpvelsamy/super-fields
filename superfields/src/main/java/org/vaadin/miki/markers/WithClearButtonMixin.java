package org.vaadin.miki.markers;

/**
 * Mixin interface for {@link HasClearButton}.
 * @param <SELF> Self type.
 * @author miki
 * @since 2021-01-10
 */
public interface WithClearButtonMixin<SELF extends HasClearButton> extends HasClearButton {

    /**
     * Chains {@link #setClearButtonVisible(boolean)} and returns itself.
     * @param state New visibility state for the clear button.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default SELF withClearButtonVisible(boolean state) {
        this.setClearButtonVisible(state);
        return (SELF)this;
    }

}
