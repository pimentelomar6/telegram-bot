package com.pimentelprojects.productivitymaster.bot;


import com.pimentelprojects.productivitymaster.models.Task;
import com.pimentelprojects.productivitymaster.models.UserEntity;
import com.pimentelprojects.productivitymaster.services.TaskService;
import com.pimentelprojects.productivitymaster.services.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pimentelprojects.productivitymaster.bot.ScheduledMessage.sendMessage;


@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final UserEntityService entityService;

    private final TaskService taskService;


    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {

            Message message = update.getMessage();

            User user = message.getFrom();
            System.out.println(user);
            Long lons = user.getId();
            System.out.println(lons);

            String chatId = message.getChatId().toString();
            String msg = message.getText();

            if (msg.startsWith("/start")) {
                try {

                    sendNotification(chatId, "mensaje generico de bienvenida");

                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            }

            if (msg.startsWith("/addalert") && msg.length() > 9) {
                // Limpiar string antes de cada comprobacion

                String alert = msg.substring(9).trim();
                String alertNotSpaces = alert.replaceAll("\\s+", " ");

                Pattern pattern = Pattern.compile("^\\d{1,2}[smhd]\\s.*$");
                Matcher matcher = pattern.matcher(alertNotSpaces);


                if (matcher.matches()) {
                    String[] alertToArray = alertNotSpaces.split(" ");

                    char time = alertToArray[0].charAt(alertToArray[0].length() - 1);
                    int cantidad = Integer.parseInt(alertToArray[0].substring(0, alertToArray[0].length() - 1));

                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 1; i < alertToArray.length; i++) {
                        stringBuilder.append(alertToArray[i]);
                        if (!(i == alertToArray.length - 1)) {
                            stringBuilder.append(" ");
                        }
                    }

                    Calendar date = getCalendar();
                    switch (time) {
                        case 'm':

                            date.add(Calendar.MINUTE, cantidad);
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService,taskService));
                            break;
                        case 's':

                            date.add(Calendar.SECOND, cantidad);
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService,taskService));
                            break;

                        case 'h':

                            date.add(Calendar.HOUR, cantidad);
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService,taskService));
                            break;

                        case 'd':

                            date.add(Calendar.DAY_OF_MONTH, cantidad);
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService, taskService));
                            break;
                        default:
                            try {
                                sendNotification(chatId, "error");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                    }


                } else {
                    try {
                        sendNotification(chatId, "pattern incorrect");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }


            }

            if (msg.startsWith("/addtask") && msg.length() > 8) {

                String task = msg.substring(8).trim();
                String taskNotSpaces = task.replaceAll("\\s+", " ");


                try {

                    if (entityService.existById(lons)) {

//                        UserEntity entity2 = entityService.getById(lons);
//                        List<Task> taskList = entity2.getTasks();
//                        taskList.add(Task.builder().name(taskNotSpaces).userEntity(entity2).build());
//                        entity2.setTasks(taskList);
//                        entityService.createUser(entity2);

                        taskService.createTask(Task.builder().name(taskNotSpaces).userEntity(entityService.getById(lons)).build());

                    } else {


                        UserEntity entity = UserEntity.builder()
                                .id(lons)
                                .username(user.getUserName())
                                .build();

                        entityService.createUser(entity);

                        taskService.createTask(Task.builder().name(taskNotSpaces).userEntity(entity).build());


                    }

                    sendNotification(chatId, "Tarea creada exitosamente");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            }

            if (msg.startsWith("/tasks")) {
                if (entityService.existById(lons)) {
                    List<Task> usersTask = taskService.getAllTask(entityService.getById(lons));
                    StringBuilder taskToString = new StringBuilder();

                    for (Task task : usersTask) {
                        taskToString.append(task.getId()).append(": ").append(task.getName()).append("\n");
                    }

                    try {
                        sendNotification(chatId, taskToString.toString());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    try {
                        sendNotification(chatId, "No se ha agregado ninguna tarea");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }

    }

    private Calendar getCalendar() {
        Date date1 = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        return date;
    }


    public void sendNotification(String chatId, String msg) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId, msg);
        execute(sendMessage);


    }


    @Override
    public String getBotUsername() {
        return "TaskMaster_bot";
    }


    @Override
    public String getBotToken() {
        return "6234177435:AAE5HX_XxJNDUaMbJO8ZJ-urqyQbmcK-1x8";
    }
}
