package org.bitbucket.paidaki.pertcharts.gui;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField {

    public NumberTextField() {
        super();
    }

    public NumberTextField(String input) {
        super(input);
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text) {
        return ("".equals(text) || text.matches("[0-9]"));
    }
}