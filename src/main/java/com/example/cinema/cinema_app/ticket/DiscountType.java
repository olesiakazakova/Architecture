package com.example.cinema.cinema_app.ticket;

public enum DiscountType {
    NO_DISCOUNT("нет скидки"),
    CHILD_DISCOUNT("для ребенка"),
    STUDENT_DISCOUNT("для студента"),
    SENIOR_DISCOUNT("для пенсионера");

    private final String label;

    DiscountType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static DiscountType fromLabel(String label) {
        if (label == null) {
            return NO_DISCOUNT;
        }

        String trimmedLabel = label.trim();
        for (DiscountType type : values()) {
            if (type.getLabel().equalsIgnoreCase(trimmedLabel)) {
                return type;
            }
        }
        return NO_DISCOUNT;
    }
}





