package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.timepicker.TimePicker;
import org.vaadin.miki.markers.WithDatePatternMixin;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithHelperPositionableMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLocaleMixin;
import org.vaadin.miki.markers.WithValueMixin;
import org.vaadin.miki.shared.dates.DatePattern;
import org.vaadin.miki.util.ReflectTools;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

/**
 * An extension of {@link DateTimePicker} that handles I18N also on the client side.
 * @author miki
 * @since 2020-04-09
 */
@JsModule("./super-date-time-picker.js")
@Tag("super-date-time-picker")
@SuppressWarnings("squid:S110") // there is no way to reduce the number of parent classes
public class SuperDateTimePicker extends DateTimePicker
        implements HasSuperDatePickerI18N,
                   WithLocaleMixin<SuperDateTimePicker>, WithLabelMixin<SuperDateTimePicker>,
                   WithDatePatternMixin<SuperDateTimePicker>, WithIdMixin<SuperDateTimePicker>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<DateTimePicker, LocalDateTime>, LocalDateTime, SuperDateTimePicker>,
                   WithHelperMixin<SuperDateTimePicker>, WithHelperPositionableMixin<SuperDateTimePicker> {

    // so, this component is a composition of DatePicker and TimePicker
    // yet, these are private fields and it is impossible to access them :|
    private static final String INTERNAL_DATE_PICKER_FIELD_NAME = "datePicker";
    private static final String INTERNAL_TIME_PICKER_FIELD_NAME = "timePicker";

    private final DatePatternDelegate<SuperDateTimePicker> delegate = new DatePatternDelegate<>(this);

    private DatePattern datePattern;

    public SuperDateTimePicker() {
        this(Locale.getDefault());
    }

    public SuperDateTimePicker(Locale locale) {
        super();
        this.setLocale(locale);
    }

    public SuperDateTimePicker(String label) {
        super(label);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(String label, LocalDateTime initialDateTime) {
        super(label, initialDateTime);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(LocalDateTime initialDateTime) {
        super(initialDateTime);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(initialDateTime, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(String label, LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, initialDateTime, listener);
        this.setLocale(Locale.getDefault());
    }

    public SuperDateTimePicker(LocalDateTime initialDateTime, Locale locale) {
        super(initialDateTime);
        this.setLocale(locale);
    }

    @Override
    @SuppressWarnings("squid:S2589")
    public void setLocale(Locale locale) {
        // this method is called from the constructor of the superclass
        // which means that first time it gets called, the delegate is not yet initialised
        if(this.delegate != null) {
            this.delegate.initPatternSetting();
            SuperDatePickerI18nHelper.updateI18N(locale, this::getDatePickerI18n, this::setDatePickerI18n);
        }
        super.setLocale(locale);
    }

    /**
     * Exposes an internal {@link DatePicker}, if it was successfully obtained through reflection.
     * @return A {@link DatePicker} used by this component, if possible.
     */
    public Optional<DatePicker> getInternalDatePicker() {
        return ReflectTools.getValueOfField(this, DatePicker.class, INTERNAL_DATE_PICKER_FIELD_NAME);
    }

    /**
     * Exposes an internal {@link TimePicker}, if it was successfully obtained through reflection.
     * @return A {@link TimePicker} used by this component, if possible.
     */
    public Optional<TimePicker> getInternalTimePicker() {
        return ReflectTools.getValueOfField(this, TimePicker.class, INTERNAL_TIME_PICKER_FIELD_NAME);
    }

    @Override
    public void setDatePattern(DatePattern pattern) {
        this.datePattern = pattern;
        this.delegate.updateClientSidePattern();
    }

    @Override
    public DatePattern getDatePattern() {
        return this.datePattern;
    }

    @Override
    public SuperDatePickerI18n getSuperDatePickerI18n() {
        return (SuperDatePickerI18n) this.getDatePickerI18n();
    }

    @Override
    public void setDatePickerI18n(DatePicker.DatePickerI18n i18n) {
        if(!(i18n instanceof SuperDatePickerI18n))
            i18n = SuperDatePickerI18nHelper.from(i18n, this.getLocale());
        super.setDatePickerI18n(i18n);
    }
}
