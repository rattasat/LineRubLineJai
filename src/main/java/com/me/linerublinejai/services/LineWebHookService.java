package com.me.linerublinejai.services;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.me.linerublinejai.models.entities.Expenditure;
import com.me.linerublinejai.models.entities.LineUser;
import com.me.linerublinejai.models.repositories.ExpenditureRepository;
import com.me.linerublinejai.models.repositories.LineUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LineWebHookService {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    private LineUserRepository lineUserRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    private SimpleDateFormat justTime = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat justDate = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateShow = new SimpleDateFormat("EEE, dd/MMM/yyyy");

    public void handleTextContent(MessageEvent<TextMessageContent> event) throws Exception {
        String replyToken = event.getReplyToken();
        String text = event.getMessage().getText();
        String lineUserId = event.getSource().getUserId();
        if (text.equalsIgnoreCase("-help")) {

        } else if (text.matches("[0-9A-Za-zก-๙]+ /\\d+")) {
            String activity = text.split(" /")[0];
            Integer price =  Integer.parseInt(text.split(" /")[1]);
            Expenditure expenditure = new Expenditure(lineUserId, activity, new Date(), price);
            expenditureRepository.save(expenditure);
            List<Message> messages = new ArrayList<>();
            messages.add(new TextMessage("เพิ่มรายการนี้ไปยังรายจ่ายวันนี้ของคุณเรียบร้อยแล้ว"));
            replyMessages(replyToken, messages);
        } else if (text.equalsIgnoreCase("รายจ่ายวันนี้")) {
            Date start = startDate(new Date());
            Date finish = finishDate(new Date());
            List<Expenditure> expenditureList = expenditureRepository.findByLineUserIdAndDateBetween(lineUserId, start, finish);
            manageMessageDay(replyToken, expenditureList);
        } else if (text.equalsIgnoreCase("รายจ่ายเดือนนี้")) {
            Date start = minDateInMonth(new Date());
            Date finish = maxDateInMonth(new Date());
            List<Expenditure> expenditureList = expenditureRepository.findByLineUserIdAndDateBetween(lineUserId, start, finish);
            manageMessageDay(replyToken, expenditureList);
        } else if (text.equalsIgnoreCase("รายจ่าย")) {
            List<QuickReplyItem> items = new ArrayList<>();
            items.add(QuickReplyItem.builder().action(new MessageAction("รายจ่ายวันนี้",
                    "รายจ่ายวันนี้")).build());
            items.add(QuickReplyItem.builder().action(new MessageAction("รายจ่ายเดือนนี้",
                    "รายจ่ายเดือนนี้")).build());
            items.add(QuickReplyItem.builder().action(new DatetimePickerAction("รายจ่ายเฉพาะวัน",
                    "date",
                    "date")).build());
            QuickReply quickReply = QuickReply.items(items);
           replyMessages(replyToken, Collections.singletonList(TextMessage.builder().text("กรุณาเลือกช่วงเวลา").quickReply(quickReply).build()));
        }
    }

    public void handleFollow(FollowEvent event) throws Exception {
        String replyToken = event.getReplyToken();
        String displayName = getProfile(event.getSource().getUserId()).getDisplayName();
        List<Message> messages = new ArrayList<>();
        messages.add(new TextMessage(String.format("ยินดีต้อนรับคุณ %s สู่ไลน์รับไลน์จ่าย", displayName)));
        messages.add(new TextMessage("หากต้องการคำแนะนำในการใช้กรุณาพิมพ์ \"-help\""));
        replyMessages(replyToken, messages);

        if (!checkExits(event.getSource().getUserId())) {
            lineUserRepository.save(new LineUser(event.getSource().getUserId()));
        }
    }

    private void manageMessageDay(String replyToken, List<Expenditure> expenditureList) throws Exception {
        if (expenditureList.isEmpty()) {
            List<Message> messages = new ArrayList<>();
            messages.add(new TextMessage("ยังไม่มีรายจ่ายในช่วงเวลาดังกล่าว"));
            replyMessages(replyToken, messages);
        } else {
            Integer index = 1;
            String firstDate = dateShow.format(expenditureList.get(0).getDate());
            String res = String.format("%s\n", firstDate);
            Integer sumPrice = 0;
            for (Expenditure expenditure : expenditureList) {
                String date = dateShow.format(expenditure.getDate());
                if (!firstDate.equals(date)) {
                    firstDate = date;
                    index = 1;
                    res = String.format("%s%s\n", res, firstDate);
                }
                String numWithAc = String.format("%d. %s", index, expenditure.getActivity());
                String price = String.format("ราคา: %d บาท", expenditure.getPrice());
                String time = String.format("เวลา: %s น.", justTime.format(expenditure.getDate()));
                String line = String.format("%s\n\t- %s\n\t- %s", numWithAc, price, time);
                sumPrice = sumPrice + expenditure.getPrice();
                res = String.format("%s%s\n", res, line);
                index++;
            }
            res = String.format("%sรวม: %d บาท",res, sumPrice);
            List<Message> messages = new ArrayList<>();
            messages.add(new TextMessage(res));
            replyMessages(replyToken, messages);
        }
    }

    private Date startDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private Date finishDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.HOUR, 11);
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

    private Boolean checkExits(String lineUserId) throws Exception {
        LineUser lineUser = lineUserRepository.findByLineUserId(lineUserId);
        return lineUser != null;
    }

    private UserProfileResponse getProfile(String userId) throws Exception {
        try {
            UserProfileResponse userProfileResponse = lineMessagingClient.getProfile(userId).get();
            return userProfileResponse;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void replyMessages(String replytoken, List<Message> messages) throws Exception {
        try {
            BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replytoken, messages)).get();
        } catch (Exception ex) {
            throw ex;
        }
    }

}
