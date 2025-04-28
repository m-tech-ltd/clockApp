package com.github.mtechltd;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import com.github.mtechltd.TimeEvent;

public class CustomClockApp extends JFrame {
    private final JLabel currentTimeLabel;
    private final JPanel timesPanel;
    private final List<TimeEvent> timeEvents;
    private final DateTimeFormatter timeFormatter;
    private final DateTimeFormatter dateFormatter;

    public CustomClockApp() {
        // Set up the window
        super("Custom Clock Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new BorderLayout());
        // Initialize formatters
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

        // Create components
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Current time display
        JPanel currentTimePanel = new JPanel(new BorderLayout());
        currentTimeLabel = new JLabel();
        currentTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        currentTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        currentTimePanel.add(currentTimeLabel, BorderLayout.CENTER);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        currentTimePanel.add(dateLabel, BorderLayout.SOUTH);

        // Update the date
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText(dateFormatter.format(now));

        // Times panel
        timesPanel = new JPanel();
        timesPanel.setLayout(new BoxLayout(timesPanel, BoxLayout.Y_AXIS));
        timesPanel.setBorder(BorderFactory.createTitledBorder("Scheduled Times"));

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton addEventButton = new JButton("Add Time Event");
        addEventButton.addActionListener(e -> showAddEventDialog());
        controlPanel.add(addEventButton);

        // Add components to main panel
        mainPanel.add(currentTimePanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(timesPanel), BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Initialize time events
        timeEvents = new ArrayList<>();

        // Add some default time events
        addDefaultTimeEvents();

        // Start the clock updater
        startClockUpdater();
    }

    private void addDefaultTimeEvents() {
        // Add some default time events
        addTimeEvent("Wake Up Time", LocalTime.of(6, 0));
        addTimeEvent("Start Work", LocalTime.of(9, 0));
        addTimeEvent("Lunch Break", LocalTime.of(12, 0));
        addTimeEvent("End Work", LocalTime.of(17, 0));
        addTimeEvent("Dinner Time", LocalTime.of(19, 0));
        addTimeEvent("Bedtime", LocalTime.of(22, 0));
    }

    private void addTimeEvent(String name, LocalTime time) {
        timeEvents.add(new TimeEvent(name, time));
        updateTimesPanel();
    }

    private void updateTimesPanel() {
        timesPanel.removeAll();

        // Sort events by time
        timeEvents.sort((e1, e2) -> e1.getTime().compareTo(e2.getTime()));

        LocalTime now = LocalTime.now();

        for (TimeEvent event : timeEvents) {
            JPanel eventPanel = new JPanel(new BorderLayout());
            eventPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            String timeString = event.getTime().format(timeFormatter);
            String timeFromNow = calculateTimeFromNow(event.getTime(), now);

            JLabel nameLabel = new JLabel(event.getName());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            JLabel timeLabel = new JLabel(timeString + " (" + timeFromNow + ")");

            // Highlight the next upcoming event
            if (event.getTime().isAfter(now) &&
                    (event.equals(timeEvents.get(0)) ||
                            timeEvents.get(timeEvents.indexOf(event) - 1).getTime().isBefore(now))) {
                eventPanel.setBackground(new Color(230, 255, 230));
                eventPanel.setOpaque(true);
                timeLabel.setForeground(new Color(0, 120, 0));
            }

            JButton removeButton = new JButton("×");
            removeButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            removeButton.setMargin(new Insets(0, 5, 0, 5));
            removeButton.addActionListener(e -> {
                timeEvents.remove(event);
                updateTimesPanel();
            });

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.add(timeLabel);
            rightPanel.add(removeButton);

            eventPanel.add(nameLabel, BorderLayout.WEST);
            eventPanel.add(rightPanel, BorderLayout.EAST);

            timesPanel.add(eventPanel);
        }

        timesPanel.revalidate();
        timesPanel.repaint();
    }

    private String calculateTimeFromNow(LocalTime eventTime, LocalTime now) {
        int eventMinutes = eventTime.getHour() * 60 + eventTime.getMinute();
        int nowMinutes = now.getHour() * 60 + now.getMinute();

        int diffMinutes = eventMinutes - nowMinutes;
        if (diffMinutes < 0) {
            diffMinutes += 24 * 60; // Add a day worth of minutes if the event is tomorrow
        }

        int hours = diffMinutes / 60;
        int minutes = diffMinutes % 60;

        if (hours == 0) {
            return minutes + " min" + (minutes != 1 ? "s" : "");
        } else if (minutes == 0) {
            return hours + " hour" + (hours != 1 ? "s" : "");
        } else {
            return hours + " hour" + (hours != 1 ? "s" : "") + ", " +
                    minutes + " min" + (minutes != 1 ? "s" : "");
        }
    }

    private void showAddEventDialog() {
        JDialog dialog = new JDialog(this, "Add Time Event", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Event Name:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Time (HH:MM):"));
        JTextField timeField = new JTextField();
        formPanel.add(timeField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Event name cannot be empty");
                }

                String[] timeParts = timeField.getText().trim().split(":");
                if (timeParts.length != 2) {
                    throw new IllegalArgumentException("Time must be in format HH:MM");
                }

                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    throw new IllegalArgumentException("Invalid time values");
                }

                addTimeEvent(name, LocalTime.of(hour, minute));
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void startClockUpdater() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Update clock every second
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                LocalDateTime now = LocalDateTime.now();
                currentTimeLabel.setText(now.format(timeFormatter));
                updateTimesPanel();
            });
        }, 0, 1, TimeUnit.SECONDS);
    }
}