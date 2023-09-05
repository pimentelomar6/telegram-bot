package com.pimentelprojects.productivitymaster.bot;


import com.pimentelprojects.productivitymaster.models.Task;
import com.pimentelprojects.productivitymaster.models.UserEntity;
import com.pimentelprojects.productivitymaster.services.TaskService;
import com.pimentelprojects.productivitymaster.services.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
   @Value("${bot_user}")
    private String botUsername;
    @Value("${bot_token}")
    private String botToken;
    private final UserEntityService entityService;

    private final TaskService taskService;


    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {

            Message message = update.getMessage();

            User user = message.getFrom();
            Long userId = user.getId();

            String chatId = message.getChatId().toString();
            String msg = message.getText();

            switch (msg){
                case "/start":
                    try {

                        sendNotification(chatId, """
                                Puedes controlarme enviando estos comandos:

                                Tareas
                                /addtask – crear nueva tarea
                                /tasks – obtener lista de tareas\s
                                /edittask – modificar tarea creada
                                /deletetask – borrar tarea

                                Alertas
                                /addalert – crear una nueva alerta
                                """);

                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/addalert":
                    try {
                        sendNotification(chatId, """
                                Utilice este formato para crear alertas:
                                /addalert Tiempo + Texto

                                'Tiempo' puede ser uno de los siguientes:
                                - El número de segundos, minutos, horas o días para la alerta en forma de 23m, 3h, 5d o 2s respectivamente.
                                'Texto' es cualquier cosa que quieras que el bot te diga.
                                Ejemplo: /addalert 2m hacer mis deberes
                                """);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/addtask":
                    try {
                        sendNotification(chatId, """
                                Utilice este formato para crear tareas:
                                /addtask Texto

                                'Texto' es cualquier tarea que quieras guardar.
                                Ejemplo: /addtask sacar la basura
                                """);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/edittask":
                    try {
                        sendNotification(chatId, """
                                Utilice este formato para editar tareas:
                                /edittask Id + Texto

                                'Id' número de identificación de la tarea generado al consultar el listado.
                                'Texto' es cualquier cosa que quieras modificar de la tarea en cuestión.
                                Ejemplo: /edittask 2 no hacer mis deberes
                                """);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/deletetask":
                    try {
                        sendNotification(chatId, """
                                Utilice este formato para eliminar tareas:
                                /deletealert Id\s

                                'Id' número de identificación de la tarea generado al consultar el listado.
                                Ejemplo: /deletetask 2\s
                                """);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;



            }

            if (msg.startsWith("/addalert") && msg.length() > 9) {

                String alert = cleanString(9,msg);
                Matcher matcher = Pattern.compile("(\\d{1,2})([smhd])\\s(.*)").matcher(alert);


                if (matcher.matches()) {

                    int quantity = Integer.parseInt(matcher.group(1));
                    char time = matcher.group(2).charAt(0);
                    String messageAlert = matcher.group(3);

                    Calendar date = getCalendar();
                    switch (time) {
                        case 'm':

                            date.add(Calendar.MINUTE, quantity);
                            sendMessage(date.getTime(), messageAlert, chatId, new TelegramBot(entityService, taskService));
                            try {
                                sendNotification(chatId,"Alerta creada exitosamente");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case 's':

                            date.add(Calendar.SECOND, quantity);
                            sendMessage(date.getTime(), messageAlert, chatId, new TelegramBot(entityService, taskService));
                            try {
                                sendNotification(chatId,"Alerta creada exitosamente");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            break;

                        case 'h':

                            date.add(Calendar.HOUR, quantity);
                            sendMessage(date.getTime(), messageAlert, chatId, new TelegramBot(entityService, taskService));
                            try {
                                sendNotification(chatId,"Alerta creada exitosamente");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            break;

                        case 'd':

                            date.add(Calendar.DAY_OF_MONTH, quantity);
                            sendMessage(date.getTime(), messageAlert, chatId, new TelegramBot(entityService, taskService));
                            try {
                                sendNotification(chatId,"Alerta creada exitosamente");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        default:
                            try {
                                sendNotification(chatId, "No se reconoce el formato utilizado");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                    }


                } else {
                    try {
                        sendNotification(chatId, "El formato del texto introducido no es valido");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }


            }

            if (msg.startsWith("/addtask") && msg.length() > 8) {

                String taskNotSpaces = cleanString(8, msg);

                try {

                    if (entityService.existById(userId)) {

                        taskService.createTask(Task.builder().name(taskNotSpaces).userEntity(entityService.getById(userId)).build());

                    } else {

                        UserEntity entity = UserEntity.builder()
                                .id(userId)
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

            if (msg.startsWith("/edittask") && msg.length() > 9) {

                String taskNotSpaces = cleanString(9, msg);

                if (entityService.existById(userId)) {

                    Matcher edit = Pattern.compile("(\\d+)\\s(.*)").matcher(taskNotSpaces);

                    if (edit.matches()) {
                        Long taskId = Long.parseLong(edit.group(1));

                        String taskEditBuilder = edit.group(2);

                        System.out.println(taskId);
                        System.out.println(taskEditBuilder);

                        if (taskService.existById(taskId)) {

                            if (userId.equals(taskService.getTaskById(taskId).getUserEntity().getId())) {
                                Task taskEdit = taskService.getTaskById(taskId);
                                taskEdit.setName(taskEditBuilder);
                                taskService.createTask(taskEdit);

                                try {
                                    sendNotification(chatId, "Se ha editado la tarea exitosamente");
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }

                            } else {
                                try {
                                    sendNotification(chatId, "Error al editar la tarea con el id proporcionado");
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else {
                            try {
                                sendNotification(chatId, "El id de la tarea no esta registrado");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        try {
                            sendNotification(chatId, "Para editar una tarea escriba /edittask seguido del id de la tarea mas el texto por el cual sera reemplazado");
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }


                } else {

                    try {
                        sendNotification(chatId, "No se ha creado ninguna tarea con tu usuario");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                }


            }

            if (msg.startsWith("/deletetask") && msg.length() > 11) {

                String taskDelete = msg.substring(11).trim();

                if (entityService.existById(userId)) {

                    if (taskDelete.matches("^\\d")) {

                        Long taskId = Long.parseLong(taskDelete);


                        if (taskService.existById(taskId)) {

                            if (userId.equals(taskService.getTaskById(taskId).getUserEntity().getId())) {
                                taskService.deleteTaskById(taskId);

                                try {
                                    sendNotification(chatId, "Se ha eliminado la tarea exitosamente");
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }

                            } else {
                                try {
                                    sendNotification(chatId, "Error al eliminar la tarea con el id proporcionado");
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else {
                            try {
                                sendNotification(chatId, "El id de la tarea no esta registrado");
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        try {
                            sendNotification(chatId, "Para eliminar una tarea escriba /deletetask seguido del id de la tarea");
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }


                } else {

                    try {
                        sendNotification(chatId, "No se ha creado ninguna tarea con tu usuario");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                }


            }

            if (msg.startsWith("/tasks")) {
                if (entityService.existById(userId)) {
                    List<Task> usersTask = taskService.getAllTask(entityService.getById(userId));
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

    private String cleanString(int start, String msg){
        return msg.substring(start).replaceAll("\\s+", " ").trim();
    }


    public void sendNotification(String chatId, String msg) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId, msg);
        execute(sendMessage);


    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }


    @Override
    public String getBotToken() {
        return botToken;
    }
}
