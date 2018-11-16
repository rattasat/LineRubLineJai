package com.me.linerublinejai.services;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.component.*;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.unit.FlexAlign;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;
import com.me.linerublinejai.models.DatePeriod;
import com.me.linerublinejai.models.entities.Expenses;
import com.me.linerublinejai.models.entities.LineUser;
import com.me.linerublinejai.models.repositories.LineUserRepository;
import com.me.linerublinejai.types.ModeType;
import com.me.linerublinejai.utils.Constants;
import com.me.linerublinejai.utils.Emoji;
import com.me.linerublinejai.utils.Messages;
import com.me.linerublinejai.utils.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LineWebHookService {

    @Autowired
    private ExpensesService expensesService;

    @Autowired
    private LineUserRepository lineUserRepository;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    private SimpleDateFormat datePattern = new SimpleDateFormat(Constants.DATE_PATTERN);
    private SimpleDateFormat dayModeMessage = new SimpleDateFormat(Constants.DAY_MODE_PATTERN);
    private SimpleDateFormat monthModeMessage = new SimpleDateFormat(Constants.MONTH_MODE_PATTERN);
    private SimpleDateFormat dayOfMonthMessage = new SimpleDateFormat(Constants.DAY_OF_MONTH_PATTERN);

    public void textMessageEvent(String userId, String replyToken, String text) throws Exception {
        DatePeriod datePeriod;
        List<Expenses> expensesList;
        switch (text) {
            case Mode.BY_THIS_DAY:
                datePeriod = new DatePeriod(new Date(), ModeType.THIS_DAY);
                expensesList = expensesService.getExpenses(userId, datePeriod.getStart(), datePeriod.getFinish());
                if (expensesList.isEmpty()) {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(Messages.NOT_FOUND_EXPENDITURE)));
                } else {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, setFlexMessage(expensesList, ModeType.THIS_DAY)));
                }
                break;
            case Mode.BY_THIS_MONTH:
                datePeriod = new DatePeriod(new Date(), ModeType.THIS_MONTH);
                expensesList = expensesService.getExpenses(userId, datePeriod.getStart(), datePeriod.getFinish());
                if (expensesList.isEmpty()) {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(Messages.NOT_FOUND_EXPENDITURE)));
                } else {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, setFlexMessage(expensesList, ModeType.THIS_MONTH)));
                }
                break;
            case Mode.SELECT_MODE:
                lineMessagingClient.replyMessage(new ReplyMessage(replyToken, setFlexMessageForSelectMode()));
                break;
            case Mode.HELP:
                List<Message> messages = new ArrayList<>();
                messages.add(new TextMessage(Messages.HOW_TO_USE_1));
                messages.add(new TextMessage(Messages.HOW_TO_USE_2));
                messages.add(new TextMessage(Messages.HOW_TO_USE_3));
                lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages));
                break;
            default:
                if (text.trim().matches(Mode.REGEX_ADD)) {
                    String activity = text.split(" /")[0];
                    Integer price = Integer.parseInt(text.split(" /")[1]);
                    try {
                        expensesService.saveExpenses(new Expenses(userId, activity, new Date(), price));
                        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(Messages.ADD_EXPENDITURE_SUCCESS)));
                    } catch (Exception ex) {
                        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(Messages.SOMETHING_WRONG)));
                    }
                } else {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(Messages.SHOULD_CORRECT_MODE)));
                }
                break;
        }
    }

    public void handleFollow(String userId, String replyToken) throws Exception {
        String displayName = lineMessagingClient.getProfile(userId).get().getDisplayName();
        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, Arrays.asList(new TextMessage(String.format(Messages.WELCOME_1, displayName)), new TextMessage(String.format(Messages.WELCOME_2, Mode.HELP)))));

        if (lineUserRepository.findByLineUserId(userId) == null) {
            lineUserRepository.save(new LineUser(userId));
        }
    }

    public void handlePostbackEvent(String data, Map<String, String> params, String userId, String replyToken) throws Exception {
        DatePeriod datePeriod;
        List<Expenses> expensesList;

        switch (data) {
            case Constants.DATE:
                String date = params.get("date");
                datePeriod = new DatePeriod(datePattern.parse(date), ModeType.THIS_DAY);
                expensesList = expensesService.getExpenses(userId, datePeriod.getStart(), datePeriod.getFinish());
                if (expensesList.isEmpty()) {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(Messages.NOT_FOUND_EXPENDITURE)));
                } else {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, setFlexMessage(expensesList, ModeType.THIS_DAY)));
                }
                break;
            case Constants.MONTH:
                lineMessagingClient.replyMessage(new ReplyMessage(replyToken, setFlexMessageMonth()));
                break;
            default:
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());
                calendar.set(Calendar.MONTH, Integer.parseInt(data));
                datePeriod = new DatePeriod(calendar.getTime(), ModeType.THIS_MONTH);
                expensesList = expensesService.getExpenses(userId, datePeriod.getStart(), datePeriod.getFinish());
                if (expensesList.isEmpty()) {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, new TextMessage(Messages.NOT_FOUND_EXPENDITURE)));
                } else {
                    lineMessagingClient.replyMessage(new ReplyMessage(replyToken, setFlexMessage(expensesList, ModeType.THIS_MONTH)));
                }
        }
    }

    public void multicastExpensesMessage(Short time) throws Exception {
        String message = "";
        switch (time) {
            case 1:
                message = Messages.ADD_EXPENSES_1PM;
                break;
            case 8:
                message = Messages.ADD_EXPENSES_8PM;
                break;
        }

        List<LineUser> userList = lineUserRepository.findAll();
        if (!userList.isEmpty()) {
            Set<String> lineUserIdSet = new HashSet<>();
            userList.forEach(lineUser -> lineUserIdSet.add(lineUser.getLineUserId()));
            lineMessagingClient.multicast(new Multicast(lineUserIdSet, new TextMessage(message + String.valueOf(Character.toChars(Emoji.PLEASE)))));
        }
    }

    private FlexMessage setFlexMessageForSelectMode() {
        String altText = ModeType.SELECT_MODE.getLabel();
        List<FlexComponent> flexComponents = Arrays.asList(
                Button
                        .builder()
                        .action(new DatetimePickerAction(
                                ModeType.SELECT_DATE.getLabel(),
                                "date",
                                Constants.DATE,
                                datePattern.format(new Date()),
                                datePattern.format(new Date()),
                                Constants.MIN_DATE
                        ))
                        .style(Button.ButtonStyle.PRIMARY)
                        .build(),
                Button
                        .builder()
                        .action(PostbackAction
                                .builder()
                                .label(ModeType.SELECT_MONTH.getLabel())
                                .data("month")
                                .build())
                        .margin(FlexMarginSize.MD)
                        .style(Button.ButtonStyle.PRIMARY)
                        .build()
        );

        Bubble bubble = Bubble
                .builder()
                .body(Box
                        .builder()
                        .layout(FlexLayout.VERTICAL)
                        .contents(flexComponents)
                        .build())
                .build();
        return new FlexMessage(altText, bubble);
    }

    private FlexMessage setFlexMessageMonth() {
        String altText = ModeType.SELECT_MONTH.getLabel();
        LinkedHashMap<String, String> month = new LinkedHashMap<>();
        month.put("มกราคม", "0");
        month.put("กุมภาพันธ์", "1");
        month.put("มีนาคม", "2");
        month.put("เมษายน", "3");
        month.put("พฤษภาคม", "4");
        month.put("มิถุนายน", "5");
        month.put("กรกฎาคม", "6");
        month.put("สิงหาคม", "7");
        month.put("กันยายน", "8");
        month.put("ตุลาคม", "9");
        month.put("พฤศจิกายน", "10");
        month.put("ธันวาคม", "11");

        List<FlexComponent> flexComponents = new ArrayList<>();

        for (String key : month.keySet()) {
            flexComponents.add(Button
                    .builder()
                    .action(PostbackAction
                            .builder()
                            .label(key)
                            .data(month.get(key))
                            .build())
                    .margin(FlexMarginSize.SM)
                    .style(Button.ButtonStyle.PRIMARY)
                    .height(Button.ButtonHeight.SMALL)
                    .build());
        }

        Bubble bubble = Bubble
                .builder()
                .body(Box
                        .builder()
                        .layout(FlexLayout.VERTICAL)
                        .contents(flexComponents)
                        .build())
                .build();
        return new FlexMessage(altText, bubble);
    }

    private FlexMessage setFlexMessage(List<Expenses> expensesList, ModeType mode) throws Exception {
        String altText = Messages.ALT_TEXT_EXPENDITURE;
        String header1 = Messages.EXPENDITURE;
        String header2;
        List<FlexComponent> flexComponents = new ArrayList<>();
        Integer sumActivity = 0;
        Integer sumPrice = 0;
        switch (mode) {
            case THIS_DAY:
                header2 = dayModeMessage.format(expensesList.get(0).getDate());
                flexComponents = createHeader(header1, header2);
                flexComponents.add(Separator.builder().margin(FlexMarginSize.MD).build());
                for (Expenses expenses : expensesList) {
                    flexComponents.add(createBoxActivity(expenses.getName(), String.format(Messages.PRICE_BATH, expenses.getPrice())));
                    sumPrice += expenses.getPrice();
                    sumActivity++;
                }
                flexComponents.add(Separator.builder().margin(FlexMarginSize.MD).build());
                flexComponents.add(createFooter(String.format(Messages.RESULT, sumActivity), String.format(Messages.PRICE_BATH, sumPrice)));
                break;
            case THIS_MONTH:
                header2 = monthModeMessage.format(expensesList.get(0).getDate());
                flexComponents = createHeader(header1, header2);
                flexComponents.add(Separator.builder().margin(FlexMarginSize.MD).build());
                Date date = expensesList.get(0).getDate();
                String dateCompare = datePattern.format(date);
                Integer sumAllPrice = 0;
                for (Expenses expenses : expensesList) {
                    if (dateCompare.equals(datePattern.format(expenses.getDate()))) {
                        sumPrice += expenses.getPrice();
                    } else {
                        flexComponents.add(createBoxActivity(String.format(Messages.DAY_OF_MOTH_AT, dayOfMonthMessage.format(date)), String.format(Messages.PRICE_BATH, sumPrice)));
                        sumPrice = expenses.getPrice();
                        date = expenses.getDate();
                        dateCompare = datePattern.format(date);
                        sumActivity++;
                    }
                    sumAllPrice += expenses.getPrice();
                }
                flexComponents.add(createBoxActivity(String.format(Messages.DAY_OF_MOTH_AT, dayOfMonthMessage.format(date)), String.format(Messages.PRICE_BATH, sumPrice)));
                flexComponents.add(Separator.builder().margin(FlexMarginSize.MD).build());
                flexComponents.add(createFooter(String.format(Messages.RESULT, sumActivity + 1), String.format(Messages.PRICE_BATH, sumAllPrice)));
                break;
        }
        Bubble bubble = Bubble
                .builder()
                .body(Box
                        .builder()
                        .layout(FlexLayout.VERTICAL)
                        .contents(flexComponents)
                        .build())
                .build();
        return new FlexMessage(altText, bubble);
    }

    private List<FlexComponent> createHeader(String header1, String header2) {
        List<FlexComponent> flexComponents = new ArrayList<>();
        flexComponents.add(Text
                .builder()
                .text(header1)
                .size(FlexFontSize.LG)
                .weight(Text.TextWeight.BOLD)
                .color(Constants.HEADER_TEXT_COLOR)
                .build());
        flexComponents.add(Text
                .builder()
                .text(header2)
                .size(FlexFontSize.XL)
                .weight(Text.TextWeight.BOLD)
                .color(Constants.HEADER_DATE_TEXT_COLOR)
                .margin(FlexMarginSize.MD).build());
        return flexComponents;
    }

    private Box createBoxActivity(String left, String right) {
        Text activityText = Text
                .builder()
                .text(left)
                .color(Constants.ACTIVITY_TEXT_COLOR)
                .align(FlexAlign.START)
                .build();
        Text priceText = Text
                .builder()
                .text(right)
                .color(Constants.PRICE_TEXT_COLOR)
                .align(FlexAlign.END)
                .build();
        return Box
                .builder()
                .layout(FlexLayout.HORIZONTAL)
                .margin(FlexMarginSize.MD)
                .contents(Arrays.asList(activityText, priceText))
                .build();
    }

    private Box createFooter(String left, String right) {
        Text resultText = Text
                .builder()
                .text(left)
                .color(Constants.FOOTER_TEXT_COLOR)
                .align(FlexAlign.START)
                .build();

        Text sumPriceText = Text
                .builder()
                .text(right)
                .color(Constants.FOOTER_TEXT_COLOR)
                .align(FlexAlign.END)
                .build();
        return Box
                .builder()
                .layout(FlexLayout.HORIZONTAL)
                .margin(FlexMarginSize.SM)
                .contents(Arrays.asList(resultText, sumPriceText))
                .build();
    }

}
