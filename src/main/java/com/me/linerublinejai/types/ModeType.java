package com.me.linerublinejai.types;

import com.me.linerublinejai.utils.Mode;

public enum ModeType {

    // Text Mode
    THIS_DAY(Mode.BY_THIS_DAY, "วันนี้"),
    THIS_MONTH(Mode.BY_THIS_MONTH, "เดือนนี้"),
    SELECT_MODE(Mode.SELECT_MODE, "กรุณาเลือกรูปแบบ"),
    HELP(Mode.HELP, "ช่วยเหลือ"),

    // Postback Mode
    SELECT_DATE(Mode.BY_DATE, "กรุณาเลือกวัน"),
    SELECT_MONTH(Mode.BY_MONTH, "กรุณาเลือกเดือน");

    private String mode;
    private String label;

    ModeType(String mode, String label) {
        this.mode = mode;
        this.label = label;
    }

    public String getMode() {
        return this.mode;
    }

    public String getLabel() {
        return label;
    }
}
