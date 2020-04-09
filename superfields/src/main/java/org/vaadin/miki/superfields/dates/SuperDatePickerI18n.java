package org.vaadin.miki.superfields.dates;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.markers.HasLocale;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A locale-powered {@link com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n}.
 * @author miki
 * @since 2020-04-09
 */
// this is a workaround for https://github.com/vaadin/vaadin-date-time-picker-flow/issues/26
final class SuperDatePickerI18n extends DatePicker.DatePickerI18n implements HasLocale {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperDatePickerI18n.class);

    private static final String RESOURCE_BUNDLE_NAME = SuperDatePickerI18n.class.getSimpleName().toLowerCase();

    private final Map<String, Function<String, DatePicker.DatePickerI18n>> keysToStringMethods = new HashMap<>();

    private final Map<String, Function<List<String>, DatePicker.DatePickerI18n>> keysToListStringMethods = new HashMap<>();

    private Locale locale;

    /**
     * Creates the i18n data based on default {@link Locale}.
     */
    public SuperDatePickerI18n() {
        this(Locale.getDefault());
    }

    /**
     * Creates the i18n data based on given {@link Locale}.
     * @param locale Locale to use.
     */
    public SuperDatePickerI18n(Locale locale) {
        this.keysToStringMethods.put("calendar", this::setCalendar);
        this.keysToStringMethods.put("cancel", this::setCancel);
        this.keysToStringMethods.put("clear", this::setClear);
        this.keysToStringMethods.put("today", this::setToday);
        this.keysToStringMethods.put("week", this::setWeek);
        this.keysToListStringMethods.put("month-names", this::setMonthNames);
        this.keysToListStringMethods.put("weekdays", this::setWeekdays);
        this.keysToListStringMethods.put("weekdays-short", this::setWeekdaysShort);
        // finally, set locale
        this.setLocale(locale);
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale == null ? Locale.getDefault() : locale;
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        this.setMonthNames(Arrays.asList(symbols.getMonths()));
        this.setFirstDayOfWeek(Calendar.getInstance(this.locale).getFirstDayOfWeek() == Calendar.MONDAY ? 1 : 0);
        this.setWeekdays(Arrays.stream(symbols.getWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        this.setWeekdaysShort(Arrays.stream(symbols.getShortWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));

        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, this.locale);
            final Set<String> bundleKeys = bundle.keySet();

            // filter out those required keys that are present
            this.keysToStringMethods.entrySet().stream()
                    .filter(entry -> bundleKeys.contains(entry.getKey()))
                    .forEach(entry -> entry.getValue().apply(bundle.getString(entry.getKey())));
            this.keysToListStringMethods.entrySet().stream()
                    .filter(entry -> bundleKeys.contains(entry.getKey()))
                    .forEach(entry -> entry.getValue().apply(Arrays.asList(bundle.getString(entry.getKey()).split("\\s*,\\s*"))));
            if(bundleKeys.contains("first-day-of-week"))
                this.setFirstDayOfWeek(Integer.parseInt(bundle.getString("first-day-of-week")));
        }
        catch(MissingResourceException mre) {
            LOGGER.warn("resource bundle {} for locale {} not found, some texts may display incorrectly or not at all", RESOURCE_BUNDLE_NAME, locale);
            // do nothing, no resource - no text to display
        }
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }
}