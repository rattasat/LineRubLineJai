package com.me.linerublinejai.models;

import com.me.linerublinejai.types.ModeType;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Data
public class DatePeriod {

    private Date start;

    private Date finish;

    public DatePeriod(Date start, Date finish) {
        this.start = start;
        this.finish = finish;
    }

    public DatePeriod() {
    }

    public DatePeriod(Date now, ModeType mode) {
        switch (mode) {
            case THIS_DAY:
                this.start = startDate(now);
                this.finish = finishDate(now);
                break;
            case THIS_MONTH:
                this.start = minDateInMonth(now);
                this.finish = maxDateInMonth(now);
                break;
        }
    }


    private Date startDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private Date finishDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    private Date minDateInMonth(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));

        return startDate(calendar.getTime());
    }

    private Date maxDateInMonth(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

        return finishDate(calendar.getTime());
    }

}
