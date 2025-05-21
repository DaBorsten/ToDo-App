package org.todo.utils.GUI.General;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.XChartPanel;
import org.todo.classes.Task;
import org.todo.components.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class GUI_General_Chart {

    public static JPanel contentPanel = new JPanel();

    public static void displayPieChart(List<Task> tasks) {

        List<Task> doneTasks = new ArrayList<>();
        List<Task> todoTasks = new ArrayList<>();

        tasks.forEach(task -> {
            if (task.isCompleted()) {
                doneTasks.add(task);
            } else {
                todoTasks.add(task);
            }
        });

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        CPieChart pieChart = new CPieChart();
        PieChart chart = pieChart.getChart();
        chart.addSeries("Abgeschlossen (" + doneTasks.size() + ")", doneTasks.size());
        chart.addSeries("ToDo (" + todoTasks.size() + ")", todoTasks.size());
        JPanel chartPanel = new XChartPanel<>(chart);

        contentPanel.add(chartPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
