package com.dlsc.gemsfx;

import com.dlsc.gemsfx.skins.CalendarPickerSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Objects;

/**
 * A control for quickly selecting the month of a year. The format used for the
 * month depends on the {@link #converterProperty()}. The default converter produces
 * and expects the full month name, e.g. "January", "February", etc. An invalid text
 * resets the value of the picker to null.
 */
public class CalendarPicker extends ComboBoxBase<LocalDate> {

    private final TextField editor = new TextField();

    private final CalendarView calendarView = new CalendarView();

    /**
     * Constructs a new picker.
     */
    public CalendarPicker() {
        super();

        getStyleClass().setAll("calendar-picker", "text-input");

        calendarView.setShowToday(true);
        calendarView.setShowTodayButton(true);

        setFocusTraversable(false);

        valueProperty().addListener(it -> updateText());

        editor.promptTextProperty().bindBidirectional(promptTextProperty());
        editor.editableProperty().bind(editableProperty());
        editor.setOnAction(evt -> commitValue());
        editor.focusedProperty().addListener(it -> {
            if (!editor.isFocused()) {
                commitValue();
            }
            pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), editor.isFocused());
        });

        editor.addEventHandler(KeyEvent.ANY, evt -> {
            if (evt.getCode().equals(KeyCode.DOWN)) {
                setValue(getValue().plusMonths(1));
            } else if (evt.getCode().equals(KeyCode.UP)) {
                setValue(getValue().minusMonths(1));
            }
        });

        converterProperty().addListener((obs, oldConverter, newConverter) -> {
            if (newConverter == null) {
                setConverter(oldConverter);
            }
        });

        setMaxWidth(Region.USE_PREF_SIZE);
        updateText();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CalendarPickerSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(CalendarPicker.class.getResource("calendar-picker.css")).toExternalForm();
    }

    /**
     * Returns the view used to display dates when the picker is opening up.
     *
     * @return the calendar view for picking a date
     */
    public final CalendarView getCalendarView() {
        return calendarView;
    }

    /*
     * Performs the work of actually creating and setting a new month value.
     */
    private void commitValue() {
        String text = editor.getText();
        if (StringUtils.isNotBlank(text)) {
            StringConverter<LocalDate> converter = getConverter();
            if (converter != null) {
                LocalDate value = converter.fromString(text);
                if (value != null) {
                    setValue(value);
                } else {
                    setValue(null);
                }
            }
        }
    }

    /*
     * Updates the text of the text field based on the current value / month.
     */
    private void updateText() {
        LocalDate value = getValue();
        if (value != null && getConverter() != null) {
            editor.setText(getConverter().toString(value));
        } else {
            editor.setText("");
        }
        editor.positionCaret(editor.getText().length());
    }

    /**
     * Returns the text field control used for manual input.
     *
     * @return the editor / text field
     */
    public final TextField getEditor() {
        return editor;
    }

    private final ObjectProperty<StringConverter<LocalDate>> converter = new SimpleObjectProperty<>(this, "value", new StringConverter<>() {
        @Override
        public String toString(LocalDate object) {
            if (object != null) {
                return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(object);
            }
            return null;
        }

        @Override
        public LocalDate fromString(String string) {
            try {
                return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).parse(string, LocalDate::from);
            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    });

    public final StringConverter<LocalDate> getConverter() {
        return converter.get();
    }

    /**
     * A converter used to translate a text into a YearMonth object and vice
     * versa.
     *
     * @return the converter object
     */
    public final ObjectProperty<StringConverter<LocalDate>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<LocalDate> converter) {
        this.converter.set(converter);
    }
}
