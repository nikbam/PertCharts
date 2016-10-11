package org.bitbucket.paidaki.pertcharts.application.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class XMLLocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v, Util.DATE_FORMATTER1);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return Util.DATE_FORMATTER1.format(v);
    }
}
