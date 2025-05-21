package org.todo.components;

import java.awt.Color;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.demo.charts.ExampleChart;

public class CPieChart implements ExampleChart<PieChart> {

    @Override
    public PieChart getChart() {

        PieChart chart = new PieChartBuilder().width(400).height(300).build();

        Color[] sliceColors = new Color[]{new Color(98, 103, 85), new Color(133, 95, 56)};
        chart.getStyler().setSeriesColors(sliceColors);
        chart.getStyler().setChartFontColor(Color.WHITE);

        chart.getStyler().setLegendBackgroundColor(new Color(30, 30, 30));
        chart.getStyler().setLegendBorderColor(new Color(52, 52, 52));
        chart.getStyler().setLegendFont(chart.getStyler().getLegendFont().deriveFont(14f));

        chart.getStyler().setPlotBackgroundColor(new Color(30, 30, 30));
        chart.getStyler().setPlotBorderColor(new Color(52, 52, 52));

        chart.getStyler().setChartBackgroundColor(new Color(30, 30, 30));
        chart.getStyler().setAntiAlias(true);

        chart.getStyler().setLabelsFont(chart.getStyler().getLabelsFont().deriveFont(16f));

        return chart;
    }

    @Override
    public String getExampleChartName() {
        return "";
    }

}
