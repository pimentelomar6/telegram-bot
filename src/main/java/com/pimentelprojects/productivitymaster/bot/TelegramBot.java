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
            Long lons = user.getId();

            String chatId = message.getChatId().toString();
            String msg = message.getText();

            if (msg.startsWith("/start")) {
                try {

                    sendNotification(chatId, "Puedes controlarme enviando estos comandos:\n" +
                            "\n" +
                            "Tareas\n" +
                            "/addtask – crear nueva tarea\n" +
                            "/tasks – obtener lista de tareas \n" +
                            "/edittask – modificar tarea creada\n" +
                            "/deletetask – borrar tarea\n" +
                            "\n" +
                            "Alertas\n" +
                            "/addalert – crear una nueva alerta\n");

                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            }

            if(msg.equals("/addalert")){
                try {
                    sendNotification(chatId,"Utilice este formato para crear alertas:\n" +
                            "/addalert Tiempo + Texto\n" +
                            "\n" +
                            "'Tiempo' puede ser uno de los siguientes:\n" +
                            "- El número de segundos, minutos, horas o días para la alerta en forma de 23m, 3h, 5d o 2s respectivamente.\n" +
                            "'Texto' es cualquier cosa que quieras que el bot te diga.\n" +
                            "Ejemplo: /addalert 2m hacer mis deberes\n");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if(msg.equals("/addtask")){
                try {
                    sendNotification(chatId,"Utilice este formato para crear tareas:\n" +
                            "/addtask Texto\n" +
                            "\n" +
                            "'Texto' es cualquier tarea que quieras guardar.\n" +
                            "Ejemplo: /addtask sacar la basura\n");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if(msg.equals("/edittask")){
                try {
                    sendNotification(chatId,"Utilice este formato para editar tareas:\n" +
                            "/edittask Id + Texto\n" +
                            "\n" +
                            "'Id' número de identificación de la tarea generado al consultar el listado.\n" +
                            "'Texto' es cualquier cosa que quieras modificar de la tarea en cuestión.\n" +
                            "Ejemplo: /edittask 2 no hacer mis deberes\n");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if(msg.equals("/deletetask")){
                try {
                    sendNotification(chatId,"Utilice este formato para eliminar tareas:\n" +
                            "/deletealert Id \n" +
                            "\n" +
                            "'Id' número de identificación de la tarea generado al consultar el listado.\n" +
                            "Ejemplo: /deletetask 2 \n");
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
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService, taskService));
                            break;
                        case 's':

                            date.add(Calendar.SECOND, cantidad);
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService, taskService));
                            break;

                        case 'h':

                            date.add(Calendar.HOUR, cantidad);
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService, taskService));
                            break;

                        case 'd':

                            date.add(Calendar.DAY_OF_MONTH, cantidad);
                            sendMessage(date.getTime(), stringBuilder.toString(), chatId, new TelegramBot(entityService, taskService));
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

                String task = msg.substring(8).trim();
                String taskNotSpaces = task.replaceAll("\\s+", " ");


                try {

                    if (entityService.existById(lons)) {

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

            if (msg.startsWith("/edittask") && msg.length() > 9) {

                String task = msg.substring(9).trim();
                System.out.println(task);
                String taskNotSpaces = task.replaceAll("\\s+", " ");
                System.out.println(taskNotSpaces);

                if (entityService.existById(lons)) {
                    Pattern pattern = Pattern.compile("^\\d\\s.*$");
                    Matcher matcher = pattern.matcher(taskNotSpaces);

                    if (matcher.matches()) {
                        String[] taskToArray = taskNotSpaces.split(" ");

                        Long taskId = Long.parseLong(taskToArray[0]);

                        StringBuilder taskEditBuilder = new StringBuilder();

                        for (int i = 1; i < taskToArray.length; i++) {
                            taskEditBuilder.append(taskToArray[i]);
                            if (!(i == taskToArray.length - 1)) {
                                taskEditBuilder.append(" ");
                            }
                        }
                        if (taskService.existById(taskId)) {

                            if (lons.equals(taskService.getTaskById(taskId).getUserEntity().getId())) {
                                Task taskEdit = taskService.getTaskById(taskId);
                                taskEdit.setName(taskEditBuilder.toString());
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


                if (entityService.existById(lons)) {
                    Pattern pattern = Pattern.compile("^\\d");
                    Matcher matcher = pattern.matcher(taskDelete);

                    if (matcher.matches()) {

                        Long taskId = Long.parseLong(taskDelete);


                        if (taskService.existById(taskId)) {

                            if (lons.equals(taskService.getTaskById(taskId).getUserEntity().getId())) {
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
