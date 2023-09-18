package com.dlsc.gemsfx.skins;

import com.dlsc.gemsfx.YearView;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

public class YearViewSkin extends SkinBase<YearView> {

    private static final int ROWS = 5;
    private static final int COLUMNS = 4;

    private final Label yearRangeLabel;
    private final HBox header;
    private final GridPane gridPane;

    private int offset = 0;

    public YearViewSkin(YearView yearView) {
        super(yearView);

        yearRangeLabel = new Label();
        yearRangeLabel.getStyleClass().add("year-range-label");
        yearRangeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(yearRangeLabel, Priority.ALWAYS);

        Region leftArrow = new Region();
        leftArrow.getStyleClass().addAll("arrow", "left-arrow");

        Region rightArrow = new Region();
        rightArrow.getStyleClass().addAll("arrow", "right-arrow");

        StackPane leftArrowButton = new StackPane(leftArrow);
        leftArrowButton.getStyleClass().addAll("arrow-button", "left-button");
        leftArrowButton.setOnMouseClicked(evt -> {
            offset--;
            buildGrid();
        });

        StackPane rightArrowButton = new StackPane(rightArrow);
        rightArrowButton.getStyleClass().addAll("arrow-button", "right-button");
        rightArrowButton.setOnMouseClicked(evt -> {
            offset++;
            buildGrid();
        });

        header = new HBox(leftArrowButton, yearRangeLabel, rightArrowButton);
        header.getStyleClass().add("header");
        header.setViewOrder(Double.NEGATIVE_INFINITY);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);

        gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-pane");
        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4);

        getChildren().addAll(header, gridPane);

        yearView.valueProperty().addListener(obs -> buildGrid());
        buildGrid();
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        double headerHeight = snapSizeY(header.prefHeight(-1));
        header.resizeRelocate(contentX, contentY, contentWidth, headerHeight);
        gridPane.resizeRelocate(contentX, contentY + headerHeight, contentWidth, contentHeight - headerHeight);
    }

    private void buildGrid() {
        final int visibleYears = ROWS * COLUMNS;

        Year selectedYear = getSkinnable().getValue();
        int currentYear = LocalDate.now().getYear();
        int firstYear = ((Optional.ofNullable(selectedYear).map(Year::getValue).orElse(currentYear) / visibleYears) * visibleYears) + (offset * visibleYears);

        gridPane.getChildren().clear();
        yearRangeLabel.setText(firstYear + "-" + (firstYear + visibleYears - 1));

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                final int finalYear = firstYear;

                Label yearLabel = new Label();
                yearLabel.getStyleClass().add("year");
                yearLabel.setText(String.valueOf(firstYear));
                yearLabel.setOnMouseClicked(evt -> {
                    offset = 0;
                    getSkinnable().setValue(Year.of(finalYear));
                });

                if (selectedYear != null && firstYear == selectedYear.getValue()) {
                    yearLabel.getStyleClass().add("selected");
                }

                if (firstYear == currentYear) {
                    yearLabel.getStyleClass().add("current");
                }

                gridPane.add(yearLabel, column, row);
                firstYear++;
            }
        }

    }

}